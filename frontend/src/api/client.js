// Base URL of the back-end. Can be changed with VITE_API_URL (see .env files), default = local back-end.
const BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

// An error coming from the API (or from the network). The UI only needs `message`.
export class ApiError extends Error {
  constructor(message, status) {
    super(message)
    this.name = 'ApiError'
    this.status = status // HTTP status, or 0 when the server could not be reached
  }
}

// GET <BASE_URL><path>?<params> and return the parsed JSON.
// `signal` (optional) lets the caller cancel the request.
export async function apiGet(path, params = {}, { signal } = {}) {
  const url = new URL(path, BASE_URL)
  for (const [key, value] of Object.entries(params)) {
    url.searchParams.set(key, value) // encodes special characters for us
  }

  let response
  try {
    response = await fetch(url, { signal })
  } catch (error) {
    if (error.name === 'AbortError') throw error // cancelled on purpose: not a real error
    throw new ApiError('Impossible de joindre le serveur. Vérifiez qu\'il est démarré.', 0)
  }

  if (!response.ok) {
    // The API answers errors as { status, message } (message already in French).
    let message = `Erreur ${response.status}`
    try {
      const body = await response.json()
      if (body.message) message = body.message
    } catch {
      // body is not JSON: keep the generic message
    }
    throw new ApiError(message, response.status)
  }

  return response.json()
}
