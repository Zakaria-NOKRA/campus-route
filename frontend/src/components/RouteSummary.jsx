// Total distance and estimated duration of a route (fields of the API answer).
function RouteSummary({ distanceMeters, durationSeconds, accessible }) {
  const meters = Math.round(distanceMeters)
  const minutes = Math.max(1, Math.round(durationSeconds / 60)) // never show "0 min"

  return (
    <p className="route-summary">
      <b>≈ {meters} m</b> · environ {minutes} min
      {accessible && <span className="badge">sans escalier</span>}
    </p>
  )
}

export default RouteSummary
