import { floorLabel } from '../utils/floorLabel'

// Detail panel of the selected room: idle, loading, error or the room itself.
function RoomDetail({ status, room, error }) {
  if (status === 'idle') {
    return <p className="hint">Sélectionnez une salle pour voir sa fiche.</p>
  }
  if (status === 'loading') {
    return <p className="hint">Chargement…</p>
  }
  if (status === 'error') {
    return (
      <p className="error" role="alert">
        {error}
      </p>
    )
  }
  return (
    <div className="info">
      <b>
        {room.code} · {room.name}
      </b>
      <p className="hint">{room.type}</p>
      <p>
        {floorLabel(room.floor)} · {room.capacity} places
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
