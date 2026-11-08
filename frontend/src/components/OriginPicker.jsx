import { useState } from 'react'
import { useRoomSearch } from '../utils/useRoomSearch'
import RoomList from './RoomList'
import SearchInput from './SearchInput'

// Choice of the starting point. `originCode` is a room code, or null for the main entrance.
// It reuses the same room search as the destination (useRoomSearch + RoomList).
function OriginPicker({ originCode, onChange }) {
  const [query, setQuery] = useState('') // text typed in the origin search field
  const search = useRoomSearch(query)

  function handleSelect(code) {
    onChange(code)
    setQuery('') // hide the results once a room is chosen
  }

  return (
    <div>
      <p className="route-point">
        Départ : <b>{originCode ?? 'Entrée principale'}</b>
        {originCode !== null && (
          <button type="button" className="link" onClick={() => onChange(null)}>
            Revenir à l'entrée
          </button>
        )}
      </p>
      <SearchInput
        id="origin-search"
        label="Changer le départ (code, nom ou type)"
        value={query}
        onChange={setQuery}
      />
      {/* No results block while the field is empty: the instruction is already in the label. */}
      {search.status !== 'idle' && (
        <RoomList
          status={search.status}
          rooms={search.rooms}
          error={search.error}
          selectedCode={originCode}
          onSelect={handleSelect}
        />
      )}
    </div>
  )
}

export default OriginPicker
