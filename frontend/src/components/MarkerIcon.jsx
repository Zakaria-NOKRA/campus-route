// Stairs or elevator symbol: a rounded box of side `size`, centred on (x, y).
// - stairs: a staircase profile (3 steps going up to the right);
// - elevator: one triangle up, one triangle down.
// Used on the plan (PlanMarkers) and in the legend (PlanLegend), so both look the same.
// Every measure is a fraction of `size`: the symbol keeps its shape at any size.
function MarkerIcon({ type, x, y, size }) {
  const half = size / 2
  const isStairs = type === 'STAIRS'

  let symbol
  if (isStairs) {
    const pad = size * 0.22 // empty margin inside the box
    const step = (size - 2 * pad) / 3 // width and height of one step
    const left = x - half + pad
    const bottom = y + half - pad
    // From bottom-left: up, right, up, right... like the side view of a staircase.
    const points = [
      [left, bottom - step],
      [left + step, bottom - step],
      [left + step, bottom - 2 * step],
      [left + 2 * step, bottom - 2 * step],
      [left + 2 * step, bottom - 3 * step],
      [left + 3 * step, bottom - 3 * step],
    ]
    symbol = (
      <polyline
        className="mk-symbol-line"
        points={points.map((point) => point.join(',')).join(' ')}
        strokeWidth={size * 0.09}
      />
    )
  } else {
    const w = size * 0.2 // half width of a triangle
    const gap = size * 0.06 // space between the two triangles
    const tall = size * 0.22 // height of a triangle
    symbol = (
      <>
        <polygon
          className="mk-symbol"
          points={`${x - w},${y - gap} ${x + w},${y - gap} ${x},${y - gap - tall}`}
        />
        <polygon
          className="mk-symbol"
          points={`${x - w},${y + gap} ${x + w},${y + gap} ${x},${y + gap + tall}`}
        />
      </>
    )
  }

  return (
    <g className={isStairs ? 'mk-stairs' : 'mk-elevator'}>
      <rect
        className="mk-box"
        x={x - half}
        y={y - half}
        width={size}
        height={size}
        rx={size * 0.22}
        strokeWidth={size * 0.05}
      />
      {symbol}
    </g>
  )
}

export default MarkerIcon
