import { floorLabel } from '../utils/floorLabel'

// One search result. It is a <button> so it works with the keyboard too.
function RoomListItem({ room, selected, onSelect }) {
  return (
    <li>
      <button
        type="button"
        className={selected ? 'room-item selected' : 'room-item'}
        onClick={() => onSelect(room.code)}
      >
        <strong>{room.code}</strong> {room.name}
        <small>
          {room.type} · {floorLabel(room.floor)}
        </small>
      </button>
    </li>
  )
}

export default RoomListItem
