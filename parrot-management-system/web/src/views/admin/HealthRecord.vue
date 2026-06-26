<template>
  <div class="page-wrap">
    <h2 class="page-title">健康记录管理</h2>
    <div class="filter-card">
      <el-form inline>
        <el-form-item label="鹦鹉"><el-input placeholder="鹦鹉名称" /></el-form-item>
        <el-form-item label="日期"><el-date-picker type="daterange" start-placeholder="开始日期" end-placeholder="结束日期" /></el-form-item>
        <el-form-item><el-button type="primary" :icon="Search">查询</el-button><el-button>重置</el-button></el-form-item>
      </el-form>
    </div>
    <PageTable :total="0">
      <template #toolbar><el-button type="primary" :icon="Plus">新增健康记录</el-button></template>
      <el-table :data="[]"><el-table-column label="鹦鹉" /><el-table-column label="记录日期" /><el-table-column label="体重" /></el-table>
    </PageTable>
  </div>
</template>

<script setup>
import { Plus, Search } from '@element-plus/icons-vue'
import PageTable from '../../components/PageTable.vue'
</script>
