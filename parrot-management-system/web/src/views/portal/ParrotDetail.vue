<template>
  <div class="page-wrap portal-page">
    <BaseCard title="鹦鹉详情">
      <div class="detail-empty">
        <EmptyBox description="后续接入详情接口后展示鹦鹉大图、性格和品种信息" />
        <el-button type="primary" :icon="Calendar" @click="goAppointment">预约咨询</el-button>
      </div>
    </BaseCard>
  </div>
</template>

<script setup>
import { Calendar } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import BaseCard from '../../components/BaseCard.vue'
import EmptyBox from '../../components/EmptyBox.vue'
import { useUserStore } from '../../store/user'

const router = useRouter()
const userStore = useUserStore()

function goAppointment() {
  if (!userStore.isLogin) {
    router.push('/login')
    return
  }
  router.push('/portal/appointment')
}
</script>

<style scoped>
.portal-page {
  max-width: 980px;
  margin: 0 auto;
}

.detail-empty {
  text-align: center;
}
</style>
