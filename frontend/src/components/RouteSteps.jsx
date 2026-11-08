import { useState } from 'react'

// The French instructions returned by the API, in order.
// `sub` already names the floor (e.g. "RDC" or "RDC → Étage 2"), so we just display it.
// Each step is a button: clicking it asks the plan to show the floor of that step.
// It changes neither the selection nor the route.
function RouteSteps({ steps, onStepClick }) {
  const [activeIndex, setActiveIndex] = useState(-1) // only to highlight the last clicked step

  function handleClick(index, floor) {
    setActiveIndex(index)
    onStepClick(floor)
  }

  return (
    <ol className="steps">
      {steps.map((step, index) => (
        <li key={index}>
          <button
            type="button"
            className={index === activeIndex ? 'step active' : 'step'}
            onClick={() => handleClick(index, step.floor)}
          >
            <span className="ic" aria-hidden="true">
              {step.icon}
            </span>
            <span>
              {step.text}
              <small>{step.sub}</small>
            </span>
          </button>
        </li>
      ))}
    </ol>
  )
}

export default RouteSteps
