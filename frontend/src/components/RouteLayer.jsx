import { splitByFloor, toPoints } from '../utils/routeGeometry'

// Route drawn over the plan, for ONE floor: the line(s) of that floor, then the markers.
// It goes inside the <svg>, after the rooms, so it is drawn on top of them.
function RouteLayer({ path, floor }) {
  const runs = splitByFloor(path).filter((run) => run.floor === floor)
  const start = path[0]
  const destination = path[path.length - 1]

  return (
    <g className="route-layer">
      {runs.map((run, index) =>
        // A run of a single node (e.g. only the stairs on that floor) has no line to draw.
        run.nodes.length > 1 ? (
          <polyline key={index} className="route" points={toPoints(run.nodes)} />
        ) : null,
      )}
      {start.floor === floor && (
        <circle className="mk-start" cx={start.x} cy={start.y} r="9" />
      )}
      {destination.floor === floor && (
        <>
          <circle className="mk-dest" cx={destination.x} cy={destination.y} r="10" />
          <circle cx={destination.x} cy={destination.y} r="4" fill="#fff" />
        </>
      )}
    </g>
  )
}

export default RouteLayer
