import { buildingLabel } from '../utils/place'
import { outlineTopLeft } from '../utils/planGeometry'

// One building drawn on the campus plan, from its `siteOutline` (its shape on the outdoor plan).
// It behaves like a button: click, or Enter / Space with the keyboard, opens the building.
// `onRoute` = true when the route goes through this building: its border takes the route colour.
// `k` = plan width / 1000, so the label keeps the same size on screen.
function SiteBuilding({ building, onRoute, onOpen, k }) {
  // Label near the top-left corner of the shape: for these L and U shapes the centre of the
  // shape can be outside the building, the top-left corner is always inside.
  const corner = outlineTopLeft(building.siteOutline)

  function handleKeyDown(event) {
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault() // Space would scroll the page otherwise
      onOpen(building.code)
    }
  }

  return (
    <g
      className="site-building"
      role="button"
      tabIndex={0}
      aria-label={`Ouvrir ${building.name}${onRoute ? ", sur l'itinéraire" : ''}`}
      onClick={() => onOpen(building.code)}
      onKeyDown={handleKeyDown}
    >
      <title>{building.name}</title>
      <polygon
        className={onRoute ? 'site-shape on-route' : 'site-shape'}
        points={building.siteOutline}
      />
      <text className="site-code" x={corner.x + 20 * k} y={corner.y + 30 * k}>
        {buildingLabel(building.code)}
      </text>
    </g>
  )
}

export default SiteBuilding
