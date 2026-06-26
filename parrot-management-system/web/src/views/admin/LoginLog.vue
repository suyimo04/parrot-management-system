<template>
  <div class="page-wrap">
    <h2 class="page-title">登录日志</h2>
    <div class="filter-card">
      <el-form inline>
        <el-form-item label="用户名"><el-input placeholder="请输入用户名" /></el-form-item>
        <el-form-item label="结果"><el-select placeholder="全部" style="width: 140px" /></el-form-item>
        <el-form-item><el-button type="primary" :icon="Search">查询</el-button><el-button>重置</el-button></el-form-item>
      </el-form>
    </div>
    <PageTable :total="0">
      <el-table :data="[]"><el-table-column label="用户名" /><el-table-column label="角色" /><el-table-column label="登录结果" /></el-table>
    </PageTable>
  </div>
</template>

<script setup>
import { Search } from '@element-plus/icons-vue'
import PageTable from '../../components/PageTable.vue'
</script>
