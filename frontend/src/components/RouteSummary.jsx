// Total distance and estimated duration of a route (fields of the API answer),
// shown as two big numbers side by side, then the "sans escalier" badge if needed.
function RouteSummary({ distanceMeters, durationSeconds, accessible }) {
  const meters = Math.round(distanceMeters)
  const minutes = Math.max(1, Math.round(durationSeconds / 60)) // never show "0 min"

  return (
    <div className="route-summary">
      <p className="stat">
        <span className="stat-value">≈ {meters} m</span>
        <span className="stat-label">distance</span>
      </p>
      <p className="stat">
        <span className="stat-value">≈ {minutes} min</span>
        <span className="stat-label">à pied</span>
      </p>
      {accessible && <span className="badge">sans escalier</span>}
    </div>
  )
}

export default RouteSummary
