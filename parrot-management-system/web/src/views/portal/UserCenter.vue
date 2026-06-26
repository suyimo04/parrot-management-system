<template>
  <div class="page-wrap portal-page">
    <BaseCard title="个人中心">
      <el-form :model="form" label-width="90px" class="profile-form">
        <el-form-item label="用户名">
          <el-input :model-value="userStore.username" disabled />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="角色">
          <el-input :model-value="'客户'" disabled />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save">保存资料</el-button>
        </el-form-item>
      </el-form>
    </BaseCard>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import BaseCard from '../../components/BaseCard.vue'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const form = reactive({
  realName: userStore.realName,
  phone: userStore.phone,
  email: userStore.email
})

function save() {
  userStore.updateProfile(form)
  ElMessage.success('资料已保存到本地，后续接入口后会同步到数据库')
}
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
