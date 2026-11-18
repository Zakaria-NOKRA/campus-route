import { buildingLabel } from '../utils/place'

// One button per building, plus "Extérieur" for the outdoor space. Same look as the floor tabs.
// `buildings` is the answer of GET /api/buildings, `current` the code of the building shown.
// `markedBuildings` = codes of the buildings crossed by the route: they get a small dot.
function BuildingTabs({ buildings, current, onChange, markedBuildings = [] }) {
  return (
    <div className="tabs tabs-building" role="group" aria-label="Bâtiments">
      {buildings.map((building) => (
        <button
          key={building.code}
          type="button"
          className={building.code === current ? 'on' : ''}
          aria-current={building.code === current ? 'true' : undefined}
          title={building.name}
          onClick={() => onChange(building.code)}
        >
          {buildingLabel(building.code)}
          {markedBuildings.includes(building.code) && (
            <span className="dot" role="img" aria-label="sur l'itinéraire" />
          )}
        </button>
      ))}
    </div>
  )
}

export default BuildingTabs
