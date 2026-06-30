import request from '../utils/request'

export function getMenuTree() {
  return request.get('/admin/menu/tree')
}

export function getCurrentMenus() {
  return request.get('/admin/menu/current')
}

export function getRoleMenus(role) {
  return request.get(`/admin/menu/role/${role}`)
}

export function saveRoleMenus(data) {
  return request.put('/admin/menu/role', data)
}

export function getUserMenus(userId) {
  return request.get(`/admin/menu/user/${userId}`)
}

export function saveUserMenus(data) {
  return request.put('/admin/menu/user', data)
}
