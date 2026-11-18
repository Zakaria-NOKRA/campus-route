// Campus graph generator.
// Reads campus.source.mjs (rooms as rectangles, corridors as polylines, stairs, elevators,
// building doors, outdoor points), builds the navigation graph (nodes, edges, weights),
// validates it, then writes the JSON file imported by the back-end.
//
// Usage (from the repository root):
//   node tools/campus/generate.mjs                    write backend/src/main/resources/data/campus_route_graph.json
//   node tools/campus/generate.mjs --out <file>       write somewhere else
//   node tools/campus/generate.mjs --check            only check that the committed JSON is up to date
//   node tools/campus/generate.mjs --preview <dir>    also draw one SVG per building and floor (dir outside the repo)
//
// Zero dependency: only Node built-in modules.

import { existsSync, mkdirSync, readFileSync, writeFileSync } from 'node:fs'
import path from 'node:path'
import { fileURLToPath, pathToFileURL } from 'node:url'
import { parseArgs } from 'node:util'

import { validate } from './validate.mjs'

const TOOL_DIR = path.dirname(fileURLToPath(import.meta.url))
const REPO_ROOT = path.resolve(TOOL_DIR, '..', '..')
const SOURCE_FILE = path.join(TOOL_DIR, 'campus.source.mjs')
const DEFAULT_OUT = path.join(REPO_ROOT, 'backend', 'src', 'main', 'resources', 'data', 'campus_route_graph.json')

// Code of the outdoor "plan space" (courtyard, paths). Buildings use their letter.
export const OUTDOOR = 'EXT'
// Id of the main campus entrance. It keeps its historic id: the front-end uses it as default start.
export const ENTRANCE_ID = 'ENTREE'

// ---------------------------------------------------------------- geometry

export function distance(p, q) {
  return Math.hypot(q[0] - p[0], q[1] - p[1])
}

// Closest point to p on the segment [a, b].
// t = position along the segment: 0 at a, 1 at b (clamped, so the point never leaves the segment).
export function projectOnSegment(p, a, b) {
  const dx = b[0] - a[0]
  const dy = b[1] - a[1]
  const raw = ((p[0] - a[0]) * dx + (p[1] - a[1]) * dy) / (dx * dx + dy * dy)
  const t = Math.min(1, Math.max(0, raw))
  return { t, point: [a[0] + t * dx, a[1] + t * dy] }
}

// Door of a rectangle [x, y, w, h]: a point on the wall `side` (N, S, E or W),
// at `fraction` of that wall (0.5 = middle). Returns null for an unknown side.
export function doorOnRect([x, y, w, h], side, fraction = 0.5) {
  if (side === 'N') return [x + w * fraction, y]
  if (side === 'S') return [x + w * fraction, y + h]
  if (side === 'W') return [x, y + h * fraction]
  if (side === 'E') return [x + w, y + h * fraction]
  return null
}

function roundPoint(p) {
  return [Math.round(p[0]), Math.round(p[1])]
}

// Polygon as an SVG "points" string, optionally moved by an offset: "x1,y1 x2,y2 ..."
function toPoints(points, [ox, oy] = [0, 0]) {
  return points.map(([x, y]) => `${x + ox},${y + oy}`).join(' ')
}

function byKey(key) {
  return (a, b) => (key(a) < key(b) ? -1 : key(a) > key(b) ? 1 : 0)
}

// ---------------------------------------------------------------- graph building

/**
 * Builds the graph from the source object.
 * Returns { graph, errors }: errors found while reading the source (unknown junction, unknown
 * room type...). Checks on the finished graph are done by validate().
 */
