import { edgeClass } from '../utils/planGeometry'

// Walking links of one floor, drawn as lines under the rooms.
// Inside a building, thick lines with round ends: they overlap at the junctions and
// together look like the corridors. Links that need no line (e.g. room door) are skipped.
// `edges` = [{ from, to, kind }] with the two nodes, as given by floorContents.
function PlanEdges({ edges }) {
  return (
    <g className="plan-edges">
      {edges.map((edge) => {
        const className = edgeClass(edge)
        if (className === null) return null
        return (
          <line
            key={`${edge.from.id}-${edge.to.id}`}
            className={className}
            x1={edge.from.x}
            y1={edge.from.y}
            x2={edge.to.x}
            y2={edge.to.y}
          />
        )
      })}
    </g>
  )
}

export default PlanEdges
