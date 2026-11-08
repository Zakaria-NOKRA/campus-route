import RouteForm from '../components/RouteForm'

// Route card: the form, then loading / error / result. `routeState` is { status, route, error }
// and is "idle" whenever the form no longer matches the last calculation.
function RoutePage({
  originCode,
  onOriginChange,
  destinationCode,
  accessible,
  onAccessibleChange,
  onCalculate,
  routeState,
}) {
  let content = null
  if (routeState.status === 'loading') {
    content = <p className="hint">Calcul de l'itinéraire…</p>
  } else if (routeState.status === 'error') {
    content = (
      <p className="error" role="alert">
        {routeState.error}
      </p>
    )
  } else if (routeState.status === 'success') {
    const { route } = routeState
    // Temporary line: the real summary and the step list come in the next step.
    content = (
      <p className="hint">
        {route.distanceMeters} m · {route.durationSeconds} s · {route.steps.length} étapes
      </p>
    )
  }

  return (
    <section className="card">
      <h2>Itinéraire</h2>
      <RouteForm
        originCode={originCode}
        onOriginChange={onOriginChange}
        destinationCode={destinationCode}
        accessible={accessible}
        onAccessibleChange={onAccessibleChange}
        onCalculate={onCalculate}
      />
      {content}
    </section>
  )
}

export default RoutePage
