<template>
  <div class="page-wrap">
    <h2 class="page-title">品种管理</h2>
    <div class="filter-card">
      <el-form inline>
        <el-form-item label="品种名称"><el-input placeholder="请输入品种名称" /></el-form-item>
        <el-form-item label="状态"><el-select placeholder="全部" style="width: 140px" /></el-form-item>
        <el-form-item><el-button type="primary" :icon="Search">查询</el-button><el-button>重置</el-button></el-form-item>
      </el-form>
    </div>
    <PageTable :total="0">
      <template #toolbar>
        <el-button v-if="isAdmin(userStore.role)" type="primary" :icon="Plus">新增品种</el-button>
      </template>
      <el-table :data="[]"><el-table-column label="名称" /><el-table-column label="难度" /><el-table-column label="状态" /></el-table>
    </PageTable>
  </div>
</template>

<script setup>
import { Plus, Search } from '@element-plus/icons-vue'
import PageTable from '../../components/PageTable.vue'
import { useUserStore } from '../../store/user'
import { isAdmin } from '../../utils/auth'

const userStore = useUserStore()
</script>
