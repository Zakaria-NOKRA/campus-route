import { BENCHES, FLOWERBEDS, FOUNTAIN, LAWNS, TREES, flowerDots } from '../utils/campusScenery'

// Decoration of the campus plan, drawn on the grass and under the paths and buildings.
// Only simple shapes (rect, circle, ellipse): no image. It shows nothing useful for a
// screen reader, so it is hidden from it (aria-hidden), and it never catches a click.
// `nodes` = nodes of the outdoor plan: the gate is drawn around the campus entrance found
// there. `width` / `height` = size of the plan, for the low wall along the south edge.
const GATE_HALF = 90 // half width of the opening in the wall, around the entrance
const WALL = 12 // thickness of the wall
const PILLAR = 34 // side of a gate pillar

function CampusScenery({ nodes, width, height }) {
  const entrance = nodes.find((node) => node.type === 'ENTRANCE')
  const wallY = height - WALL - 10

  return (
    <g className="scenery" aria-hidden="true">
      {LAWNS.map((lawn, index) => (
        <rect key={index} className="lawn" {...lawn} rx="40" />
      ))}

      {/* Fountain: stone ring, water, and a light spot in the middle */}
      <circle className="fountain-stone" cx={FOUNTAIN.x} cy={FOUNTAIN.y} r={FOUNTAIN.r} />
      <circle className="fountain-water" cx={FOUNTAIN.x} cy={FOUNTAIN.y} r={FOUNTAIN.r - 14} />
      <circle className="fountain-jet" cx={FOUNTAIN.x} cy={FOUNTAIN.y} r={FOUNTAIN.r * 0.22} />

      {FLOWERBEDS.map((bed, index) => (
        <g key={index}>
          <rect className="bed" {...bed} rx={bed.height / 2} />
          {flowerDots(bed).map((dot, i) => (
            <circle
              key={i}
              className={`flower flower-${dot.color}`}
              cx={dot.x}
              cy={dot.y}
              r={bed.height * 0.22}
            />
          ))}
        </g>
      ))}

      {BENCHES.map((bench, index) => (
        <rect key={index} className="bench" {...bench} rx="4" />
      ))}

      {/* Trees: shadow on the ground (down-right), crown, then a lighter spot (sun from the top-left) */}
      {TREES.map(([x, y, r, shade], index) => (
        <g key={index}>
          <ellipse className="tree-shadow" cx={x + r * 0.25} cy={y + r * 0.3} rx={r} ry={r * 0.9} />
          <circle className={`tree tree-${shade}`} cx={x} cy={y} r={r} />
          <circle className="tree-light" cx={x - r * 0.3} cy={y - r * 0.3} r={r * 0.45} />
        </g>
      ))}

      {/* Low wall along the south edge, open at the entrance, with two gate pillars */}
      {entrance && (
        <>
          <rect className="campus-wall" x="0" y={wallY} width={entrance.x - GATE_HALF} height={WALL} />
          <rect
            className="campus-wall"
            x={entrance.x + GATE_HALF}
            y={wallY}
            width={width - entrance.x - GATE_HALF}
            height={WALL}
          />
          {[-1, 1].map((side) => (
            <rect
              key={side}
              className="gate-pillar"
              x={entrance.x + side * GATE_HALF - PILLAR / 2}
              y={wallY + WALL / 2 - PILLAR / 2}
              width={PILLAR}
              height={PILLAR}
              rx="4"
            />
          ))}
        </>
      )}
    </g>
  )
}

export default CampusScenery