export function buildGraph(source) {
  const errors = []
  const nodes = []
  const edges = []
  const rooms = []
  const nodeById = new Map()

  // Duplicates are kept on purpose: validate() reports them.
  function addNode(node) {
    nodes.push(node)
    if (!nodeById.has(node.id)) nodeById.set(node.id, node)
    return node
  }

  function addEdge(a, b, w, kind) {
    edges.push({ a, b, w, kind })
  }

  for (const building of source.buildings) {
    const floors = Object.keys(building.floors).map(Number).sort((x, y) => x - y)
    for (const floor of floors) {
      buildFloor(building, floor)
    }
    // Vertical links between consecutive floors served by each stair core / elevator
    for (const core of building.stairs ?? []) {
      linkFloors(building.code, core, floors, 'stairs', source.vertical.stairs)
    }
    for (const core of building.elevators ?? []) {
      linkFloors(building.code, core, floors, 'elevator', source.vertical.elevator)
    }
  }

  buildOutdoor()

  // ---- one floor of one building
  function buildFloor(building, floor) {
    const b = building.code
    const prefix = `${b}${floor}-` // e.g. "A1-"
    const plan = building.floors[floor]
    const corridorNodes = [] // corridor nodes of this floor, to reuse an anchor at the same place

    // 1. Junction nodes and corridor segments
    const segments = [] // { pa, pb, anchors: [{ id, t }] }
    for (const line of plan.corridors ?? []) {
      for (const name of line) {
        const point = building.junctions?.[name]
        const id = prefix + name
        if (!point) {
          errors.push(`${b}${floor}: corridor uses unknown junction "${name}"`)
        } else if (!nodeById.has(id)) {
          corridorNodes.push(addNode({ id, building: b, floor, x: point[0], y: point[1], type: 'corridor', label: 'Couloir' }))
        }
      }
      for (let i = 1; i < line.length; i++) {
        const pa = building.junctions?.[line[i - 1]]
        const pb = building.junctions?.[line[i]]
        if (!pa || !pb) continue
        if (distance(pa, pb) === 0) {
          errors.push(`${b}${floor}: zero-length corridor segment ${line[i - 1]}-${line[i]}`)
          continue
        }
        segments.push({ pa, pb, anchors: [{ id: prefix + line[i - 1], t: 0 }, { id: prefix + line[i], t: 1 }] })
      }
    }

    // 2. Everything that has a door on the corridor: rooms, stairs, elevators, building doors
    const owners = []
    for (const tuple of plan.rooms ?? []) {
      const owner = readRoom(building, floor, tuple)
      if (owner) owners.push(owner)
    }
    for (const core of building.stairs ?? []) {
      if (serves(core, floor)) {
        owners.push({ id: prefix + core.id, type: 'stairs', label: `Escalier ${core.id}`, door: doorOnRect(core.rect, core.door, core.at) })
      }
    }
    for (const core of building.elevators ?? []) {
      if (serves(core, floor)) {
        owners.push({ id: prefix + core.id, type: 'elevator', label: 'Ascenseur', door: doorOnRect(core.rect, core.door, core.at) })
      }
    }
    for (const door of building.doors ?? []) {
      if (door.floor === floor) {
        owners.push({ id: prefix + door.id, type: 'entrance', label: door.label, door: door.at })
      }
    }

    // 3. Each door gets a node, linked to the closest point of the corridor (its "anchor")
    for (const owner of owners) {
      if (!owner.door) {
        errors.push(`${owner.id}: invalid door side`)
        continue
      }
      const door = roundPoint(owner.door)
      addNode({ id: owner.id, building: b, floor, x: door[0], y: door[1], type: owner.type, label: owner.label })
      if (segments.length === 0) {
        errors.push(`${owner.id}: no corridor on floor ${floor} of building ${b}`)
        continue
      }

      let best = null
      for (const segment of segments) {
        const { t, point } = projectOnSegment(door, segment.pa, segment.pb)
        const d = distance(door, point)
        if (!best || d < best.d) best = { segment, t, point: roundPoint(point), d }
      }

      // Reuse a corridor node already at this place (a junction, or the anchor of a facing door)
      let anchor = corridorNodes.find((n) => n.x === best.point[0] && n.y === best.point[1])
      if (!anchor) {
        anchor = addNode({ id: `${owner.id}_C`, building: b, floor, x: best.point[0], y: best.point[1], type: 'corridor', label: 'Couloir' })
        corridorNodes.push(anchor)
      }
      if (!best.segment.anchors.some((a) => a.id === anchor.id)) {
        best.segment.anchors.push({ id: anchor.id, t: best.t })
      }
      addEdge(owner.id, anchor.id, Math.round(distance(door, best.point)), 'walk')
    }

    // 4. Cut every segment at its anchors, in order along the segment
    for (const segment of segments) {
      const ordered = [...segment.anchors].sort((x, y) => x.t - y.t)
      for (let i = 1; i < ordered.length; i++) {
        const a = nodeById.get(ordered[i - 1].id)
        const c = nodeById.get(ordered[i].id)
        if (a.id !== c.id) addEdge(a.id, c.id, Math.round(distance([a.x, a.y], [c.x, c.y])), 'walk')
      }
    }
  }

  // ---- [code, x, y, w, h, doorSide, typeKey, overrides?] -> room + door owner
  function readRoom(building, floor, [code, x, y, w, h, side, typeKey, overrides = {}]) {
    const preset = source.types[typeKey]
    if (!preset) {
      errors.push(`${code}: unknown room type "${typeKey}"`)
      return null
    }
    rooms.push({
      code,
      building: building.code,
      floor,
      name: overrides.name ?? `${preset.type} ${code}`,
      type: preset.type,
      tag: overrides.tag ?? preset.tag,
      cap: overrides.cap ?? preset.cap,
      eq: overrides.eq ?? preset.eq,
      x,
      y,
      w,
      h,
    })
    return { id: code, type: 'room', label: code, door: doorOnRect([x, y, w, h], side, overrides.door) }
  }

  function serves(core, floor) {
    return !core.floors || core.floors.includes(floor)
  }

  function linkFloors(b, core, floors, kind, weight) {
    const served = floors.filter((f) => serves(core, f))
    for (let i = 1; i < served.length; i++) {
      addEdge(`${b}${served[i - 1]}-${core.id}`, `${b}${served[i]}-${core.id}`, weight, kind)
    }
  }

  // ---- courtyard, outdoor paths and links between building doors and the outside
  function buildOutdoor() {
    const site = source.outdoor
    const outdoorId = (name) => (name === ENTRANCE_ID ? name : `${OUTDOOR}-${name}`)

    for (const [name, [x, y, label, kind]] of Object.entries(site.points)) {
      addNode({ id: outdoorId(name), building: OUTDOOR, floor: 0, x, y, type: kind === 'entrance' ? 'entrance' : 'outdoor', label: label ?? name })
    }
    for (const [from, to, w, kind = 'walk'] of site.paths) {
      if (!(from in site.points) || !(to in site.points)) {
        errors.push(`outdoor path ${from}-${to}: unknown point`)
        continue
      }
      addEdge(outdoorId(from), outdoorId(to), w, kind)
    }
    for (const building of source.buildings) {
      for (const door of building.doors ?? []) {
        if (!(door.outdoor in site.points)) {
          errors.push(`${building.code}${door.floor}-${door.id}: unknown outdoor point "${door.outdoor}"`)
          continue
        }
        addEdge(`${building.code}${door.floor}-${door.id}`, outdoorId(door.outdoor), door.w, 'walk')
      }
    }
  }

  // ---- buildings list: the real buildings, then the outdoor space
  const buildings = source.buildings.map((b) => ({
    code: b.code,
    name: b.name,
    outdoor: false,
    width: b.size[0],
    height: b.size[1],
    origin: b.origin,
    outline: toPoints(b.outline),
    siteOutline: toPoints(b.outline, b.origin),
  }))
  const [siteWidth, siteHeight] = source.outdoor.size
  const siteRect = [[0, 0], [siteWidth, 0], [siteWidth, siteHeight], [0, siteHeight]]
  buildings.push({
    code: OUTDOOR,
    name: source.outdoor.name,
    outdoor: true,
    width: siteWidth,
    height: siteHeight,
    origin: [0, 0],
    outline: toPoints(siteRect),
    siteOutline: toPoints(siteRect),
  })

  // Stable order, so that a regenerated file gives a readable git diff
  const graph = {
    scale_m_per_px: source.scale,
    buildings,
    rooms: rooms.sort(byKey((r) => r.code)),
    nodes: nodes.sort(byKey((n) => n.id)),
    edges: edges.sort(byKey((e) => `${e.a}|${e.b}`)),
  }
  return { graph, errors }
}

