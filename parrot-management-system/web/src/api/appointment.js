import request from '../utils/request'

export function getAppointmentPage(params) {
  return request.get('/admin/appointment/page', { params })
}

export function confirmAppointment(id, data = {}) {
  return request.put(`/admin/appointment/${id}/confirm`, data)
}

export function finishAppointment(id, data) {
  return request.put(`/admin/appointment/${id}/finish`, data)
}

export function rejectAppointment(id, data) {
  return request.put(`/admin/appointment/${id}/reject`, data)
}

export function cancelAppointmentByAdmin(id, data = {}) {
  return request.put(`/admin/appointment/${id}/cancel`, data)
}
