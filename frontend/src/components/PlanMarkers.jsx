import { OUTDOOR } from '../utils/place'
import MarkerIcon from './MarkerIcon'

// Markers of the special points of one floor: stairs, elevator, doors of a building,
// and on the campus plan the outdoor points (steps, ramp, courtyard...).
// `nodes` = nodes of the floor. `k` = plan width / 1000, so the markers keep the same size on screen.
// They are drawn after the rooms (on top) and only show information: no click.
const BOX = 36 // side of the stairs / elevator box, in plan pixels (before k)

function PlanMarkers({ nodes, k }) {
  return (
    <g className="plan-markers">
      {nodes.map((node) => {
        if (node.type === 'STAIRS' || node.type === 'ELEVATOR') {
          // A symbol centred on the node (the door of the stairs / elevator), its name as a tooltip.
          return (
            <g key={node.id}>
              <title>{node.label}</title>
              <MarkerIcon type={node.type} x={node.x} y={node.y} size={BOX * k} />
            </g>
          )
        }
        if (node.building === OUTDOOR) {
          // Outdoor point: a dot, its name as a tooltip. Written names for all of them would
          // overlap (or be cut at the right edge for the ramp), so only the campus entrance,
          // the usual starting point, has its name written.
          const isEntrance = node.type === 'ENTRANCE'
          return (
            <g key={node.id}>
              <title>{node.label}</title>
              <circle
                className={isEntrance ? 'entrance' : 'outdoor'}
                cx={node.x}
                cy={node.y}
                r={(isEntrance ? 10 : 6) * k}
              />
              {isEntrance && (
                <text className="lbl outdoor-lbl" x={node.x + 16 * k} y={node.y + 4 * k}>
                  {node.label}
                </text>
              )}
            </g>
          )
        }
        if (node.type === 'ENTRANCE') {
          // A door of the building (on its outline): a round mark with a small white door
          // inside, its full name as a tooltip.
          return (
            <g key={node.id}>
              <title>{node.label}</title>
              <circle className="entrance" cx={node.x} cy={node.y} r={12 * k} />
              <rect
                className="entrance-door"
                x={node.x - 3.5 * k}
                y={node.y - 5.5 * k}
                width={7 * k}
                height={11 * k}
                rx={1.5 * k}
              />
            </g>
          )
        }
        return null // corridors and rooms already have their own drawing
      })}
    </g>
  )
}

export default PlanMarkers
