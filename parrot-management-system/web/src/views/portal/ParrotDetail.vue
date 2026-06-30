<template>
  <div class="page-wrap portal-page">
    <div v-if="loading" class="app-empty">正在加载...</div>
    <template v-else-if="detail.id">
      <section class="detail-head">
        <el-image class="detail-img" :src="detail.image || defaultParrotImage" fit="cover" />
        <BaseCard class="detail-main">
          <div class="detail-title">
            <div>
              <h2>{{ detail.name }}</h2>
              <p>{{ detail.code }} · {{ detail.speciesName || '暂无品种' }}</p>
            </div>
            <StatusTag :text="detail.healthStatus" />
          </div>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="性别">{{ detail.gender || '-' }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ detail.age ?? '-' }}</el-descriptions-item>
            <el-descriptions-item label="颜色">{{ detail.color || '-' }}</el-descriptions-item>
            <el-descriptions-item label="来源">{{ detail.source || '-' }}</el-descriptions-item>
            <el-descriptions-item label="入园日期">{{ detail.entryDate || '-' }}</el-descriptions-item>
            <el-descriptions-item label="当前状态">{{ detail.currentStatus || '-' }}</el-descriptions-item>
          </el-descriptions>
          <div class="detail-actions">
            <el-button type="primary" :icon="Calendar" @click="goAppointment">预约咨询</el-button>
            <el-button @click="router.push('/portal/parrot')">返回列表</el-button>
          </div>
        </BaseCard>
      </section>

      <section class="detail-section">
        <BaseCard title="性格与养护说明">
          <p class="text-block">{{ detail.personality || '暂未填写性格说明。' }}</p>
          <p class="text-block">{{ detail.careNotes || detail.description || '暂未填写养护说明。' }}</p>
        </BaseCard>
        <BaseCard title="品种信息">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="英文名">{{ detail.speciesEnglishName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="产地">{{ detail.speciesOrigin || '-' }}</el-descriptions-item>
            <el-descriptions-item label="体型">{{ detail.speciesSize || '-' }}</el-descriptions-item>
            <el-descriptions-item label="平均寿命">{{ detail.speciesAvgLifespan || '-' }}</el-descriptions-item>
            <el-descriptions-item label="饲养难度">{{ detail.speciesDifficulty || '-' }}</el-descriptions-item>
          </el-descriptions>
          <p class="text-block">{{ detail.speciesHabits || '暂未填写品种习性。' }}</p>
        </BaseCard>
      </section>
    </template>
    <BaseCard v-else>
      <EmptyBox description="没有找到该鹦鹉信息" />
    </BaseCard>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Calendar } from '@element-plus/icons-vue'
import BaseCard from '../../components/BaseCard.vue'
import EmptyBox from '../../components/EmptyBox.vue'
import StatusTag from '../../components/StatusTag.vue'
import { getPublicParrot } from '../../api/public'
import { useUserStore } from '../../store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const defaultParrotImage = 'https://images.unsplash.com/photo-1552728089-571681646280?auto=format&fit=crop&w=900&q=80'
const loading = ref(false)
const detail = ref({})

async function loadDetail() {
  loading.value = true
  try {
    detail.value = await getPublicParrot(route.params.id)
  } finally {
    loading.value = false
  }
}

function goAppointment() {
  if (!userStore.isLogin) {
    router.push({ path: '/login', query: { redirect: `/portal/appointment?parrotId=${detail.value.id}` } })
    return
  }
  router.push({ path: '/portal/appointment', query: { parrotId: detail.value.id } })
}

onMounted(loadDetail)
</script>

<style scoped>
.portal-page {
  max-width: 1100px;
  margin: 0 auto;
}

.detail-head {
  display: grid;
  grid-template-columns: 420px minmax(0, 1fr);
  gap: 16px;
}

.detail-img {
  width: 100%;
  height: 360px;
  border-radius: 10px;
  border: 1px solid var(--pm-border);
}

.detail-main {
  min-width: 0;
}

.detail-title {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.detail-title h2 {
  margin: 0 0 8px;
  font-size: 26px;
}

.detail-title p {
  margin: 0;
  color: var(--pm-text-light);
}

.detail-actions {
  display: flex;
  gap: 10px;
  margin-top: 16px;
}

.detail-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-top: 16px;
}

.text-block {
  margin: 0 0 12px;
  color: var(--pm-text-light);
  line-height: 1.9;
  white-space: pre-wrap;
}

@media (max-width: 900px) {
  .detail-head,
  .detail-section {
    grid-template-columns: 1fr;
  }

  .detail-img {
    height: 280px;
  }
}
</style>
