// Top bar: a small map-pin icon (drawn here, not a logo), the title and the university name.
// The icon is decorative: aria-hidden, the title already says everything.
function AppHeader() {
  return (
    <header className="app-header">
      <span className="app-logo" aria-hidden="true">
        <svg viewBox="0 0 24 24" focusable="false">
          {/* soft shadow on the ground, under the tip of the pin */}
          <ellipse cx="12" cy="21.3" rx="4" ry="1.2" fill="#0f766e" opacity="0.25" />
          {/* the pin: a round head that narrows to a point at the bottom */}
          <path
            d="M12 2.2c-3.9 0-7 3-7 6.9 0 4.8 7 11.6 7 11.6s7-6.8 7-11.6c0-3.9-3.1-6.9-7-6.9z"
            fill="#0f766e"
          />
          <circle cx="12" cy="9.1" r="2.7" fill="#ffffff" />
        </svg>
      </span>
      <div className="app-title">
        <h1>Plan du campus</h1>
        <p>Université Sorbonne Paris Nord</p>
      </div>
    </header>
  )
}

export default AppHeader
