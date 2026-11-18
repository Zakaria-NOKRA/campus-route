// Helpers to draw a plan sent by GET /api/plan?building=.
// All coordinates are plan pixels (1 px = 0.07 m). The <svg> uses viewBox "0 0 width height"
// of the plan, so SVG units = plan pixels: we never convert anything ourselves.

// Thickness of a door mark, and its length along the wall.
const DOOR_THICKNESS = 6
const DOOR_LENGTH = 28

// What to draw for ONE floor: its rooms, its nodes, and the edges whose two ends are both on
// this floor. Stairs / elevator edges join two floors, so they are left out automatically.
// Each edge comes back with its two nodes ({ from, to, kind }) so it can be drawn as a line.
export function floorContents(plan, floor) {
  const rooms = plan.rooms.filter((room) => room.floor === floor)
  const nodes = plan.nodes.filter((node) => node.floor === floor)

  const nodeById = new Map(nodes.map((node) => [node.id, node]))
  const edges = []
  for (const edge of plan.edges) {
    const from = nodeById.get(edge.from)
    const to = nodeById.get(edge.to)
    if (from && to) edges.push({ from, to, kind: edge.kind })
  }
  return { rooms, nodes, edges }
}

// Small rectangle of the door, centred on (doorX, doorY). The door lies on one wall of the
// room: on the top or bottom wall it is horizontal, on the left or right wall it is vertical.
export function doorRect(room) {
  const onTopOrBottom = room.doorY === room.y || room.doorY === room.y + room.height
  const width = onTopOrBottom ? DOOR_LENGTH : DOOR_THICKNESS
  const height = onTopOrBottom ? DOOR_THICKNESS : DOOR_LENGTH
  return { x: room.doorX - width / 2, y: room.doorY - height / 2, width, height }
}

// Colour class of a room, from its type (same rules as the prototype).
// The API has no category field, so we match the French type names it sends.
export function roomTypeClass(type) {
  if (type === 'Amphithéâtre') return 't-amph'
  if (/Labo|informatique/.test(type)) return 't-lab'
  if (type === 'Service') return 't-serv'
  return ''
}
