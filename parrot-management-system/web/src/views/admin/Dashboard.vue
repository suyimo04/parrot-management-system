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

    <div class="chart-grid">
      <BaseCard title="品种分布">
        <div ref="speciesChartRef" class="chart-box"></div>
        <EmptyBox v-if="!charts.speciesDistribution.length" text="暂无品种统计" />
      </BaseCard>
      <BaseCard title="近七日预约趋势">
        <div ref="trendChartRef" class="chart-box"></div>
        <EmptyBox v-if="!charts.appointmentTrend.length" text="暂无预约数据" />
      </BaseCard>
      <BaseCard title="健康状态占比" class="wide-card">
        <div ref="healthChartRef" class="chart-box"></div>
        <EmptyBox v-if="!charts.healthStatusDistribution.length" text="暂无健康状态数据" />
      </BaseCard>
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import * as echarts from 'echarts'
import BaseCard from '../../components/BaseCard.vue'
import EmptyBox from '../../components/EmptyBox.vue'
import { getDashboardCharts, getDashboardStats } from '../../api/dashboard'

const stats = reactive({
  parrotCount: 0,
  customerCount: 0,
  pendingAppointmentCount: 0,
  abnormalHealthCount: 0,
  todayFeedingCount: 0
})

const charts = reactive({
  speciesDistribution: [],
  appointmentTrend: [],
  healthStatusDistribution: []
})

const speciesChartRef = ref()
const trendChartRef = ref()
const healthChartRef = ref()
let speciesChart
let trendChart
let healthChart

const cards = computed(() => [
  { title: '鹦鹉总数', value: stats.parrotCount, icon: 'Star', type: 'primary' },
  { title: '客户数量', value: stats.customerCount, icon: 'User', type: 'success' },
  { title: '待处理预约', value: stats.pendingAppointmentCount, icon: 'Calendar', type: 'warning' },
  { title: '健康异常数', value: stats.abnormalHealthCount, icon: 'Warning', type: 'danger' },
  { title: '今日喂养记录', value: stats.todayFeedingCount, icon: 'Food', type: 'primary' }
])

async function loadData() {
  const [statData, chartData] = await Promise.all([getDashboardStats(), getDashboardCharts()])
  Object.assign(stats, statData || {})
  Object.assign(charts, {
    speciesDistribution: chartData?.speciesDistribution || [],
    appointmentTrend: chartData?.appointmentTrend || [],
    healthStatusDistribution: chartData?.healthStatusDistribution || []
  })
  await nextTick()
  renderCharts()
}

function renderCharts() {
  renderSpecies()
  renderTrend()
  renderHealth()
}

function renderSpecies() {
  speciesChart ||= echarts.init(speciesChartRef.value)
  speciesChart.setOption({
    color: ['#16A085'],
    grid: { left: 36, right: 16, top: 28, bottom: 36 },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: charts.speciesDistribution.map(item => item.name), axisTick: { show: false } },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{ type: 'bar', barWidth: 28, data: charts.speciesDistribution.map(item => item.value) }]
  })
}

function renderTrend() {
  trendChart ||= echarts.init(trendChartRef.value)
  trendChart.setOption({
    color: ['#2ECC71'],
    grid: { left: 36, right: 16, top: 28, bottom: 36 },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: charts.appointmentTrend.map(item => item.name.slice(5)), boundaryGap: false },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{ type: 'line', smooth: true, data: charts.appointmentTrend.map(item => item.value), areaStyle: { opacity: 0.12 } }]
  })
}

function renderHealth() {
  healthChart ||= echarts.init(healthChartRef.value)
  healthChart.setOption({
    color: ['#27AE60', '#F39C12', '#E74C3C', '#909399', '#F4B400'],
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['42%', '66%'],
      center: ['50%', '45%'],
      data: charts.healthStatusDistribution
    }]
  })
}

function resizeCharts() {
  speciesChart?.resize()
  trendChart?.resize()
  healthChart?.resize()
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  speciesChart?.dispose()
  trendChart?.dispose()
  healthChart?.dispose()
})
</script>

<style scoped>
.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 14px;
}

.stat-card {
  min-height: 70px;
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

.chart-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.wide-card {
  grid-column: span 2;
}

.chart-box {
  width: 100%;
  height: 300px;
}

.wide-card .chart-box {
  height: 340px;
}

@media (max-width: 1180px) {
  .dashboard-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .dashboard-grid,
  .chart-grid {
    grid-template-columns: 1fr;
  }

  .wide-card {
    grid-column: auto;
  }
}
</style>
