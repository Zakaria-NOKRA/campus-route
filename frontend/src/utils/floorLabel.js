// Floor 0 is the ground floor ("RDC" in French), the others are "Étage n".
export function floorLabel(floor) {
  return floor === 0 ? 'RDC' : `Étage ${floor}`
}
