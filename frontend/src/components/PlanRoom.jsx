import { ROOM_HEIGHT, ROOM_WIDTH, roomTypeClass, rowY, slotX } from '../utils/planGeometry'

// One room of the plan: box, code, short tag and the little door facing the corridor.
function PlanRoom({ room }) {
  const cx = slotX(room.slot)
  const y0 = rowY(room.top)
  const doorY = room.top ? 167 : 228 // door sits on the corridor side
  return (
    <g>
      <title>{room.name}</title>
      <rect
        className={`room ${roomTypeClass(room.type)}`}
        x={cx - ROOM_WIDTH / 2}
        y={y0}
        width={ROOM_WIDTH}
        height={ROOM_HEIGHT}
        rx="6"
      />
      <text className="code" x={cx} y={y0 + 55} textAnchor="middle">
        {room.code}
      </text>
      <text className="tag" x={cx} y={y0 + 72} textAnchor="middle">
        {room.tag}
      </text>
      <rect className="door" x={cx - 14} y={doorY} width="28" height="5" />
    </g>
  )
}

export default PlanRoom
