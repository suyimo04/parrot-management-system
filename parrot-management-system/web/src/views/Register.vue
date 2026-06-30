<template>
  <div class="auth-page">
    <section class="auth-card register-card">
      <div class="auth-head">
        <el-icon><Star /></el-icon>
        <h1>注册客户账号</h1>
        <p>注册后可提交预约并查看处理进度</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model.trim="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password placeholder="请再次输入密码" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model.trim="form.realName" placeholder="可填写真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model.trim="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model.trim="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-button class="auth-submit" type="primary" :loading="loading" @click="submit">注册</el-button>
      </el-form>

      <div class="auth-foot">
        <span>已有账号？</span>
        <router-link to="/login">返回登录</router-link>
      </div>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star } from '@element-plus/icons-vue'
import { register } from '../api/auth'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: '',
  email: ''
})

const validateConfirm = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
  password: [
    { required: true, message: '密码不能为空', trigger: 'blur' },
    { min: 6, max: 30, message: '密码长度应为6到30位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ],
  phone: [{ pattern: /^$|^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }]
}

async function submit() {
  await formRef.value.validate()
  loading.value = true
  try {
    await register({
      username: form.username,
      password: form.password,
      realName: form.realName,
      phone: form.phone,
      email: form.email
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
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
    linear-gradient(rgba(36, 50, 63, 0.62), rgba(36, 50, 63, 0.62)),
    url('https://images.unsplash.com/photo-1522858547137-f1dcec554f55?auto=format&fit=crop&w=1600&q=80') center/cover;
}

.auth-card {
  width: 430px;
  max-width: 100%;
  padding: 28px;
  background: #fff;
  border: 1px solid rgba(255, 255, 255, 0.72);
  border-radius: var(--pm-card-radius);
  box-shadow: var(--pm-shadow-strong);
}

.register-card {
  margin: 20px 0;
}

.auth-head {
  margin-bottom: 18px;
  text-align: center;
}

.auth-head .el-icon {
  color: var(--pm-primary);
  font-size: 34px;
}

.auth-head h1 {
  margin: 10px 0 6px;
  font-size: 23px;
}

.auth-head p,
.auth-foot {
  color: var(--pm-text-light);
}

.auth-submit {
  width: 100%;
}

.auth-foot {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 16px;
}
</style>
