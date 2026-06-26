import request from '../utils/request'

export function getProfile() {
  return request.get('/portal/user/profile')
}

export function updateProfile(data) {
  return request.put('/portal/user/profile', data)
}

export function submitAppointment(data) {
  return request.post('/portal/appointment', data)
}

export function getMyAppointments(params) {
  return request.get('/portal/appointment/my', { params })
}

export function cancelMyAppointment(id) {
  return request.put(`/portal/appointment/${id}/cancel`)
}
