import { apiGet } from './client'

// GET /api/plan  -> { scaleMetersPerPixel, floors, rooms, nodes, edges }
export function getPlan(options) {
  return apiGet('/api/plan', {}, options)
}
