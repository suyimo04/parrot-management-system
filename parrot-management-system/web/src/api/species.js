import request from '../utils/request'

export function getSpeciesPage(params) {
  return request.get('/admin/species/page', { params })
}

export function getSpeciesList(params) {
  return request.get('/admin/species/list', { params })
}

export function addSpecies(data) {
  return request.post('/admin/species', data)
}

export function updateSpecies(id, data) {
  return request.put(`/admin/species/${id}`, data)
}

export function deleteSpecies(id) {
  return request.delete(`/admin/species/${id}`)
}
