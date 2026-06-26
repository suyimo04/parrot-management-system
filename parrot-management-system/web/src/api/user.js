import request from '../utils/request'

export function getUserPage(params) {
  return request.get('/admin/user/page', { params })
}

export function addUser(data) {
  return request.post('/admin/user', data)
}

export function updateUser(id, data) {
  return request.put(`/admin/user/${id}`, data)
}

export function disableUser(id) {
  return request.delete(`/admin/user/${id}`)
}

export function resetUserPassword(id) {
  return request.put(`/admin/user/${id}/reset-password`)
}
