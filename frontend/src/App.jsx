import { useState } from 'react'
import PlanPage from './pages/PlanPage'
import RoutePage from './pages/RoutePage'
import SearchPage from './pages/SearchPage'
import { OUTDOOR, roomPlace } from './utils/place'
import { useRoute } from './utils/useRoute'

const NO_ROUTE = { status: 'idle', route: null, error: null }

function App() {
  // The ONE place that remembers which room is selected (a room code, or null).
  // The search page and the plan both read it and change it through setSelectedCode.
  const [selectedCode, setSelectedCode] = useState(null)

  // What the plan shows: a building (A, B, C or EXT = outdoor) and a floor.
  // It lives here because the search, the route steps and the plan tabs all change it.
  // At the start: the campus site plan.
  const [view, setView] = useState({ building: OUTDOOR, floor: 0 })

  // Select a room (search list, plan, swap) and show its building and floor.
  // null = unselect: the plan stays where it is.
  function handleSelect(code) {
    setSelectedCode(code)
    if (code !== null) setView(roomPlace(code))
  }

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

  // Click on a step of the route: show the building and floor of that step.
  // Selection and route are untouched.
  function handleStepClick(building, floor) {
    setView({ building, floor })
  }

  function handleCalculate() {
    setRequest({ from, to: selectedCode, accessible })
  }

  // Swap: the origin room becomes the destination and vice versa.
  // Only called when both are rooms (the button is disabled otherwise).
  function handleSwap() {
    setOriginCode(selectedCode)
    handleSelect(originCode)
  }

  return (
    <>
      <header className="app-header">
        <h1>Campus Route</h1>
        <span>Campus : bâtiments A, B et C</span>
      </header>
      <main className="app-main">
        <div className="side">
          <SearchPage selectedCode={selectedCode} onSelect={handleSelect} />
          <RoutePage
            originCode={originCode}
            onOriginChange={setOriginCode}
            destinationCode={selectedCode}
            accessible={accessible}
            onAccessibleChange={setAccessible}
            onCalculate={handleCalculate}
            onSwap={handleSwap}
            onStepClick={handleStepClick}
            routeState={routeState}
          />
        </div>
        <PlanPage
          view={view}
          onViewChange={setView}
          selectedCode={selectedCode}
          onSelect={handleSelect}
          route={routeState.route}
        />
      </main>
    </>
  )
}

export default App
