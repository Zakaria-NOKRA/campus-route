import FloorPlan from './FloorPlan'

// Loading, error, empty or the drawn plan. `planState` = { status, plan, error } from usePlan.
function PlanContent({ planState, floor, selectedCode, onSelect, route }) {
  if (planState.status === 'loading') {
    return (
      <p className="hint" role="status">
        Chargement du plan…
      </p>
    )
  }
  if (planState.status === 'error') {
    return (
      <p className="error" role="alert">
        {planState.error}
      </p>
    )
  }
  const { plan } = planState
  // The outdoor plan has no rooms but has nodes (paths): only "nothing at all" is empty.
  if (plan.rooms.length === 0 && plan.nodes.length === 0) {
    return (
      <p className="hint" role="status">
        Aucun plan disponible.
      </p>
    )
  }
  return (
    <FloorPlan
      plan={plan}
      floor={floor}
      selectedCode={selectedCode}
      onSelect={onSelect}
      route={route}
    />
  )
}

export default PlanContent
