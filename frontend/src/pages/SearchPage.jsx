import { useEffect, useState } from 'react'
import { getRoom } from '../api/rooms'
import RoomDetail from '../components/RoomDetail'
import RoomList from '../components/RoomList'
import SearchInput from '../components/SearchInput'
import { useRoomSearch } from '../utils/useRoomSearch'

// The selected room is owned by App (shared with the plan): we get it as a prop.
function SearchPage({ selectedCode, onSelect }) {
  const [query, setQuery] = useState('')
  // Last answer received. It remembers WHICH code it answers,
  // so the status below can be computed instead of stored.
  const [detailResult, setDetailResult] = useState(null) // { code, room, error }

  // Search (debounce, request, status) lives in a shared hook.
  const search = useRoomSearch(query)

  // Detail of the selected room.
  useEffect(() => {
    if (selectedCode === null) return
    const controller = new AbortController()
    getRoom(selectedCode, { signal: controller.signal })
      .then((room) => setDetailResult({ code: selectedCode, room, error: null }))
      .catch((error) => {
        if (error.name === 'AbortError') return
        setDetailResult({ code: selectedCode, room: null, error: error.message })
      })
    return () => controller.abort()
  }, [selectedCode])

  let detailStatus
  if (selectedCode === null) detailStatus = 'idle'
  else if (detailResult?.code !== selectedCode) detailStatus = 'loading'
  else detailStatus = detailResult.error ? 'error' : 'success'

  return (
    <>
      <section className="card">
        <h2>Où allez-vous ?</h2>
        <SearchInput
          id="room-search"
          label="Destination (code, nom ou type)"
          value={query}
          onChange={setQuery}
        />
        <RoomList
          status={search.status}
          rooms={search.rooms}
          error={search.error}
          selectedCode={selectedCode}
          onSelect={onSelect}
        />
      </section>
      <section className="card">
        <h2>Lieu</h2>
        <RoomDetail
          status={detailStatus}
          room={detailResult?.room}
          error={detailResult?.error}
        />
      </section>
    </>
  )
}

export default SearchPage
