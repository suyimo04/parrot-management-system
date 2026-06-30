import { defineStore } from 'pinia'

const USER_KEY = 'parrot_user'

function readLocalUser() {
  const text = localStorage.getItem(USER_KEY)
  if (!text) return {}
  try {
    return JSON.parse(text)
  } catch (e) {
    localStorage.removeItem(USER_KEY)
    return {}
  }
}

export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    userId: '',
    username: '',
    realName: '',
    phone: '',
    email: '',
    role: '',
    menuCodes: [],
    ...readLocalUser(),
    menuLoaded: false
  }),
  getters: {
    isLogin: (state) => Boolean(state.token),
    showName: (state) => state.realName || state.username || '未登录'
  },
  actions: {
    setUser(info) {
      this.token = info.token || this.token
      this.userId = info.userId || info.id || ''
      this.username = info.username || ''
      this.realName = info.realName || ''
      this.phone = info.phone || ''
      this.email = info.email || ''
      this.role = info.role || ''
      this.menuCodes = info.menuCodes || this.menuCodes || []
      this.saveLocal()
    },
    setMenus(codes) {
      this.menuCodes = codes || []
      this.menuLoaded = true
      this.saveLocal()
    },
    updateProfile(info) {
      this.realName = info.realName ?? this.realName
      this.phone = info.phone ?? this.phone
      this.email = info.email ?? this.email
      this.saveLocal()
    },
    saveLocal() {
      localStorage.setItem(USER_KEY, JSON.stringify({
        token: this.token,
        userId: this.userId,
        username: this.username,
        realName: this.realName,
        phone: this.phone,
        email: this.email,
        role: this.role,
        menuCodes: this.menuCodes
      }))
    },
    logout() {
      this.token = ''
      this.userId = ''
      this.username = ''
      this.realName = ''
      this.phone = ''
      this.email = ''
      this.role = ''
      this.menuCodes = []
      this.menuLoaded = false
      localStorage.removeItem(USER_KEY)
    }
  }
})
