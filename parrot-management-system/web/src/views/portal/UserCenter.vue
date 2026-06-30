<template>
  <div class="page-wrap portal-page">
    <BaseCard title="个人中心">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" class="profile-form">
        <el-form-item label="用户名">
          <el-input :model-value="userStore.username" disabled />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" maxlength="11" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="角色">
          <el-input :model-value="'客户'" disabled />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="save">保存资料</el-button>
        </el-form-item>
      </el-form>
    </BaseCard>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import BaseCard from '../../components/BaseCard.vue'
import { getProfile, updateProfile } from '../../api/portal'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const saving = ref(false)
const formRef = ref()
const form = reactive({
  realName: '',
  phone: '',
  email: ''
})

const rules = {
  phone: [{ pattern: /^$|^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }]
}

async function loadProfile() {
  const data = await getProfile()
  Object.assign(form, {
    realName: data.realName || '',
    phone: data.phone || '',
    email: data.email || ''
  })
  userStore.updateProfile(form)
}

async function save() {
  await formRef.value.validate()
  saving.value = true
  try {
    const data = await updateProfile(form)
    userStore.updateProfile(data)
    ElMessage.success('个人资料已保存')
  } finally {
    saving.value = false
  }
}

onMounted(loadProfile)
</script>

<style scoped>
.portal-page {
  max-width: 760px;
  margin: 0 auto;
}

.profile-form {
  max-width: 520px;
}
</style>
