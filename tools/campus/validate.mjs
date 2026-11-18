// Checks on a generated graph ({ buildings, rooms, nodes, edges }).
// validate(graph) returns a list of error messages: an empty list means the graph is valid.

const OUTDOOR = 'EXT'
const ENTRANCE_ID = 'ENTREE'
const KINDS = ['walk', 'stairs', 'elevator']

const MAX_DOOR_GAP = 150 // px between a room door and its corridor (10.5 m)
const MAX_DOOR_LINK = 60 // px between a building door and its outdoor point, on the site plan
const ON_WALL = 1 // px of tolerance for "the door is on the wall of its room"

function distance(p, q) {
  return Math.hypot(q[0] - p[0], q[1] - p[1])
}

export function validate(graph) {
  const errors = []
  const buildingByCode = new Map(graph.buildings.map((b) => [b.code, b]))

  // Rule: node ids are unique
  const nodeById = new Map()
  for (const n of graph.nodes) {
    if (nodeById.has(n.id)) errors.push(`duplicate node id ${n.id}`)
    else nodeById.set(n.id, n)
  }

  // Position on the common site plan: building coordinates moved by the building origin
  function sitePosition(n) {
    const [ox, oy] = buildingByCode.get(n.building)?.origin ?? [0, 0]
    return [n.x + ox, n.y + oy]
  }

  // Rules on edges: known ends, no self-loop, no duplicate, positive integer weight, known kind,
  // floor changes only by stairs/elevator between consecutive floors, never shorter than the straight line
  const links = new Map(graph.nodes.map((n) => [n.id, []])) // id -> [{ to, w, kind }]
  const pairs = new Set()
  const verticalLinks = new Set() // "A|0|stairs" = a stairs edge between floors 0 and 1 of A
  for (const e of graph.edges) {
    const name = `edge ${e.a}-${e.b}`
    const a = nodeById.get(e.a)
    const b = nodeById.get(e.b)
    if (!a || !b) {
      errors.push(`${name}: unknown node`)
      continue
    }
    if (e.a === e.b) errors.push(`${name}: self-loop`)
    const pair = [e.a, e.b].sort().join('|')
    if (pairs.has(pair)) errors.push(`${name}: duplicate edge`)
    pairs.add(pair)
    if (!Number.isInteger(e.w) || e.w <= 0) errors.push(`${name}: weight must be a positive integer (got ${e.w})`)
    if (!KINDS.includes(e.kind)) errors.push(`${name}: unknown kind "${e.kind}"`)

    if (a.floor !== b.floor) {
      if (a.building !== b.building) errors.push(`${name}: floor change between two buildings`)
      if (Math.abs(a.floor - b.floor) !== 1) errors.push(`${name}: floor change must link consecutive floors`)
      if (e.kind === 'walk') errors.push(`${name}: floor change must be stairs or elevator`)
      verticalLinks.add(`${a.building}|${Math.min(a.floor, b.floor)}|${e.kind}`)
    } else {
      if (e.kind === 'elevator') errors.push(`${name}: elevator edge on a single floor`)
      const straight = distance(sitePosition(a), sitePosition(b))
      if (e.w < Math.floor(straight)) errors.push(`${name}: weight ${e.w} is shorter than the straight line (${Math.floor(straight)})`)
    }

    if (a.building !== b.building) {
      // The only links between two plan spaces: a building door and an outdoor point next to it
      const door = a.building === OUTDOOR ? b : a
      const outside = a.building === OUTDOOR ? a : b
      if (outside.building !== OUTDOOR || door.type !== 'entrance') {
        errors.push(`${name}: two plan spaces must be linked by a building door and an outdoor point`)
      } else if (distance(sitePosition(door), sitePosition(outside)) > MAX_DOOR_LINK) {
        errors.push(`${name}: outdoor point is more than ${MAX_DOOR_LINK} px from the door`)
      }
    }

    links.get(e.a).push({ to: e.b, w: e.w, kind: e.kind })
    links.get(e.b).push({ to: e.a, w: e.w, kind: e.kind })
  }

  // Rules on rooms: code format, unique, door node on the wall and close to a corridor,
  // inside the building, no overlap with another room of the same floor
  const codes = new Set()
  for (const r of graph.rooms) {
    if (codes.has(r.code)) errors.push(`duplicate room code ${r.code}`)
    codes.add(r.code)
    if (!/^[A-Z]\d{3}$/.test(r.code)) {
      errors.push(`room ${r.code}: code must be a letter + 3 digits`)
    } else {
      if (r.code[0] !== r.building) errors.push(`room ${r.code}: letter does not match building ${r.building}`)
      if (Number(r.code[1]) !== r.floor) errors.push(`room ${r.code}: first digit does not match floor ${r.floor}`)
    }

    const door = nodeById.get(r.code)
    if (!door || door.type !== 'room') {
      errors.push(`room ${r.code}: no door node`)
    } else {
      const onWall =
        door.x >= r.x - ON_WALL && door.x <= r.x + r.w + ON_WALL &&
        door.y >= r.y - ON_WALL && door.y <= r.y + r.h + ON_WALL &&
        (Math.abs(door.x - r.x) <= ON_WALL || Math.abs(door.x - r.x - r.w) <= ON_WALL ||
          Math.abs(door.y - r.y) <= ON_WALL || Math.abs(door.y - r.y - r.h) <= ON_WALL)
      if (!onWall) errors.push(`room ${r.code}: door is not on the wall of the room`)
      for (const link of links.get(r.code)) {
        if (link.w < 1 || link.w > MAX_DOOR_GAP) errors.push(`room ${r.code}: door is ${link.w} px from the corridor (1 to ${MAX_DOOR_GAP})`)
      }
    }

    const building = buildingByCode.get(r.building)
    if (!building) {
      errors.push(`room ${r.code}: unknown building ${r.building}`)
    } else if (r.x < 0 || r.y < 0 || r.x + r.w > building.width || r.y + r.h > building.height) {
      errors.push(`room ${r.code}: outside the building`)
    }
  }
  for (let i = 0; i < graph.rooms.length; i++) {
    for (let j = i + 1; j < graph.rooms.length; j++) {
      const p = graph.rooms[i]
      const q = graph.rooms[j]
      const sameFloor = p.building === q.building && p.floor === q.floor
      if (sameFloor && p.x < q.x + q.w && q.x < p.x + p.w && p.y < q.y + q.h && q.y < p.y + p.h) {
        errors.push(`rooms ${p.code} and ${q.code} overlap`)
      }
    }
  }

  // Rules on buildings: consecutive floors linked by stairs AND elevator, at least one door to the outside
  for (const building of graph.buildings.filter((b) => !b.outdoor)) {
    const floors = [...new Set(graph.nodes.filter((n) => n.building === building.code).map((n) => n.floor))].sort((x, y) => x - y)
    for (let i = 1; i < floors.length; i++) {
      for (const kind of ['stairs', 'elevator']) {
        if (!verticalLinks.has(`${building.code}|${floors[i - 1]}|${kind}`)) {
          errors.push(`building ${building.code}: floors ${floors[i - 1]} and ${floors[i]} have no ${kind} link`)
        }
      }
    }
    const hasExit = graph.nodes.some((n) => n.building === building.code && n.type === 'entrance' &&
      links.get(n.id).some((link) => nodeById.get(link.to).building === OUTDOOR))
    if (!hasExit) errors.push(`building ${building.code}: no door linked to the outside`)
  }

  // Rule: no isolated node
  for (const n of graph.nodes) {
    if (links.get(n.id)?.length === 0) errors.push(`node ${n.id} is isolated`)
  }

  // Rule: everything is reachable from the main entrance, and every room without stairs
  if (!nodeById.has(ENTRANCE_ID)) {
    errors.push(`no ${ENTRANCE_ID} node`)
  } else {
    const reached = reachable(ENTRANCE_ID, links, () => true)
    for (const n of graph.nodes) {
      if (!reached.has(n.id)) errors.push(`node ${n.id} cannot be reached from ${ENTRANCE_ID}`)
    }
    const reachedWithoutStairs = reachable(ENTRANCE_ID, links, (link) => link.kind !== 'stairs')
    for (const r of graph.rooms) {
      if (reached.has(r.code) && !reachedWithoutStairs.has(r.code)) {
        errors.push(`room ${r.code} cannot be reached without stairs`)
      }
    }
  }

  return errors
}

// Breadth-first search: ids reachable from `start` using only the links accepted by `allowed`
function reachable(start, links, allowed) {
  const seen = new Set([start])
  const queue = [start]
  while (queue.length > 0) {
    const id = queue.shift()
    for (const link of links.get(id) ?? []) {
      if (allowed(link) && !seen.has(link.to)) {
        seen.add(link.to)
        queue.push(link.to)
      }
    }
  }
  return seen
}
