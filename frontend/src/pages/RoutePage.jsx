import RouteForm from '../components/RouteForm'
import RouteSteps from '../components/RouteSteps'
import RouteSummary from '../components/RouteSummary'

// Route card: the form, then loading / error / result. `routeState` is { status, route, error }
// and is "idle" whenever the form no longer matches the last calculation.
function RoutePage({
  originCode,
  onOriginChange,
  destinationCode,
  accessible,
  onAccessibleChange,
  onCalculate,
  onSwap,
  onStepClick,
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
    if (route.path.length < 2) {
      // Empty result: nowhere to go (the form already blocks this case).
      content = <p className="hint">Vous êtes déjà sur place.</p>
    } else {
      content = (
        <>
          <RouteSummary
            distanceMeters={route.distanceMeters}
            durationSeconds={route.durationSeconds}
            accessible={accessible}
          />
          <RouteSteps steps={route.steps} onStepClick={onStepClick} />
        </>
      )
    }
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
        onSwap={onSwap}
      />
      {content}
    </section>
  )
}

export default RoutePage
