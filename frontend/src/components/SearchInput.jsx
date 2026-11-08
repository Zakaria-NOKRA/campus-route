// Controlled text input: the parent owns the text, we only display it and report changes.
function SearchInput({ value, onChange }) {
  return (
    <>
      <label htmlFor="room-search">Destination (code, nom ou type)</label>
      <input
        type="text"
        id="room-search"
        value={value}
        onChange={(event) => onChange(event.target.value)}
        placeholder="ex. B204, amphi, labo…"
        autoComplete="off"
      />
    </>
  )
}

export default SearchInput
