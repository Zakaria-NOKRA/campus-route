// Markers of the special points of one floor: stairs, elevator and doors of the building.
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
