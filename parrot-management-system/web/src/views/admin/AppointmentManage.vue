<template>
  <div class="page-wrap">
    <h2 class="page-title">预约咨询管理</h2>
    <div class="filter-card">
      <el-form inline>
        <el-form-item label="状态"><el-select placeholder="全部" style="width: 150px" /></el-form-item>
        <el-form-item label="客户"><el-input placeholder="客户关键词" /></el-form-item>
        <el-form-item><el-button type="primary" :icon="Search">查询</el-button><el-button>重置</el-button></el-form-item>
      </el-form>
    </div>
    <PageTable :total="0">
      <el-table :data="[]"><el-table-column label="客户" /><el-table-column label="鹦鹉" /><el-table-column label="状态" /></el-table>
    </PageTable>
  </div>
</template>

<script setup>
import { Search } from '@element-plus/icons-vue'
import PageTable from '../../components/PageTable.vue'
</script>
