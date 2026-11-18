// Destination marker: a pulsing ring on the ground, and a pin whose tip is exactly on the point.
function DestinationPin({ x, y, k }) {
  // Pin drawn in a 24 x 24 box with its tip at (12, 22), then moved and scaled onto the point.
  const scale = 1.6 * k
  return (
    <g>
      <circle className="mk-pulse" cx={x} cy={y} r={10 * k} />
      <circle className="mk-dest-spot" cx={x} cy={y} r={4 * k} />
      <g transform={`translate(${x - 12 * scale} ${y - 22 * scale}) scale(${scale})`}>
        <path
          className="mk-dest"
          d="M12 2c-4.4 0-8 3.4-8 7.7C4 15.2 12 22 12 22s8-6.8 8-12.3C20 5.4 16.4 2 12 2z"
        />
        <circle cx="12" cy="9.7" r="3" fill="#fff" />
      </g>
    </g>
  )
}

export default DestinationPin
