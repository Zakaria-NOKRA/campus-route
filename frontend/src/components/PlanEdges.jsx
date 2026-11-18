import { edgeClass } from '../utils/planGeometry'

// Links of one floor, drawn as lines under the rooms (or under the buildings, outdoors).
// Thick lines with round ends: they overlap at the junctions and together look like the
// corridors, or the outdoor paths. Links that need no line (e.g. room door) are skipped.
// The same component draws both: only the CSS class changes (see edgeClass).
// `edges` = [{ from, to, kind }] with the two nodes, as given by floorContents.
// Outdoor paths are drawn in two passes: first a slightly wider darker border under EVERY
// path, then the light surface of every path. Doing it path by path would draw the border of
// one path over the surface of the previous one, and show a seam at each junction.
// Outdoor steps get a written "Marches", the ramp a written "Rampe".
// `k` = plan width / 1000, so these texts keep the same size on screen.
function PlanEdges({ edges, k }) {
  const drawn = edges
    .map((edge) => ({ ...edge, className: edgeClass(edge) }))
    .filter((edge) => edge.className !== null)
  // Outdoor links (paths and steps): they get the border. Under the steps it also fills the
  // gaps between the treads, so they show stone instead of grass.
  const paths = drawn.filter((edge) => edge.className === 'path' || edge.className === 'steps')

  return (
    <g className="plan-edges">
      {paths.map((edge) => (
        <line key={`border-${edge.from.id}-${edge.to.id}`} className="path-border" {...lineEnds(edge)} />
      ))}
      {drawn.map((edge) => {
        const middleX = (edge.from.x + edge.to.x) / 2
        const middleY = (edge.from.y + edge.to.y) / 2
        // The ramp: the outdoor link whose two ends are named "Rampe..." in the data.
        const isRamp = edge.from.label?.startsWith('Rampe') && edge.to.label?.startsWith('Rampe')
        return (
          <g key={`${edge.from.id}-${edge.to.id}`}>
            <line className={edge.className} {...lineEnds(edge)} />
            {edge.className === 'steps' && (
              <text
                className="lbl outdoor-lbl"
                x={middleX - 40 * k}
                y={middleY + 4 * k}
                textAnchor="end"
              >
                Marches
              </text>
            )}
            {edge.className === 'path' && isRamp && (
              <text className="lbl outdoor-lbl" x={middleX + 40 * k} y={middleY + 4 * k}>
                Rampe
              </text>
            )}
          </g>
        )
      })}
    </g>
  )
}

// The two ends of the straight line of a link, as SVG <line> attributes.
function lineEnds(edge) {
  return { x1: edge.from.x, y1: edge.from.y, x2: edge.to.x, y2: edge.to.y }
}

export default PlanEdges
