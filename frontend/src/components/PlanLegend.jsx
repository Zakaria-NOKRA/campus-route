import MarkerIcon from './MarkerIcon'

// Legend under a building plan: what each room colour means, and the stairs / elevator symbols.
// The classes are the ones returned by roomTypeClass (utils/planGeometry.js).
const CATEGORIES = [
  { className: 't-amph', label: 'Amphithéâtre' },
  { className: 't-td', label: 'Salle de TD' },
  { className: 't-lab', label: 'TP / labo' },
  { className: 't-info', label: 'Informatique' },
  { className: 't-admin', label: 'Administration' },
  { className: 't-commons', label: 'Cafétéria / bibliothèque' },
]

const SYMBOLS = [
  { type: 'STAIRS', label: 'Escalier' },
  { type: 'ELEVATOR', label: 'Ascenseur' },
]

function PlanLegend() {
  return (
    <ul className="legend" aria-label="Légende du plan">
      {CATEGORIES.map((category) => (
        <li key={category.className}>
          <span className={`swatch ${category.className}`} aria-hidden="true" />
          {category.label}
        </li>
      ))}
      {SYMBOLS.map((symbol) => (
        <li key={symbol.type}>
          {/* Same drawing as on the plan, in a small 20 x 20 box */}
          <svg className="legend-icon" viewBox="0 0 20 20" aria-hidden="true" focusable="false">
            <MarkerIcon type={symbol.type} x={10} y={10} size={18} />
          </svg>
          {symbol.label}
        </li>
      ))}
    </ul>
  )
}

export default PlanLegend
