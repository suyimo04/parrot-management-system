<template>
  <div class="page-wrap">
    <h2 class="page-title">登录日志</h2>

    <div class="filter-card">
      <el-form :model="query" inline>
        <el-form-item label="用户名">
          <el-input v-model="query.username" clearable placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="结果">
          <el-select v-model="query.result" clearable placeholder="全部" style="width: 120px">
            <el-option label="成功" value="成功" />
            <el-option label="失败" value="失败" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="search">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <PageTable
      :current="query.page"
      :size="query.size"
      :total="total"
      @page-change="changePage"
      @size-change="changeSize"
    >
      <el-table v-loading="loading" :data="list" row-key="id">
        <el-table-column prop="username" label="用户名" min-width="130" />
        <el-table-column label="角色" min-width="100">
          <template #default="{ row }">{{ roleText(row.role) }}</template>
        </el-table-column>
        <el-table-column prop="ip" label="IP" min-width="140" />
        <el-table-column prop="loginTime" label="登录时间" min-width="170" />
        <el-table-column label="结果" min-width="90">
          <template #default="{ row }"><StatusTag :text="row.result" /></template>
        </el-table-column>
        <el-table-column prop="failReason" label="失败原因" min-width="220" show-overflow-tooltip />
      </el-table>
    </PageTable>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { Search } from '@element-plus/icons-vue'
import PageTable from '../../components/PageTable.vue'
import StatusTag from '../../components/StatusTag.vue'
import { getLoginLogPage } from '../../api/loginLog'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const dateRange = ref([])

const query = reactive({
  page: 1,
  size: 10,
  username: '',
  result: ''
})

async function loadList() {
  loading.value = true
  try {
    const params = cleanParams({
      ...query,
      startDate: dateRange.value?.[0],
      endDate: dateRange.value?.[1]
    })
    const data = await getLoginLogPage(params)
    list.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function cleanParams(source) {
  return Object.fromEntries(Object.entries(source).filter(([, value]) => value !== '' && value !== null && value !== undefined))
}

function search() {
  query.page = 1
  loadList()
}

function resetQuery() {
  Object.assign(query, { page: 1, size: 10, username: '', result: '' })
  dateRange.value = []
  loadList()
}

function changePage(page) {
  query.page = page
  loadList()
}

function changeSize(size) {
  query.size = size
  query.page = 1
  loadList()
}

function roleText(role) {
  if (role === 'ADMIN') return '管理员'
  if (role === 'KEEPER') return '饲养员'
  if (role === 'CUSTOMER') return '客户'
  return role || '未知'
}

onMounted(loadList)
</script>
