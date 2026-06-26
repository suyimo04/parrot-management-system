import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store/user'
import { canVisitAdmin, canVisitAdminOnly, homeByRole, isCustomer } from '../utils/auth'

const Login = () => import('../views/Login.vue')
const Register = () => import('../views/Register.vue')
const AdminLayout = () => import('../layout/AdminLayout.vue')
const PortalLayout = () => import('../layout/PortalLayout.vue')

const routes = [
  { path: '/', redirect: '/portal/home' },
  { path: '/login', component: Login, meta: { title: '登录' } },
  { path: '/register', component: Register, meta: { title: '注册' } },
  {
    path: '/portal',
    component: PortalLayout,
    redirect: '/portal/home',
    children: [
      { path: 'home', component: () => import('../views/portal/Home.vue'), meta: { title: '首页' } },
      { path: 'parrot', component: () => import('../views/portal/ParrotList.vue'), meta: { title: '鹦鹉展示' } },
      { path: 'parrot/:id', component: () => import('../views/portal/ParrotDetail.vue'), meta: { title: '鹦鹉详情' } },
      { path: 'notice', component: () => import('../views/portal/NoticeList.vue'), meta: { title: '公告知识' } },
      { path: 'notice/:id', component: () => import('../views/portal/NoticeDetail.vue'), meta: { title: '公告详情' } },
      { path: 'user', component: () => import('../views/portal/UserCenter.vue'), meta: { title: '个人中心', needLogin: true, customerOnly: true } },
      { path: 'appointment', component: () => import('../views/portal/MyAppointment.vue'), meta: { title: '我的预约', needLogin: true, customerOnly: true } }
    ]
  },
  {
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin/dashboard',
    meta: { needLogin: true, adminArea: true },
    children: [
      { path: 'dashboard', component: () => import('../views/admin/Dashboard.vue'), meta: { title: '首页看板' } },
      { path: 'parrot/list', component: () => import('../views/admin/ParrotManage.vue'), meta: { title: '鹦鹉档案管理' } },
      { path: 'parrot/species', component: () => import('../views/admin/SpeciesManage.vue'), meta: { title: '品种管理' } },
      { path: 'health', component: () => import('../views/admin/HealthRecord.vue'), meta: { title: '健康记录管理' } },
      { path: 'feeding', component: () => import('../views/admin/FeedingRecord.vue'), meta: { title: '喂养记录管理' } },
      { path: 'training', component: () => import('../views/admin/TrainingRecord.vue'), meta: { title: '训练记录管理' } },
      { path: 'appointment', component: () => import('../views/admin/AppointmentManage.vue'), meta: { title: '预约咨询管理' } },
      { path: 'notice', component: () => import('../views/admin/NoticeManage.vue'), meta: { title: '公告知识管理' } },
      { path: 'user', component: () => import('../views/admin/UserManage.vue'), meta: { title: '用户管理', adminOnly: true } },
      { path: 'login-log', component: () => import('../views/admin/LoginLog.vue'), meta: { title: '登录日志', adminOnly: true } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const userStore = useUserStore()
  const role = userStore.role

  if ((to.path === '/login' || to.path === '/register') && userStore.isLogin) {
    return homeByRole(role)
  }

  if (to.meta.needLogin && !userStore.isLogin) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  if (to.matched.some(record => record.meta.adminArea)) {
    if (!canVisitAdmin(role)) {
      ElMessage.warning('当前账号不能进入后台')
      return homeByRole(role)
    }
  }

  if (to.meta.adminOnly && !canVisitAdminOnly(role)) {
    ElMessage.warning('该页面仅管理员可访问')
    return '/admin/dashboard'
  }

  if (to.meta.customerOnly && !isCustomer(role)) {
    ElMessage.warning('请使用客户账号访问该页面')
    return homeByRole(role)
  }

  return true
})

export default router
