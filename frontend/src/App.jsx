import { useState } from 'react'
import PlanPage from './pages/PlanPage'
import RoutePage from './pages/RoutePage'
import SearchPage from './pages/SearchPage'
import { useRoute } from './utils/useRoute'

const NO_ROUTE = { status: 'idle', route: null, error: null }

function App() {
  // The ONE place that remembers which room is selected (a room code, or null).
  // The search page and the plan both read it and change it through setSelectedCode.
  const [selectedCode, setSelectedCode] = useState(null)

  // Route form. originCode = null means "main entrance" (sent to the API as ENTREE).
  const [originCode, setOriginCode] = useState(null)
  const [accessible, setAccessible] = useState(false)
  // What was asked at the last click on "Calculer": { from, to, accessible } or null.
  const [request, setRequest] = useState(null)
  const routeResult = useRoute(request)

  const from = originCode ?? 'ENTREE'

  // The route is shown only while the form still matches the request it answers.
  // Changing origin, destination or the option makes them differ: the route disappears
  // by itself, no reset needed.
  const isCurrent =
    request !== null &&
    request.from === from &&
    request.to === selectedCode &&
    request.accessible === accessible
  const routeState = isCurrent ? routeResult : NO_ROUTE

  function handleCalculate() {
    setRequest({ from, to: selectedCode, accessible })
  }

  return (
    <>
      <header className="app-header">
        <h1>Campus Route</h1>
        <span>Bâtiment B, 3 étages</span>
      </header>
      <main className="app-main">
        <div className="side">
          <SearchPage selectedCode={selectedCode} onSelect={setSelectedCode} />
          <RoutePage
            originCode={originCode}
            onOriginChange={setOriginCode}
            destinationCode={selectedCode}
            accessible={accessible}
            onAccessibleChange={setAccessible}
            onCalculate={handleCalculate}
            routeState={routeState}
          />
        </div>
        <PlanPage selectedCode={selectedCode} onSelect={setSelectedCode} />
      </main>
    </>
  )
}

export default App
