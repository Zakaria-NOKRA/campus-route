// Helpers to draw a route on the plan. `path` is the list of nodes sent by GET /api/route,
// from the start to the arrival: [{ id, floor, x, y, ... }].

// Cuts the path into runs of consecutive nodes that are on the same floor.
// A change of floor (stairs / elevator) ends a run and starts the next one.
// Example: floor 0, 0, 0, 2, 2  ->  [{ floor: 0, nodes: [3 nodes] }, { floor: 2, nodes: [2 nodes] }]
export function splitByFloor(path) {
  const runs = []
  for (const node of path) {
    const last = runs[runs.length - 1]
    if (last && last.floor === node.floor) {
      last.nodes.push(node)
    } else {
      runs.push({ floor: node.floor, nodes: [node] })
    }
  }
  return runs
}

// Floors crossed by the route, without duplicates (used to mark the floor tabs).
export function routeFloors(path) {
  return [...new Set(path.map((node) => node.floor))]
}

// Value of the SVG `points` attribute for a run: "x1,y1 x2,y2 ...".
// The coordinates are already plan pixels, the same as the SVG viewBox: no conversion.
export function toPoints(nodes) {
  return nodes.map((node) => `${node.x},${node.y}`).join(' ')
}
