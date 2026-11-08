// Geometry of the floor plan, copied from prototype/campus_route_prototype.html.
//
// NOTE: the API only sends `slot` (column) and `top` (row) for each room, so these
// constants rebuild the drawing from that grid. This layout (5 columns, 2 rows around
// one central corridor) only fits the current sample building. A real building would
// need real coordinates or shapes from the API.
//
// Units: SVG units = plan pixels. The <svg> uses viewBox "0 0 800 360", so the
// browser does the scaling and we never convert anything ourselves.

export const VIEW_WIDTH = 800
export const VIEW_HEIGHT = 360

export const SLOT_COUNT = 5 // columns of rooms
const SLOT_X0 = 128 // x of the centre of column 0
const SLOT_STEP = 136 // distance between two column centres

export const ROOM_WIDTH = 128
export const ROOM_HEIGHT = 110
const TOP_Y = 60 // y of the top edge of the top row
const BOTTOM_Y = 230 // y of the top edge of the bottom row

export const CORRIDOR_Y = 170
export const CORRIDOR_HEIGHT = 60
export const SERVICE_WIDTH = 48 // stairs / elevator boxes at both ends

// x of the centre of a column. For a room it equals `room.x` sent by the API.
export function slotX(slot) {
  return SLOT_X0 + SLOT_STEP * slot
}

// Top edge of a row.
export function rowY(top) {
  return top ? TOP_Y : BOTTOM_Y
}

// Colour class of a room, from its type (same rules as the prototype).
// The API has no category field, so we match the French type names it sends.
export function roomTypeClass(type) {
  if (type === 'Amphithéâtre') return 't-amph'
  if (/Labo|informatique/.test(type)) return 't-lab'
  if (type === 'Service') return 't-serv'
  return ''
}

// Columns of the bottom row with no room on this floor (the hall / atrium).
export function emptyBottomSlots(rooms) {
  const used = new Set(rooms.filter((room) => !room.top).map((room) => room.slot))
  const empty = []
  for (let slot = 0; slot < SLOT_COUNT; slot++) {
    if (!used.has(slot)) empty.push(slot)
  }
  return empty
}
