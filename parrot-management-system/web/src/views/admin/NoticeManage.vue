<template>
  <div class="page-wrap">
    <h2 class="page-title">公告知识管理</h2>
    <div class="filter-card">
      <el-form inline>
        <el-form-item label="标题"><el-input placeholder="公告标题" /></el-form-item>
        <el-form-item label="类型"><el-select placeholder="全部" style="width: 150px" /></el-form-item>
        <el-form-item><el-button type="primary" :icon="Search">查询</el-button><el-button>重置</el-button></el-form-item>
      </el-form>
    </div>
    <PageTable :total="0">
      <template #toolbar><el-button type="primary" :icon="Plus">新增公告</el-button></template>
      <el-table :data="[]"><el-table-column label="标题" /><el-table-column label="类型" /><el-table-column label="发布状态" /></el-table>
    </PageTable>
  </div>
</template>

<script setup>
import { Plus, Search } from '@element-plus/icons-vue'
import PageTable from '../../components/PageTable.vue'
</script>
