import { useEffect, useRef, useState } from 'react'

// "Copier l'itinéraire" button, under the steps.
// It copies the route as plain text (summary, then the numbered steps) to the clipboard,
// so it can be pasted in a message or a note and read on the way.
function CopyRouteButton({ from, to, distanceMeters, durationSeconds, accessible, steps }) {
  const [status, setStatus] = useState('idle') // 'idle' | 'copied' | 'error'
  const timer = useRef(null)

  // If the button disappears (new route, new destination...), cancel the pending timer.
  useEffect(() => () => clearTimeout(timer.current), [])

  function buildText() {
    const meters = Math.round(distanceMeters)
    const minutes = Math.max(1, Math.round(durationSeconds / 60)) // same rounding as RouteSummary
    const lines = [
      'Itinéraire CampusRoute',
      `Départ : ${from === 'ENTREE' ? 'entrée principale' : from}`,
      `Destination : ${to}`,
      `≈ ${meters} m, ≈ ${minutes} min à pied${accessible ? ' (sans escalier)' : ''}`,
      '',
      ...steps.map((step, index) => `${index + 1}. ${step.text}${step.sub ? ` (${step.sub})` : ''}`),
    ]
    return lines.join('\n')
  }

  async function handleClick() {
    clearTimeout(timer.current)
    try {
      await navigator.clipboard.writeText(buildText())
      setStatus('copied')
    } catch {
      // No clipboard access: old browser, or page not served over https.
      setStatus('error')
    }
    // The message disappears after a few seconds.
    timer.current = setTimeout(() => setStatus('idle'), 2500)
  }

  return (
    <>
      {/* Same look as the "Inverser" button (secondary action). */}
      <button type="button" className="swap" onClick={handleClick}>
        Copier l'itinéraire
      </button>
      {status === 'copied' && (
        <p className="hint" role="status">
          Itinéraire copié : vous pouvez le coller dans un message ou une note.
        </p>
      )}
      {status === 'error' && (
        <p className="error" role="alert">
          Copie impossible dans ce navigateur.
        </p>
      )}
    </>
  )
}

export default CopyRouteButton