// ---------------------------------------------------------------- SVG preview (debug only)

function escapeXml(text) {
  return String(text).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
}

const EDGE_COLORS = { walk: '#888', stairs: '#d33', elevator: '#36c' }

function previewSvg(graph, building, floor) {
  const here = (n) => n.building === building.code && n.floor === floor
  const nodes = graph.nodes.filter(here)
  const ids = new Set(nodes.map((n) => n.id))
  const byId = new Map(nodes.map((n) => [n.id, n]))
  const parts = [`<polygon points="${building.outline}" fill="#f7f7f2" stroke="#333" stroke-width="4"/>`]

  if (building.outdoor) {
    for (const other of graph.buildings.filter((b) => !b.outdoor)) {
      parts.push(`<polygon points="${other.siteOutline}" fill="#ddd" stroke="#333" stroke-width="3"/>`)
      parts.push(`<text x="${other.origin[0] + 20}" y="${other.origin[1] + 60}" font-size="50">${other.code}</text>`)
    }
  }
  for (const r of graph.rooms.filter(here)) {
    parts.push(`<rect x="${r.x}" y="${r.y}" width="${r.w}" height="${r.h}" fill="#fff" stroke="#555" stroke-width="2"/>`)
    parts.push(`<text x="${r.x + 6}" y="${r.y + 20}" font-size="16">${escapeXml(r.code)}</text>`)
  }
  for (const e of graph.edges.filter((e) => ids.has(e.a) && ids.has(e.b))) {
    const a = byId.get(e.a)
    const b = byId.get(e.b)
    parts.push(`<line x1="${a.x}" y1="${a.y}" x2="${b.x}" y2="${b.y}" stroke="${EDGE_COLORS[e.kind]}" stroke-width="3"/>`)
  }
  for (const n of nodes) {
    parts.push(`<circle cx="${n.x}" cy="${n.y}" r="4" fill="#222"/>`)
    parts.push(`<text x="${n.x + 5}" y="${n.y - 5}" font-size="10" fill="#a33">${escapeXml(n.id)}</text>`)
  }
  const w = building.width + 40
  const h = building.height + 40
  return `<svg xmlns="http://www.w3.org/2000/svg" viewBox="-20 -20 ${w} ${h}" width="${w}" height="${h}" font-family="sans-serif">\n${parts.join('\n')}\n</svg>\n`
}

