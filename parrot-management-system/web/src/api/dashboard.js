import request from '../utils/request'

export function getDashboardStats() {
  return request.get('/admin/dashboard/stats')
}

export function getDashboardCharts() {
  return request.get('/admin/dashboard/charts')
}
