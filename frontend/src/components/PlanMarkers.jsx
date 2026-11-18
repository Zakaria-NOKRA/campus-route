import { OUTDOOR } from '../utils/place'

// Markers of the special points of one floor: stairs, elevator, doors of a building,
// and on the campus plan the outdoor points (steps, ramp, courtyard...).
// `nodes` = nodes of the floor. `k` = plan width / 1000, so the markers keep the same size on screen.
// They are drawn after the rooms (on top) and only show information: no click.
const BOX = 36 // side of the stairs / elevator box, in plan pixels (before k)

function PlanMarkers({ nodes, k }) {
  const size = BOX * k
  return (
    <g className="plan-markers">
      {nodes.map((node) => {
        if (node.type === 'STAIRS' || node.type === 'ELEVATOR') {
          // A box centred on the node (the door of the stairs / elevator), with a short label.
          return (
            <g key={node.id}>
              <title>{node.label}</title>
              <rect
                className="svc"
                x={node.x - size / 2}
                y={node.y - size / 2}
                width={size}
                height={size}
                rx={4 * k}
              />
              <text className="lbl" x={node.x} y={node.y + 4 * k} textAnchor="middle">
                {node.type === 'STAIRS' ? 'Esc.' : 'Asc.'}
              </text>
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
          // A door of the building (on its outline): a round mark, its full name as a tooltip.
          return (
            <circle key={node.id} className="entrance" cx={node.x} cy={node.y} r={10 * k}>
              <title>{node.label}</title>
            </circle>
          )
        }
        return null // corridors and rooms already have their own drawing
      })}
    </g>
  )
}

export default PlanMarkers
