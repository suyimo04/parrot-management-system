import request from '../utils/request'

export function getLoginLogPage(params) {
  return request.get('/admin/login-log/page', { params })
}
