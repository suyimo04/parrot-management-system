import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'
import { useUserStore } from '../store/user'

const service = axios.create({
  baseURL: '/api',
  timeout: 15000
})

service.interceptors.request.use((config) => {
  const userStore = useUserStore()
  if (userStore.token) {
    config.headers.Authorization = `Bearer ${userStore.token}`
  }
  return config
})

service.interceptors.response.use(
  (response) => {
    const res = response.data
    if (!res || typeof res.code === 'undefined') {
      return res
    }
    if (res.code === 200) {
      return res.data
    }
    const message = res.message || '操作失败'
    if (res.code === 401) {
      const userStore = useUserStore()
      userStore.logout()
      ElMessage.warning(message || '登录已过期，请重新登录')
      router.push('/login')
      return Promise.reject(new Error(message))
    }
    if (res.code === 403) {
      ElMessage.error(message || '没有权限访问该功能')
      return Promise.reject(new Error(message))
    }
    ElMessage.error(message)
    return Promise.reject(new Error(message))
  },
  (error) => {
    const message = error.response?.data?.message || error.message || '网络请求失败'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default service
