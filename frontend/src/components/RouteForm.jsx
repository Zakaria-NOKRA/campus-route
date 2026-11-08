import OriginPicker from './OriginPicker'

// Route form: origin, destination (the room selected elsewhere), "accessible" option, button.
// It owns no state: everything comes from App through props.
function RouteForm({
  originCode,
  onOriginChange,
  destinationCode,
  accessible,
  onAccessibleChange,
  onCalculate,
  onSwap,
}) {
  const from = originCode ?? 'ENTREE'

  // Swapping needs two rooms. The entrance cannot become a destination.
  const canSwap = originCode !== null && destinationCode !== null

  // Why the button is disabled (null = it can be used).
  let blockedReason = null
  if (destinationCode === null) {
    blockedReason = 'Choisissez une destination (recherche ou plan), puis calculez l\'itinéraire.'
  } else if (from === destinationCode) {
    blockedReason = 'Le départ et la destination sont identiques.'
  }

  function handleSubmit(event) {
    event.preventDefault() // no page reload
    if (blockedReason === null) onCalculate()
  }

  return (
    <form onSubmit={handleSubmit}>
      <OriginPicker originCode={originCode} onChange={onOriginChange} />

      <button
        type="button"
        className="swap"
        disabled={!canSwap}
        onClick={onSwap}
        title={canSwap ? undefined : "Inversion possible quand le départ et la destination sont des salles."}
      >
        ⇅ Inverser départ et destination
      </button>

      <p className="route-point">
        Destination : <b>{destinationCode ?? 'aucune'}</b>
      </p>

      <label className="check">
        <input
          type="checkbox"
          checked={accessible}
          onChange={(event) => onAccessibleChange(event.target.checked)}
        />
        Itinéraire accessible (sans escalier)
      </label>

      <button
        type="submit"
        className="primary"
        disabled={blockedReason !== null}
        aria-describedby="route-blocked"
      >
        Calculer l'itinéraire
      </button>
      {blockedReason && (
        <p className="hint" id="route-blocked">
          {blockedReason}
        </p>
      )}
    </form>
  )
}

export default RouteForm
