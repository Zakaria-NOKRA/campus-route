import { splitByPlace, toPoints } from '../utils/routeGeometry'

// Route drawn over the plan, for ONE building and ONE floor: the line(s) there, then the markers.
// It goes inside the <svg>, after the rooms, so it is drawn on top of them.
// `k` = plan width / 1000: the markers get bigger on a bigger plan, to keep the same size on screen.
function RouteLayer({ path, building, floor, k }) {
  const isHere = (item) => item.building === building && item.floor === floor
  const runs = splitByPlace(path).filter(isHere)
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
      {isHere(start) && <circle className="mk-start" cx={start.x} cy={start.y} r={9 * k} />}
      {isHere(destination) && (
        <>
          <circle className="mk-dest" cx={destination.x} cy={destination.y} r={10 * k} />
          <circle cx={destination.x} cy={destination.y} r={4 * k} fill="#fff" />
        </>
      )}
    </g>
  )
}

export default RouteLayer
