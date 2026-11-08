import { floorLabel } from '../utils/floorLabel'

// One button per floor. The active one is highlighted (and announced with aria-pressed).
function FloorTabs({ floors, current, onChange }) {
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
        </button>
      ))}
    </div>
  )
}

export default FloorTabs
