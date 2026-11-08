import { useState } from 'react'
import {
  CORRIDOR_HEIGHT,
  CORRIDOR_Y,
  ROOM_HEIGHT,
  ROOM_WIDTH,
  SERVICE_WIDTH,
  VIEW_HEIGHT,
  VIEW_WIDTH,
  emptyBottomSlots,
  rowY,
  slotX,
} from '../utils/planGeometry'
import { floorLabel } from '../utils/floorLabel'
import { routeFloors } from '../utils/routeGeometry'
import FloorTabs from './FloorTabs'
import PlanRoom from './PlanRoom'
import RouteLayer from './RouteLayer'

// Stairs or elevator: a box centred on its node, with a short label.
function ServiceBox({ node, label }) {
  return (
    <g>
      <rect
        className="svc"
        x={node.x - SERVICE_WIDTH / 2}
        y={CORRIDOR_Y}
        width={SERVICE_WIDTH}
        height={CORRIDOR_HEIGHT}
        rx="4"
      />
      <text className="lbl" x={node.x} y={node.y + 4} textAnchor="middle">
        {label}
      </text>
    </g>
  )
}

// Floor tabs + the SVG of the chosen floor. `plan` is the answer of GET /api/plan.
// `route` is the answer of GET /api/route, or null when there is no route to show.
// `floorRequest` = { floor } sent when a step of the route is clicked, or null.
function FloorPlan({ plan, selectedCode, onSelect, route, floorRequest }) {
  const selectedRoom = plan.rooms.find((room) => room.code === selectedCode)

  // The floor being shown is view state: it lives here, not in the page.
  // At the start, show the floor of the selected room (if any).
  const [floor, setFloor] = useState(selectedRoom?.floor ?? plan.floors[0] ?? 0)

  // When the selection changes (from the search list or from a click on the plan),
  // jump to the floor of the new room. We compare with the previous code during
  // render instead of using an effect: React re-renders at once, with no flicker.
  // Clicking a floor tab never changes the selection.
  const [previousCode, setPreviousCode] = useState(selectedCode)
  if (selectedCode !== previousCode) {
    setPreviousCode(selectedCode)
    if (selectedRoom) setFloor(selectedRoom.floor)
  }

  // Same idea when a new route arrives: show the floor where it starts.
  // A new calculation gives a new `route` object, so this runs once per route.
  const [previousRoute, setPreviousRoute] = useState(route)
  if (route !== previousRoute) {
    setPreviousRoute(route)
    if (route) setFloor(route.path[0].floor)
  }

  // And when a step of the route is clicked. Each click makes a new `floorRequest` object.
  const [previousFloorRequest, setPreviousFloorRequest] = useState(floorRequest)
  if (floorRequest !== previousFloorRequest) {
    setPreviousFloorRequest(floorRequest)
    if (floorRequest) setFloor(floorRequest.floor)
  }

  const rooms = plan.rooms.filter((room) => room.floor === floor)
  const nodes = plan.nodes.filter((node) => node.floor === floor)
  const stairs = nodes.find((node) => node.type === 'STAIRS')
  const elevator = nodes.find((node) => node.type === 'ELEVATOR')
  const hasEntrance = nodes.some((node) => node.type === 'ENTRANCE')

  // The corridor fills the gap between the stairs box and the elevator box.
  const corridorX = stairs ? stairs.x + SERVICE_WIDTH / 2 : 0
  const corridorEnd = elevator ? elevator.x - SERVICE_WIDTH / 2 : VIEW_WIDTH

  return (
    <>
      <FloorTabs
        floors={plan.floors}
        current={floor}
        onChange={setFloor}
        markedFloors={route ? routeFloors(route.path) : []}
      />
      <div className="plan-scroll">
        <svg
          className="plan-svg"
          viewBox={`0 0 ${VIEW_WIDTH} ${VIEW_HEIGHT}`}
          role="group"
          aria-label={`Plan : ${floorLabel(floor)}`}
        >
          <rect
            className="corr"
            x={corridorX}
            y={CORRIDOR_Y}
            width={corridorEnd - corridorX}
            height={CORRIDOR_HEIGHT}
          />
          {stairs && <ServiceBox node={stairs} label="Esc." />}
          {elevator && <ServiceBox node={elevator} label="Asc." />}

          {rooms.map((room) => (
            <PlanRoom
              key={room.code}
              room={room}
              selected={room.code === selectedCode}
              onSelect={onSelect}
            />
          ))}

          {/* Free spot of the bottom row: entrance hall on the ground floor, empty atrium above. */}
          {emptyBottomSlots(rooms).map((slot) => {
            const cx = slotX(slot)
            const x = cx - ROOM_WIDTH / 2
            const y = rowY(false)
            return hasEntrance ? (
              <g key={`hall-${slot}`}>
                <rect
                  className="room t-serv"
                  x={x}
                  y={y}
                  width={ROOM_WIDTH}
                  height={ROOM_HEIGHT}
                  rx="6"
                />
                <text className="tag" x={cx} y={y + 26} textAnchor="middle">
                  Hall d'entrée
                </text>
                <text className="code" x={cx} y={y + 104} textAnchor="middle">
                  ▲ Entrée
                </text>
              </g>
            ) : (
              <g key={`atrium-${slot}`}>
                <rect
                  className="atrium"
                  x={x}
                  y={y}
                  width={ROOM_WIDTH}
                  height={ROOM_HEIGHT}
                  rx="6"
                />
                <text className="tag" x={cx} y={y + 60} textAnchor="middle">
                  Atrium (vide)
                </text>
              </g>
            )
          })}

          {route && <RouteLayer path={route.path} floor={floor} />}
        </svg>
      </div>
      <p className="hint">
        Cliquez sur une salle pour la sélectionner. Sur un petit écran, faites
        glisser le plan pour voir toute la largeur.
      </p>
    </>
  )
}

export default FloorPlan
