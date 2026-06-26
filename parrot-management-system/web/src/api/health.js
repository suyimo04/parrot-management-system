import request from '../utils/request'

export function getHealthPage(params) {
  return request.get('/admin/health-record/page', { params })
}

export function addHealth(data) {
  return request.post('/admin/health-record', data)
}

export function updateHealth(id, data) {
  return request.put(`/admin/health-record/${id}`, data)
}

export function deleteHealth(id) {
  return request.delete(`/admin/health-record/${id}`)
}
