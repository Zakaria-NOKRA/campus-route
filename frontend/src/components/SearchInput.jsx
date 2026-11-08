// Controlled text input: the parent owns the text, we only display it and report changes.
// `label` and `id` are props so the page can have several search fields (unique ids).
function SearchInput({ id, label, value, onChange }) {
  return (
    <>
      <label htmlFor={id}>{label}</label>
      <input
        type="text"
        id={id}
        value={value}
        onChange={(event) => onChange(event.target.value)}
        placeholder="ex. B204, amphi, labo…"
        autoComplete="off"
      />
    </>
  )
}

export default SearchInput
