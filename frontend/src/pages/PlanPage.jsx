import { useEffect, useState } from 'react'
import { getPlan } from '../api/plan'

// Loads the building plan once and shows loading, error, empty or (for now) a summary.
// The drawing itself comes in the next steps.
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
    const { rooms, floors } = result.plan
    content = (
      <p className="hint">
        {rooms.length} salles, {floors.length} étages
      </p>
    )
  }

  return (
    <section className="card">
      <h2>Plan</h2>
      {content}
    </section>
  )
}

export default PlanPage
