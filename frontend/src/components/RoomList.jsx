import RoomListItem from './RoomListItem'

// Shows the right thing for each search status: idle, loading, error, empty or results.
function RoomList({ status, rooms, error, selectedCode, onSelect }) {
  if (status === 'idle') {
    return <p className="hint">Tapez un code, un nom ou un type de salle.</p>
  }
  if (status === 'loading') {
    return <p className="hint">Recherche…</p>
  }
  if (status === 'error') {
    return (
      <p className="error" role="alert">
        {error}
      </p>
    )
  }
  if (rooms.length === 0) {
    return <p className="hint">Aucune salle trouvée.</p>
  }
  return (
    <ul className="results">
      {rooms.map((room) => (
        <RoomListItem
          key={room.code}
          room={room}
          selected={room.code === selectedCode}
          onSelect={onSelect}
        />
      ))}
    </ul>
  )
}

export default RoomList
