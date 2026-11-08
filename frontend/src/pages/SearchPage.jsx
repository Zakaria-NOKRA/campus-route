import { useEffect, useState } from 'react'
import { searchRooms, getRoom } from '../api/rooms'
import RoomDetail from '../components/RoomDetail'
import RoomList from '../components/RoomList'
import SearchInput from '../components/SearchInput'
import { useDebouncedValue } from '../utils/useDebouncedValue'

const DEBOUNCE_MS = 300

// The selected room is owned by App (shared with the plan): we get it as a prop.
function SearchPage({ selectedCode, onSelect }) {
  const [query, setQuery] = useState('')
  // Last answers received. They remember WHICH request they answer (q / code),
  // so the status below can be computed instead of stored.
  const [searchResult, setSearchResult] = useState(null) // { q, rooms, error }
  const [detailResult, setDetailResult] = useState(null) // { code, room, error }

  const trimmed = query.trim()
  const debouncedQuery = useDebouncedValue(trimmed, DEBOUNCE_MS)

  // Search: runs only once the user stopped typing. The cleanup cancels the previous
  // request, so a slow old answer can never replace a newer one.
  useEffect(() => {
    if (debouncedQuery === '') return
    const controller = new AbortController()
    searchRooms(debouncedQuery, { signal: controller.signal })
      .then((rooms) => setSearchResult({ q: debouncedQuery, rooms, error: null }))
      .catch((error) => {
        if (error.name === 'AbortError') return // cancelled on purpose
        setSearchResult({ q: debouncedQuery, rooms: null, error: error.message })
      })
    return () => controller.abort()
  }, [debouncedQuery])

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

  // Search status, derived from the RAW text: emptying the input hides old results
  // at once, without waiting for the debounce. While the debounce is pending or the
  // answer has not arrived, we show "loading" instead of outdated results.
  let searchStatus
  if (trimmed === '') searchStatus = 'idle'
  else if (debouncedQuery !== trimmed || searchResult?.q !== debouncedQuery) searchStatus = 'loading'
  else searchStatus = searchResult.error ? 'error' : 'success'

  let detailStatus
  if (selectedCode === null) detailStatus = 'idle'
  else if (detailResult?.code !== selectedCode) detailStatus = 'loading'
  else detailStatus = detailResult.error ? 'error' : 'success'

  return (
    <>
      <section className="card">
        <h2>Où allez-vous ?</h2>
        <SearchInput value={query} onChange={setQuery} />
        <RoomList
          status={searchStatus}
          rooms={searchResult?.rooms}
          error={searchResult?.error}
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
