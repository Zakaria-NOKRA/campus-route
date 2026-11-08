import { useEffect, useState } from 'react'
import { getPlan } from '../api/plan'
import FloorPlan from '../components/FloorPlan'

// Loads the building plan once and shows loading, error, empty or the drawn plan.
function PlanPage() {
  // Last answer received: { plan, error }. null means "still waiting".
  const [result, setResult] = useState(null)

  useEffect(() => {
    const controller = new AbortController()
    getPlan({ signal: controller.signal })
      .then((plan) => setResult({ plan, error: null }))
      .catch((error) => {
        if (error.name === 'AbortError') return // cancelled on purpose
        setResult({ plan: null, error: error.message })
      })
    return () => controller.abort()
  }, [])

  let content
  if (result === null) {
    content = <p className="hint">Chargement du plan…</p>
  } else if (result.error) {
    content = (
      <p className="error" role="alert">
        {result.error}
      </p>
    )
  } else if (result.plan.rooms.length === 0) {
    content = <p className="hint">Aucun plan disponible.</p>
  } else {
    // Floor 0 only for now; floor tabs come in the next step.
    content = <FloorPlan plan={result.plan} floor={0} />
  }

  return (
    <section className="card">
      <h2>Plan</h2>
      {content}
    </section>
  )
}

export default PlanPage
