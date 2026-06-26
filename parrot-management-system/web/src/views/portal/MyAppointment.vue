<template>
  <div class="page-wrap portal-page">
    <h2 class="page-title">我的预约</h2>
    <div class="filter-card">
      <el-form inline>
        <el-form-item label="状态"><el-select placeholder="全部" style="width: 160px" /></el-form-item>
        <el-form-item><el-button type="primary" :icon="Search">查询</el-button><el-button>新增预约</el-button></el-form-item>
      </el-form>
    </div>
    <BaseCard>
      <EmptyBox description="暂无预约记录，后续接入接口后显示预约状态和处理结果" />
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
  max-width: 1000px;
  margin: 0 auto;
}
</style>
