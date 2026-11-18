import { apiGet } from './client'

// GET /api/buildings  -> [{ code, name, outdoor, floors, width, height, outline, siteOutline }]
// Buildings A, B, C sorted by code, then the outdoor space (code EXT, outdoor = true).
export function getBuildings(options) {
  return apiGet('/api/buildings', {}, options)
}