function writePreviews(graph, dir) {
  mkdirSync(dir, { recursive: true })
  for (const building of graph.buildings) {
    const floors = [...new Set(graph.nodes.filter((n) => n.building === building.code).map((n) => n.floor))].sort()
    for (const floor of floors) {
      writeFileSync(path.join(dir, `${building.code}${floor}.svg`), previewSvg(graph, building, floor))
    }
  }
}

// ---------------------------------------------------------------- command line

function printSummary(graph) {
  for (const building of graph.buildings) {
    const floors = [...new Set(graph.nodes.filter((n) => n.building === building.code).map((n) => n.floor))].sort()
    const details = floors.map((f) => {
      const rooms = graph.rooms.filter((r) => r.building === building.code && r.floor === f).length
      const nodes = graph.nodes.filter((n) => n.building === building.code && n.floor === f).length
      return `floor ${f}: ${rooms} rooms, ${nodes} nodes`
    })
    console.log(`  ${building.code.padEnd(3)} ${details.join(' | ')}`)
  }
  console.log(`  total: ${graph.rooms.length} rooms, ${graph.nodes.length} nodes, ${graph.edges.length} edges`)
}

function isInside(dir, root) {
  const relative = path.relative(root, path.resolve(dir))
  return !relative.startsWith('..') && !path.isAbsolute(relative)
}

async function main(argv) {
  const { values } = parseArgs({
    args: argv,
    options: { out: { type: 'string' }, check: { type: 'boolean' }, preview: { type: 'string' } },
  })
  if (values.preview && isInside(values.preview, REPO_ROOT)) {
    console.error('The preview folder must be outside the repository (use a temp folder).')
    process.exit(1)
  }

  const { default: source } = await import(pathToFileURL(SOURCE_FILE).href)
  const { graph, errors } = buildGraph(source)
  errors.push(...validate(graph))
  if (errors.length > 0) {
    console.error(`${errors.length} error(s), nothing written:`)
    for (const error of errors) console.error(`  - ${error}`)
    process.exit(1)
  }

  const json = JSON.stringify(graph, null, 2) + '\n'
  const out = values.out ?? DEFAULT_OUT
  if (values.check) {
    const current = existsSync(out) ? readFileSync(out, 'utf8').replace(/\r\n/g, '\n') : ''
    if (current !== json) {
      console.error(`${out} is out of date: run "node tools/campus/generate.mjs".`)
      process.exit(1)
    }
    console.log('OK: graph valid and JSON up to date')
  } else {
    writeFileSync(out, json)
    console.log(`OK: graph valid, written to ${out}`)
  }
  if (values.preview) {
    writePreviews(graph, values.preview)
    console.log(`SVG previews written to ${path.resolve(values.preview)}`)
  }
  printSummary(graph)
}

// Run only when called from the command line (not when imported by the tests)
if (process.argv[1] && path.resolve(process.argv[1]) === fileURLToPath(import.meta.url)) {
  await main(process.argv.slice(2))
}
