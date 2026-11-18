// Code of the outdoor space (courtyard, paths) in the API.
export const OUTDOOR = 'EXT'

// Building and floor of a room, read from its code: building letter + 3 digits, the first
// digit is the floor. Example: "B204" -> { building: 'B', floor: 2 }.
// Lets the plan switch to the right place before the building plan is even loaded.
export function roomPlace(code) {
  return { building: code[0], floor: Number(code[1]) }
}

// Short name of a building for tabs and lists: "Bât. A", or "Extérieur" for the outdoor space.
export function buildingLabel(code) {
  return code === OUTDOOR ? 'Extérieur' : `Bât. ${code}`
}
