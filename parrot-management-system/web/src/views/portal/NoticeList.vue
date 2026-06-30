<template>
  <div class="page-wrap portal-page">
    <h2 class="page-title">公告知识</h2>

    <div class="filter-card">
      <el-form :model="query" inline>
        <el-form-item label="类型">
          <el-select v-model="query.type" clearable placeholder="全部" style="width: 160px">
            <el-option label="系统公告" value="系统公告" />
            <el-option label="饲养知识" value="饲养知识" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="loadList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <BaseCard>
      <div v-if="loading" class="app-empty">正在加载...</div>
      <div v-else-if="list.length" class="notice-grid">
        <article v-for="item in list" :key="item.id" class="notice-card" @click="router.push(`/portal/notice/${item.id}`)">
          <el-image class="notice-card__img" :src="item.coverImage || defaultNoticeImage" fit="cover" />
          <div class="notice-card__body">
            <div class="notice-card__meta">
              <StatusTag :text="item.type" />
              <span>{{ item.publishTime || item.createTime || '暂无时间' }}</span>
            </div>
            <h3>{{ item.title }}</h3>
            <p>{{ summary(item.content) }}</p>
          </div>
        </article>
      </div>
      <EmptyBox v-else description="暂无已发布公告" />
    </BaseCard>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import BaseCard from '../../components/BaseCard.vue'
import EmptyBox from '../../components/EmptyBox.vue'
import StatusTag from '../../components/StatusTag.vue'
import { getPublicNotices } from '../../api/public'

const router = useRouter()
const defaultNoticeImage = 'https://images.unsplash.com/photo-1522858547137-f1dcec554f55?auto=format&fit=crop&w=900&q=80'
const loading = ref(false)
const list = ref([])
const query = reactive({
  type: ''
})

async function loadList() {
  loading.value = true
  try {
    list.value = await getPublicNotices(cleanParams(query)) || []
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.type = ''
  loadList()
}

function summary(text) {
  if (!text) return '暂无内容摘要'
  return text.replace(/\s+/g, ' ').slice(0, 86)
}

function cleanParams(source) {
  return Object.fromEntries(Object.entries(source).filter(([, value]) => value !== '' && value !== null && value !== undefined))
}

onMounted(loadList)
</script>

<style scoped>
.portal-page {
  max-width: 1180px;
  margin: 0 auto;
}

.notice-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.notice-card {
  overflow: hidden;
  cursor: pointer;
  border: 1px solid var(--pm-border);
  border-radius: 8px;
  background: #fff;
}

.notice-card__img {
  width: 100%;
  height: 170px;
}

.notice-card__body {
  padding: 14px;
}

.notice-card__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  color: var(--pm-text-light);
  font-size: 13px;
}

.notice-card h3 {
  margin: 12px 0 8px;
  font-size: 18px;
}

.notice-card p {
  display: -webkit-box;
  min-height: 48px;
  margin: 0;
  overflow: hidden;
  color: var(--pm-text-light);
  line-height: 1.7;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

@media (max-width: 980px) {
  .notice-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .notice-grid {
    grid-template-columns: 1fr;
  }
}
</style>
