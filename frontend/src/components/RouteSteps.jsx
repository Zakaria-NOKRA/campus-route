// The French instructions returned by the API, in order.
// `sub` already names the floor (e.g. "RDC" or "RDC → Étage 2"), so we just display it.
function RouteSteps({ steps }) {
  return (
    <ol className="steps">
      {steps.map((step, index) => (
        <li key={index}>
          <span className="ic" aria-hidden="true">
            {step.icon}
          </span>
          <span>
            {step.text}
            <small>{step.sub}</small>
          </span>
        </li>
      ))}
    </ol>
  )
}

export default RouteSteps
