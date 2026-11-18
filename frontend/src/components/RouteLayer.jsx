import { splitByPlace, toPoints } from '../utils/routeGeometry'
import DestinationPin from './DestinationPin'

// Route drawn over the plan, for ONE building and ONE floor: the line(s) there, then the markers.
// It goes inside the <svg>, after the rooms, so it is drawn on top of them.
// `k` = plan width / 1000: lines and markers get bigger on a bigger plan, to keep the same size on screen.
// Each line is drawn 3 times, on top of each other:
// 1. a white "casing", a bit wider, so the route stands out on any colour below;
// 2. the orange line. pathLength="1" tells the browser to count its length as 1, so the CSS
//    can draw it progressively (stroke-dashoffset from 1 to 0) whatever its real length;
// 3. thin white dashes that move along the line once it is drawn (direction of walking).
// The animations are in index.css; they are switched off with prefers-reduced-motion.
function RouteLayer({ path, building, floor, k }) {
  const isHere = (item) => item.building === building && item.floor === floor
  const runs = splitByPlace(path)
    .filter(isHere)
    .filter((run) => run.nodes.length > 1) // a single node (e.g. only the stairs) has no line
  const start = path[0]
  const destination = path[path.length - 1]

  return (
    <g className="route-layer">
      {runs.map((run, index) => {
        const points = toPoints(run.nodes)
        return (
          <g key={index}>
            <polyline className="route-casing" points={points} />
            <polyline className="route" points={points} pathLength="1" />
            <polyline className="route-flow" points={points} />
          </g>
        )
      })}
      {isHere(start) && <circle className="mk-start" cx={start.x} cy={start.y} r={9 * k} />}
      {isHere(destination) && <DestinationPin x={destination.x} y={destination.y} k={k} />}
    </g>
  )
}

export default RouteLayer
