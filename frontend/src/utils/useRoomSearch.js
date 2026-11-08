import { useEffect, useState } from 'react'
import { searchRooms } from '../api/rooms'
import { useDebouncedValue } from './useDebouncedValue'

const DEBOUNCE_MS = 300

// Room search driven by the text typed by the user.
// Returns { status: 'idle' | 'loading' | 'error' | 'success', rooms, error }.
export function useRoomSearch(query) {
  // Last answer received. It remembers WHICH request it answers (q),
  // so the status below can be computed instead of stored.
  const [result, setResult] = useState(null) // { q, rooms, error }

  const trimmed = query.trim()
  const debouncedQuery = useDebouncedValue(trimmed, DEBOUNCE_MS)

  // Runs only once the user stopped typing. The cleanup cancels the previous
  // request, so a slow old answer can never replace a newer one.
  useEffect(() => {
    if (debouncedQuery === '') return
    const controller = new AbortController()
    searchRooms(debouncedQuery, { signal: controller.signal })
      .then((rooms) => setResult({ q: debouncedQuery, rooms, error: null }))
      .catch((error) => {
        if (error.name === 'AbortError') return // cancelled on purpose
        setResult({ q: debouncedQuery, rooms: null, error: error.message })
      })
    return () => controller.abort()
  }, [debouncedQuery])

  // Status derived from the RAW text: emptying the input hides old results
  // at once, without waiting for the debounce. While the debounce is pending or the
  // answer has not arrived, we report "loading" instead of outdated results.
  let status
  if (trimmed === '') status = 'idle'
  else if (debouncedQuery !== trimmed || result?.q !== debouncedQuery) status = 'loading'
  else status = result.error ? 'error' : 'success'

  return { status, rooms: result?.rooms, error: result?.error }
}
