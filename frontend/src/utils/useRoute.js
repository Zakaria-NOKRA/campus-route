import { useEffect, useState } from 'react'
import { getRoute } from '../api/route'

// Fetches the route for `request` = { from, to, accessible }, or does nothing when it is null.
// Returns { status: 'idle' | 'loading' | 'error' | 'success', route, error }.
export function useRoute(request) {
  // Last answer received. It remembers WHICH request it answers (`key`), so the status
  // below is computed instead of stored: a new request is "loading" until its own answer arrives.
  const [result, setResult] = useState(null) // { key, route, error }

  const from = request?.from
  const to = request?.to
  const accessible = request?.accessible
  const key = request ? `${from}|${to}|${accessible}` : null

  useEffect(() => {
    if (key === null) return
    const controller = new AbortController()
    getRoute(from, to, accessible, { signal: controller.signal })
      .then((route) => setResult({ key, route, error: null }))
      .catch((error) => {
        if (error.name === 'AbortError') return // cancelled on purpose
        setResult({ key, route: null, error: error.message })
      })
    return () => controller.abort() // a new request cancels the previous one
  }, [key, from, to, accessible])

  if (key === null) return { status: 'idle', route: null, error: null }
  if (result?.key !== key) return { status: 'loading', route: null, error: null }
  if (result.error) return { status: 'error', route: null, error: result.error }
  return { status: 'success', route: result.route, error: null }
}
