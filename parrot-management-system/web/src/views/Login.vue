<template>
  <div class="auth-page">
    <section class="auth-card">
      <div class="auth-head">
        <el-icon><Star /></el-icon>
        <h1>鹦鹉管理系统</h1>
        <p>请输入账号信息进入系统</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="submit">
        <el-form-item label="用户名" prop="username">
          <el-input v-model.trim="form.username" placeholder="如 admin、keeper、customer" :prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" :prefix-icon="Lock" />
        </el-form-item>
        <el-button class="auth-submit" type="primary" :loading="loading" @click="submit">登录</el-button>
      </el-form>

      <div class="auth-foot">
        <span>还没有账号？</span>
        <router-link to="/register">注册客户账号</router-link>
      </div>
      <div class="auth-demo">
        默认账号：admin / keeper / customer，密码均为 admin123
      </div>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, Star, User } from '@element-plus/icons-vue'
import { login } from '../api/auth'
import { getCurrentMenus } from '../api/menu'
import { useUserStore } from '../store/user'
import { canVisitAdmin, homeByRole } from '../utils/auth'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
  password: [{ required: true, message: '密码不能为空', trigger: 'blur' }]
}

async function submit() {
  await formRef.value.validate()
  loading.value = true
  try {
    const data = await login(form)
    userStore.setUser(data)
    if (canVisitAdmin(data.role)) {
      const menuData = await getCurrentMenus()
      userStore.setMenus(menuData.codes || [])
    }
    ElMessage.success('登录成功')
    const target = route.query.redirect || homeByRole(data.role)
    router.push(target)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background:
    linear-gradient(rgba(36, 50, 63, 0.64), rgba(36, 50, 63, 0.64)),
    url('https://images.unsplash.com/photo-1552728089-571681646280?auto=format&fit=crop&w=1600&q=80') center/cover;
}

.auth-card {
  width: 420px;
  max-width: 100%;
  padding: 30px;
  background: #fff;
  border: 1px solid rgba(255, 255, 255, 0.72);
  border-radius: var(--pm-card-radius);
  box-shadow: var(--pm-shadow-strong);
}

.auth-head {
  margin-bottom: 22px;
  text-align: center;
}

.auth-head .el-icon {
  color: var(--pm-primary);
  font-size: 34px;
}

.auth-head h1 {
  margin: 10px 0 6px;
  font-size: 24px;
}

.auth-head p,
.auth-foot,
.auth-demo {
  color: var(--pm-text-light);
}

.auth-submit {
  width: 100%;
  margin-top: 4px;
}

.auth-foot {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 18px;
}

.auth-demo {
  margin-top: 14px;
  padding: 10px 12px;
  border-radius: var(--pm-card-radius);
  background: var(--pm-bg);
  font-size: 12px;
  line-height: 1.6;
  text-align: center;
}
</style>
