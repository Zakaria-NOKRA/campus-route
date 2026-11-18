import { edgeClass } from '../utils/planGeometry'

// Links of one floor, drawn as lines under the rooms (or under the buildings, outdoors).
// Thick lines with round ends: they overlap at the junctions and together look like the
// corridors, or the outdoor paths. Links that need no line (e.g. room door) are skipped.
// The same component draws both: only the CSS class changes (see edgeClass).
// `edges` = [{ from, to, kind }] with the two nodes, as given by floorContents.
// Outdoor steps also get a written "Marches", left of their middle.
// `k` = plan width / 1000, so that text keeps the same size on screen.
function PlanEdges({ edges, k }) {
  return (
    <g className="plan-edges">
      {edges.map((edge) => {
        const className = edgeClass(edge)
        if (className === null) return null
        const middleX = (edge.from.x + edge.to.x) / 2
        const middleY = (edge.from.y + edge.to.y) / 2
        return (
          <g key={`${edge.from.id}-${edge.to.id}`}>
            <line
              className={className}
              x1={edge.from.x}
              y1={edge.from.y}
              x2={edge.to.x}
              y2={edge.to.y}
            />
            {className === 'steps' && (
              <text
                className="lbl outdoor-lbl"
                x={middleX - 40 * k}
                y={middleY + 4 * k}
                textAnchor="end"
              >
                Marches
              </text>
            )}
          </g>
        )
      })}
    </g>
  )
}

export default PlanEdges
