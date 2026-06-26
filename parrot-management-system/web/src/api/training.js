import request from '../utils/request'

export function getTrainingPage(params) {
  return request.get('/admin/training-record/page', { params })
}

export function addTraining(data) {
  return request.post('/admin/training-record', data)
}

export function updateTraining(id, data) {
  return request.put(`/admin/training-record/${id}`, data)
}

export function deleteTraining(id) {
  return request.delete(`/admin/training-record/${id}`)
}
