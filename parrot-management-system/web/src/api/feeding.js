import request from '../utils/request'

export function getFeedingPage(params) {
  return request.get('/admin/feeding-record/page', { params })
}

export function addFeeding(data) {
  return request.post('/admin/feeding-record', data)
}

export function updateFeeding(id, data) {
  return request.put(`/admin/feeding-record/${id}`, data)
}

export function deleteFeeding(id) {
  return request.delete(`/admin/feeding-record/${id}`)
}
