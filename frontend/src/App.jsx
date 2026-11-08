import PlanPage from './pages/PlanPage'
import SearchPage from './pages/SearchPage'

function App() {
  return (
    <>
      <header className="app-header">
        <h1>Campus Route</h1>
        <span>Bâtiment B, 3 étages</span>
      </header>
      <SearchPage />
      {/* Temporary place: under the search. The final layout comes in step 4. */}
      <div className="app-plan">
        <PlanPage />
      </div>
    </>
  )
}

export default App
