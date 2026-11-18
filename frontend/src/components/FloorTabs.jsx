import { floorLabel } from '../utils/floorLabel'

// One button per floor. The active one is highlighted (and announced with aria-current).
// `markedFloors` = floors crossed by the route: they get a small dot.
function FloorTabs({ floors, current, onChange, markedFloors = [] }) {
  return (
    <div className="tabs tabs-floor" role="group" aria-label="Étages">
      {floors.map((floor) => (
        <button
          key={floor}
          type="button"
          className={floor === current ? 'on' : ''}
          aria-current={floor === current ? 'true' : undefined}
          onClick={() => onChange(floor)}
        >
          {floorLabel(floor)}
          {markedFloors.includes(floor) && (
            <span className="dot" role="img" aria-label="sur l'itinéraire" />
          )}
        </button>
      ))}
    </div>
  )
}

export default FloorTabs
