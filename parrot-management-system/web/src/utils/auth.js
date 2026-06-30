export function isAdmin(role) {
  return role === 'ADMIN'
}

export function isKeeper(role) {
  return role === 'KEEPER'
}

export function isCustomer(role) {
  return role === 'CUSTOMER'
}

export function canVisitAdmin(role) {
  return isAdmin(role) || isKeeper(role)
}

export function canVisitAdminOnly(role) {
  return isAdmin(role)
}

export function hasMenu(userStore, code) {
  if (!code) return true
  return (userStore.menuCodes || []).includes(code)
}

export function hasAction(userStore, code) {
  return hasMenu(userStore, code)
}

export function canShowButton(role, code) {
  if (code === 'adminOnly') return isAdmin(role)
  if (code === 'keeperOrAdmin') return canVisitAdmin(role)
  if (code === 'customer') return isCustomer(role)
  return true
}

export function homeByRole(role) {
  if (role === 'ADMIN' || role === 'KEEPER') return '/admin/dashboard'
  return '/portal/home'
}
