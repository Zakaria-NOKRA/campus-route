import { useState } from 'react'
import PlanPage from './pages/PlanPage'
import SearchPage from './pages/SearchPage'

function App() {
  // The ONE place that remembers which room is selected (a room code, or null).
  // The search page and the plan both read it and change it through setSelectedCode.
  const [selectedCode, setSelectedCode] = useState(null)

  return (
    <>
      <header className="app-header">
        <h1>Campus Route</h1>
        <span>Bâtiment B, 3 étages</span>
      </header>
      <main className="app-main">
        <div className="side">
          <SearchPage selectedCode={selectedCode} onSelect={setSelectedCode} />
        </div>
        <PlanPage selectedCode={selectedCode} onSelect={setSelectedCode} />
      </main>
    </>
  )
}

export default App
