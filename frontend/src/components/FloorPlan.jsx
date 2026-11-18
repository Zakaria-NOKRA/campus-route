import { floorLabel } from '../utils/floorLabel'
import { OUTDOOR, buildingLabel } from '../utils/place'
import { floorContents } from '../utils/planGeometry'
import PlanEdges from './PlanEdges'
import PlanMarkers from './PlanMarkers'
import PlanRoom from './PlanRoom'
import RouteLayer from './RouteLayer'
import SiteBuilding from './SiteBuilding'

// The SVG of one floor of one plan (a building, or the outdoor space EXT).
// `plan` is the answer of GET /api/plan?building=, `floor` the floor to draw.
// `route` is the answer of GET /api/route, or null when there is no route to show.
// The campus plan (EXT) uses the same drawing, plus the buildings (`buildings` = answer of
// GET /api/buildings), which open with `onOpenBuilding(code)`.
// Drawing order (later = on top): walls, corridors or paths, buildings, rooms, markers, route.
function FloorPlan({ plan, floor, selectedCode, onSelect, route, buildings, onOpenBuilding }) {
  const { rooms, nodes, edges } = floorContents(plan, floor)
  const isOutdoor = plan.building === OUTDOOR

  // Plans have different sizes (about 1000 px wide for a building, 3000 px for the campus).
  // --k scales texts and line widths in the CSS, so they look the same size on every plan.
  const k = plan.width / 1000
  const svgStyle = { '--k': k }

  const label = isOutdoor
    ? `Plan : ${buildingLabel(plan.building)}`
    : `Plan : ${buildingLabel(plan.building)}, ${floorLabel(floor)}`

  return (
    <>
      <div className="plan-scroll">
        <svg
          className="plan-svg"
          viewBox={`0 0 ${plan.width} ${plan.height}`}
          style={svgStyle}
          role="group"
          aria-label={label}
        >
          {/* outline = "x1,y1 x2,y2 ...": exactly the format of the SVG points attribute */}
          <polygon className={isOutdoor ? 'site' : 'wall'} points={plan.outline} />

          <PlanEdges edges={edges} k={k} />

          {isOutdoor &&
            buildings
              .filter((building) => !building.outdoor)
              .map((building) => (
                <SiteBuilding key={building.code} building={building} onOpen={onOpenBuilding} k={k} />
              ))}

          {rooms.map((room) => (
            <PlanRoom
              key={room.code}
              room={room}
              selected={room.code === selectedCode}
              onSelect={onSelect}
            />
          ))}

          <PlanMarkers nodes={nodes} k={k} />

          {route && <RouteLayer path={route.path} building={plan.building} floor={floor} k={k} />}
        </svg>
      </div>
      <p className="hint">
        {isOutdoor
          ? 'Cliquez sur un bâtiment pour voir ses étages.'
          : selectedCode === null
            ? 'Cliquez sur une salle du plan pour la choisir comme destination.'
            : 'Sur un petit écran, faites glisser le plan pour voir toute la largeur.'}
      </p>
    </>
  )
}

export default FloorPlan
