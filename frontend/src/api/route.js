import { apiGet } from './client'

// GET /api/route?from=&to=&accessible=
// -> { distanceMeters, durationSeconds, path: [nodes], steps: [instructions] }
// `from` is a room code or 'ENTREE'. Errors (unknown room, no route...) come as an
// ApiError whose message is already in French.
export function getRoute(from, to, accessible, options) {
  return apiGet('/api/route', { from, to, accessible }, options)
}
