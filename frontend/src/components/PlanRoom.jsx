import { doorRect, roomTypeClass } from '../utils/planGeometry'

// One room of the plan: its rectangle, code and short tag in the middle, and its door.
// It behaves like a button: click, or Enter / Space when it has the keyboard focus.
function PlanRoom({ room, selected, onSelect }) {
  const cx = room.x + room.width / 2 // centre of the rectangle
  const cy = room.y + room.height / 2
  const door = doorRect(room)

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
        x={room.x}
        y={room.y}
        width={room.width}
        height={room.height}
        rx="6"
      />
      <text className="code" x={cx} y={cy} textAnchor="middle">
        {room.code}
      </text>
      <text className="tag" x={cx} y={cy + 16} textAnchor="middle">
        {room.tag}
      </text>
      <rect className="door" x={door.x} y={door.y} width={door.width} height={door.height} />
    </g>
  )
}

export default PlanRoom
