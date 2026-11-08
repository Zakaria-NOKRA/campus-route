import {
  ROOM_HEIGHT,
  ROOM_WIDTH,
  roomTypeClass,
  rowY,
  slotX,
} from '../utils/planGeometry'

// One room of the plan: box, code, short tag and the little door facing the corridor.
// It behaves like a button: click, or Enter / Space when it has the keyboard focus.
function PlanRoom({ room, selected, onSelect }) {
  const cx = slotX(room.slot)
  const y0 = rowY(room.top)
  const doorY = room.top ? 167 : 228 // door sits on the corridor side

  function handleKeyDown(event) {
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault() // Space would scroll the page otherwise
      onSelect(room.code)
    }
  }

  return (
    <g
      className="plan-room"
      role="button"
      tabIndex={0}
      aria-pressed={selected}
      aria-label={`${room.code}, ${room.name}`}
      onClick={() => onSelect(room.code)}
      onKeyDown={handleKeyDown}
    >
      <title>{room.name}</title>
      <rect
        className={`room ${roomTypeClass(room.type)}${selected ? ' selected' : ''}`}
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
