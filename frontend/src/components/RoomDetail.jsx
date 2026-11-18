import { floorLabel } from '../utils/floorLabel'
import { buildingLabel } from '../utils/place'

// Detail panel of the selected room: idle, loading, error or the room itself.
// `onClear` unselects the room (and so hides the route, which depends on the selection).
function RoomDetail({ status, room, error, onClear }) {
  if (status === 'idle') {
    return <p className="hint">Sélectionnez une salle pour voir sa fiche.</p>
  }
  if (status === 'loading') {
    return (
      <p className="hint" role="status">
        Chargement…
      </p>
    )
  }
  if (status === 'error') {
    return (
      <>
        <p className="error" role="alert">
          {error}
        </p>
        <button type="button" className="link clear-link" onClick={onClear}>
          Désélectionner
        </button>
      </>
    )
  }
  return (
    <div className="info">
      <div className="detail-head">
        <b>
          {room.code} · {room.name}
        </b>
        <button
          type="button"
          className="clear"
          aria-label="Désélectionner la salle"
          onClick={onClear}
        >
          ×
        </button>
      </div>
      <p className="hint">{room.type}</p>
      <p>
        {buildingLabel(room.building)}, {floorLabel(room.floor)} · {room.capacity} places
      </p>
      <div>
        {room.equipment.length === 0 ? (
          <span className="hint">Aucun équipement</span>
        ) : (
          room.equipment.map((item) => (
            <span key={item} className="badge">
              {item}
            </span>
          ))
        )}
      </div>
    </div>
  )
}

export default RoomDetail
