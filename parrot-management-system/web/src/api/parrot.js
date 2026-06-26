import request from '../utils/request'

export function getParrotPage(params) {
  return request.get('/admin/parrot/page', { params })
}

export function addParrot(data) {
  return request.post('/admin/parrot', data)
}

export function updateParrot(id, data) {
  return request.put(`/admin/parrot/${id}`, data)
}

export function deleteParrot(id) {
  return request.delete(`/admin/parrot/${id}`)
}
