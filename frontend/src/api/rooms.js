import { apiGet } from './client'

// GET /api/rooms/search?q=  -> array of rooms (max 8)
export function searchRooms(q, options) {
  return apiGet('/api/rooms/search', { q }, options)
}

// GET /api/rooms/{code}  -> one room, or an ApiError 404 if unknown
export function getRoom(code, options) {
  return apiGet(`/api/rooms/${encodeURIComponent(code)}`, {}, options)
}
