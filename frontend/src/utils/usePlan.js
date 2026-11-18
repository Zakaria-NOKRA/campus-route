import { useEffect, useState } from 'react'
import { getPlan } from '../api/plan'

// Fetches the plan of one building (A, B, C or EXT) each time `building` changes.
// Returns { status: 'loading' | 'error' | 'success', plan, error }.
export function usePlan(building) {
  // Last answer received. It remembers WHICH building it answers, so the status below
  // is computed instead of stored: a new building is "loading" until its own answer arrives.
  const [result, setResult] = useState(null) // { building, plan, error }

  useEffect(() => {
    const controller = new AbortController()
    getPlan(building, { signal: controller.signal })
      .then((plan) => setResult({ building, plan, error: null }))
      .catch((error) => {
        if (error.name === 'AbortError') return // cancelled on purpose
        setResult({ building, plan: null, error: error.message })
      })
    return () => controller.abort() // switching building cancels the previous request
  }, [building])

  if (result?.building !== building) return { status: 'loading', plan: null, error: null }
  if (result.error) return { status: 'error', plan: null, error: result.error }
  return { status: 'success', plan: result.plan, error: null }
}
