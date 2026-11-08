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
import PlanRoom from './PlanRoom'

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

// Draws ONE floor of the building as SVG. `plan` is the answer of GET /api/plan.
function FloorPlan({ plan, floor }) {
  const rooms = plan.rooms.filter((room) => room.floor === floor)
  const nodes = plan.nodes.filter((node) => node.floor === floor)
  const stairs = nodes.find((node) => node.type === 'STAIRS')
  const elevator = nodes.find((node) => node.type === 'ELEVATOR')
  const hasEntrance = nodes.some((node) => node.type === 'ENTRANCE')

  // The corridor fills the gap between the stairs box and the elevator box.
  const corridorX = stairs ? stairs.x + SERVICE_WIDTH / 2 : 0
  const corridorEnd = elevator ? elevator.x - SERVICE_WIDTH / 2 : VIEW_WIDTH

  return (
    <div className="plan-scroll">
      <svg
        className="plan-svg"
        viewBox={`0 0 ${VIEW_WIDTH} ${VIEW_HEIGHT}`}
        role="img"
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
          <PlanRoom key={room.code} room={room} />
        ))}

        {/* Free spot of the bottom row: entrance hall on the ground floor, empty atrium above. */}
        {emptyBottomSlots(rooms).map((slot) => {
          const cx = slotX(slot)
          const x = cx - ROOM_WIDTH / 2
          const y = rowY(false)
          return hasEntrance ? (
            <g key={`hall-${slot}`}>
              <rect className="room t-serv" x={x} y={y} width={ROOM_WIDTH} height={ROOM_HEIGHT} rx="6" />
              <text className="tag" x={cx} y={y + 26} textAnchor="middle">
                Hall d'entrée
              </text>
              <text className="code" x={cx} y={y + 104} textAnchor="middle">
                ▲ Entrée
              </text>
            </g>
          ) : (
            <g key={`atrium-${slot}`}>
              <rect className="atrium" x={x} y={y} width={ROOM_WIDTH} height={ROOM_HEIGHT} rx="6" />
              <text className="tag" x={cx} y={y + 60} textAnchor="middle">
                Atrium (vide)
              </text>
            </g>
          )
        })}
      </svg>
    </div>
  )
}

export default FloorPlan
