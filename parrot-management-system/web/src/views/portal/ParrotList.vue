<template>
  <div class="page-wrap portal-page">
    <h2 class="page-title">鹦鹉展示</h2>
    <div class="filter-card">
      <el-form inline>
        <el-form-item label="关键词"><el-input placeholder="名称或品种" /></el-form-item>
        <el-form-item><el-button type="primary" :icon="Search">查询</el-button><el-button>重置</el-button></el-form-item>
      </el-form>
    </div>
    <BaseCard>
      <EmptyBox description="暂无公开鹦鹉，后续接入接口后显示图片和详情入口" />
    </BaseCard>
  </div>
</template>

<script setup>
import { Search } from '@element-plus/icons-vue'
import BaseCard from '../../components/BaseCard.vue'
import EmptyBox from '../../components/EmptyBox.vue'
</script>

<style scoped>
.portal-page {
  max-width: 1180px;
  margin: 0 auto;
}
</style>
