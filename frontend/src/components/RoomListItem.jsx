import { floorLabel } from '../utils/floorLabel'
import { buildingLabel } from '../utils/place'

// One search result: code, name, then type, building and floor.
// It is a <button> so it works with the keyboard too.
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
          {room.type} · {buildingLabel(room.building)} · {floorLabel(room.floor)}
        </small>
      </button>
    </li>
  )
}

export default RoomListItem
