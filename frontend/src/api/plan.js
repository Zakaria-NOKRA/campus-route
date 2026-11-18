import { apiGet } from './client'

// GET /api/plan?building=  -> { building, name, scaleMetersPerPixel, width, height, outline,
// floors, rooms, nodes, edges }. `building` is A, B, C or EXT (outdoor space).
export function getPlan(building, options) {
  return apiGet('/api/plan', { building }, options)
}
