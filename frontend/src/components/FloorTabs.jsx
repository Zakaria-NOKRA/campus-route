import { floorLabel } from '../utils/floorLabel'

// One button per floor. The active one is highlighted (and announced with aria-pressed).
// `markedFloors` = floors crossed by the route: they get a small dot.
function FloorTabs({ floors, current, onChange, markedFloors = [] }) {
  return (
    <div className="tabs">
      {floors.map((floor) => (
        <button
          key={floor}
          type="button"
          className={floor === current ? 'on' : ''}
          aria-pressed={floor === current}
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
