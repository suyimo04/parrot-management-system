<template>
  <div class="page-wrap portal-page">
    <h2 class="page-title">鹦鹉展示</h2>

    <div class="filter-card">
      <el-form :model="query" inline>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" clearable placeholder="编号或名称" />
        </el-form-item>
        <el-form-item label="品种">
          <el-select v-model="query.speciesId" clearable placeholder="全部品种" style="width: 160px">
            <el-option v-for="item in speciesOptions" :key="item.id" :label="item.name" :value="item.id" />
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
      <div v-else-if="list.length" class="parrot-grid">
        <article v-for="item in list" :key="item.id" class="parrot-card" @click="router.push(`/portal/parrot/${item.id}`)">
          <el-image class="parrot-card__img" :src="item.image || defaultParrotImage" fit="cover" />
          <div class="parrot-card__body">
            <div class="parrot-card__head">
              <h3>{{ item.name }}</h3>
              <StatusTag :text="item.healthStatus" />
            </div>
            <p>{{ item.speciesName || '暂无品种' }} · {{ item.gender || '未知性别' }} · {{ item.age ?? '-' }}岁</p>
            <p class="parrot-card__desc">{{ item.personality || item.description || '这只鹦鹉的资料还在整理中。' }}</p>
            <el-button text type="primary">查看详情</el-button>
          </div>
        </article>
      </div>
      <EmptyBox v-else description="暂无公开鹦鹉" />
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
import { getPublicParrots } from '../../api/public'

const router = useRouter()
const defaultParrotImage = 'https://images.unsplash.com/photo-1552728089-571681646280?auto=format&fit=crop&w=800&q=80'
const loading = ref(false)
const list = ref([])
const speciesOptions = ref([])

const query = reactive({
  keyword: '',
  speciesId: ''
})

async function loadList() {
  loading.value = true
  try {
    list.value = await getPublicParrots(cleanParams(query)) || []
  } finally {
    loading.value = false
  }
}

async function loadSpeciesOptions() {
  const data = await getPublicParrots()
  const map = new Map()
  ;(data || []).forEach(item => {
    if (item.speciesId && item.speciesName) {
      map.set(item.speciesId, { id: item.speciesId, name: item.speciesName })
    }
  })
  speciesOptions.value = Array.from(map.values())
}

function resetQuery() {
  Object.assign(query, { keyword: '', speciesId: '' })
  loadList()
}

function cleanParams(source) {
  return Object.fromEntries(Object.entries(source).filter(([, value]) => value !== '' && value !== null && value !== undefined))
}

onMounted(async () => {
  await loadSpeciesOptions()
  loadList()
})
</script>

<style scoped>
.portal-page {
  max-width: 1180px;
  margin: 0 auto;
}

.parrot-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.parrot-card {
  overflow: hidden;
  cursor: pointer;
  border: 1px solid var(--pm-border);
  border-radius: 8px;
  background: #fff;
  transition: transform 0.16s ease, box-shadow 0.16s ease;
}

.parrot-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--pm-shadow);
}

.parrot-card__img {
  width: 100%;
  height: 190px;
}

.parrot-card__body {
  padding: 14px;
}

.parrot-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.parrot-card h3 {
  margin: 0;
  font-size: 18px;
}

.parrot-card p {
  margin: 8px 0 0;
  color: var(--pm-text-light);
  line-height: 1.7;
}

.parrot-card__desc {
  display: -webkit-box;
  min-height: 48px;
  overflow: hidden;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

@media (max-width: 980px) {
  .parrot-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .parrot-grid {
    grid-template-columns: 1fr;
  }
}
</style>
