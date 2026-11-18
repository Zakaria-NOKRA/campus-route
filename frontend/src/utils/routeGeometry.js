// Helpers to draw a route on the plan. `path` is the list of nodes sent by GET /api/route,
// from the start to the arrival: [{ id, building, floor, x, y, ... }].

// Cuts the path into runs of consecutive nodes on the same plan (same building AND floor).
// Changing floor (stairs / elevator) or leaving / entering a building ends a run.
// Example: A0, A0, EXT0, EXT0, C0, C2  ->  4 runs (A 0, EXT 0, C 0, C 2)
export function splitByPlace(path) {
  const runs = []
  for (const node of path) {
    const last = runs[runs.length - 1]
    if (last && last.building === node.building && last.floor === node.floor) {
      last.nodes.push(node)
    } else {
      runs.push({ building: node.building, floor: node.floor, nodes: [node] })
    }
  }
  return runs
}

// Buildings crossed by the route (EXT included), without duplicates, in the order of the route.
// Used to mark the building tabs and to highlight the buildings on the campus plan.
export function routeBuildings(path) {
  return [...new Set(path.map((node) => node.building))]
}

// Floors of `building` crossed by the route, without duplicates (used to mark the floor tabs).
export function routeFloors(path, building) {
  const floors = path.filter((node) => node.building === building).map((node) => node.floor)
  return [...new Set(floors)]
}

// Value of the SVG `points` attribute for a run: "x1,y1 x2,y2 ...".
// The coordinates are already plan pixels, the same as the SVG viewBox: no conversion.
export function toPoints(nodes) {
  return nodes.map((node) => `${node.x},${node.y}`).join(' ')
}
