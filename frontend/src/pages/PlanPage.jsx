import { useEffect, useState } from 'react'
import { getBuildings } from '../api/buildings'
import BuildingTabs from '../components/BuildingTabs'
import FloorTabs from '../components/FloorTabs'
import PlanContent from '../components/PlanContent'
import { routeFloors } from '../utils/routeGeometry'
import { usePlan } from '../utils/usePlan'

// Plan card: building tabs, floor tabs, then the drawing of `view` = { building, floor }.
// The view is owned by App (the search, the route and the steps change it too):
// here we only ask for a change with onViewChange.
function PlanPage({ view, onViewChange, selectedCode, onSelect, route }) {
  // Buildings are loaded once. Last answer received: { buildings, error }, null = still waiting.
  const [buildingsResult, setBuildingsResult] = useState(null)
  // Plan of the building shown, fetched again each time the building changes.
  const planState = usePlan(view.building)

  useEffect(() => {
    const controller = new AbortController()
    getBuildings({ signal: controller.signal })
      .then((buildings) => setBuildingsResult({ buildings, error: null }))
      .catch((error) => {
        if (error.name === 'AbortError') return // cancelled on purpose
        setBuildingsResult({ buildings: null, error: error.message })
      })
    return () => controller.abort()
  }, [])

  const buildings = buildingsResult?.buildings ?? []
  const building = buildings.find((item) => item.code === view.building)

  // Opening another building: show the first floor of it crossed by the route, else its lowest floor.
  function handleBuildingChange(code) {
    const target = buildings.find((item) => item.code === code)
    const floorsOnRoute = route ? routeFloors(route.path, code) : []
    onViewChange({ building: code, floor: floorsOnRoute[0] ?? target.floors[0] ?? 0 })
  }

  function handleFloorChange(floor) {
    onViewChange({ building: view.building, floor })
  }

  let content
  if (buildingsResult === null) {
    content = (
      <p className="hint" role="status">
        Chargement des bâtiments…
      </p>
    )
  } else if (buildingsResult.error) {
    content = (
      <p className="error" role="alert">
        {buildingsResult.error}
      </p>
    )
  } else if (buildings.length === 0) {
    content = (
      <p className="hint" role="status">
        Aucun bâtiment disponible.
      </p>
    )
  } else {
    content = (
      <>
        <BuildingTabs buildings={buildings} current={view.building} onChange={handleBuildingChange} />
        {/* The outdoor space has a single level: no floor tabs. */}
        {building && !building.outdoor && (
          <FloorTabs
            floors={building.floors}
            current={view.floor}
            onChange={handleFloorChange}
            markedFloors={route ? routeFloors(route.path, view.building) : []}
          />
        )}
        {/* The tabs stay visible while the plan of a building loads. */}
        <PlanContent
          planState={planState}
          floor={view.floor}
          selectedCode={selectedCode}
          onSelect={onSelect}
          route={route}
          buildings={buildings}
          onOpenBuilding={handleBuildingChange}
        />
      </>
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
