<template>
  <div class="page-wrap">
    <h2 class="page-title">首页看板</h2>
    <div class="dashboard-grid">
      <BaseCard v-for="item in cards" :key="item.title">
        <div class="stat-card">
          <el-icon :class="item.type"><component :is="item.icon" /></el-icon>
          <div>
            <p>{{ item.title }}</p>
            <strong>{{ item.value }}</strong>
          </div>
        </div>
      </BaseCard>
    </div>
    <BaseCard title="数据图表" class="chart-card">
      <div class="chart-placeholder">后续接入 ECharts 展示品种分布、预约趋势和健康状态</div>
    </BaseCard>
  </div>
</template>

<script setup>
import BaseCard from '../../components/BaseCard.vue'

const cards = [
  { title: '鹦鹉总数', value: '--', icon: 'Star', type: 'primary' },
  { title: '客户数量', value: '--', icon: 'User', type: 'success' },
  { title: '待处理预约', value: '--', icon: 'Calendar', type: 'warning' },
  { title: '健康异常数', value: '--', icon: 'Warning', type: 'danger' }
]
</script>

<style scoped>
.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 14px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 14px;
}

.stat-card .el-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  font-size: 22px;
}

.stat-card .primary {
  color: var(--pm-primary);
  background: rgba(22, 160, 133, 0.1);
}

.stat-card .success {
  color: var(--pm-success);
  background: rgba(39, 174, 96, 0.1);
}

.stat-card .warning {
  color: var(--pm-warning);
  background: rgba(243, 156, 18, 0.12);
}

.stat-card .danger {
  color: var(--pm-danger);
  background: rgba(231, 76, 60, 0.1);
}

.stat-card p {
  margin: 0 0 4px;
  color: var(--pm-text-light);
}

.stat-card strong {
  font-size: 24px;
}

.chart-card {
  min-height: 320px;
}

.chart-placeholder {
  height: 250px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--pm-text-light);
  background: var(--pm-bg);
  border-radius: 8px;
}

@media (max-width: 1000px) {
  .dashboard-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 560px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}
</style>
