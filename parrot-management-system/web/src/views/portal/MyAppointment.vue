<template>
  <div class="page-wrap portal-page">
    <h2 class="page-title">我的预约</h2>

    <div class="filter-card">
      <el-form :model="query" inline>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部" style="width: 160px">
            <el-option v-for="item in statusOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="loadList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button :icon="Plus" @click="openAdd">新增预约</el-button>
        </el-form-item>
      </el-form>
    </div>

    <BaseCard>
      <div v-if="loading" class="app-empty">正在加载...</div>
      <el-table v-else-if="list.length" :data="list" row-key="id">
        <el-table-column prop="parrotName" label="鹦鹉" min-width="120" />
        <el-table-column prop="appointmentDate" label="预约日期" min-width="110" />
        <el-table-column prop="timeSlot" label="时间段" min-width="120" />
        <el-table-column prop="consultType" label="咨询类型" min-width="110" />
        <el-table-column label="状态" min-width="100">
          <template #default="{ row }"><StatusTag :text="row.status" /></template>
        </el-table-column>
        <el-table-column prop="result" label="处理结果" min-width="180" show-overflow-tooltip />
        <el-table-column prop="createTime" label="提交时间" min-width="170" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === '待处理' || row.status === '已确认'"
              size="small"
              type="warning"
              @click="cancel(row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <EmptyBox v-else description="暂无预约记录" />
    </BaseCard>

    <el-dialog v-model="dialogVisible" title="新增预约" width="680px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="92px" class="form-grid">
        <el-form-item label="预约鹦鹉" prop="parrotId">
          <el-select v-model="form.parrotId" filterable style="width: 100%">
            <el-option v-for="item in parrotList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="预约日期" prop="appointmentDate">
          <el-date-picker v-model="form.appointmentDate" value-format="YYYY-MM-DD" type="date" style="width: 100%" />
        </el-form-item>
        <el-form-item label="时间段" prop="timeSlot">
          <el-select v-model="form.timeSlot" style="width: 100%">
            <el-option label="09:00-10:00" value="09:00-10:00" />
            <el-option label="10:00-11:00" value="10:00-11:00" />
            <el-option label="14:00-15:00" value="14:00-15:00" />
            <el-option label="15:00-16:00" value="15:00-16:00" />
          </el-select>
        </el-form-item>
        <el-form-item label="咨询类型">
          <el-select v-model="form.consultType" style="width: 100%">
            <el-option label="领养咨询" value="领养咨询" />
            <el-option label="饲养咨询" value="饲养咨询" />
            <el-option label="健康咨询" value="健康咨询" />
            <el-option label="参观预约" value="参观预约" />
          </el-select>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" maxlength="11" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="咨询内容" class="full-line">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="可以简单说明想了解的问题" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">提交预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import BaseCard from '../../components/BaseCard.vue'
import EmptyBox from '../../components/EmptyBox.vue'
import StatusTag from '../../components/StatusTag.vue'
import { getPublicParrots } from '../../api/public'
import { cancelMyAppointment, getMyAppointments, submitAppointment } from '../../api/portal'
import { useUserStore } from '../../store/user'

const route = useRoute()
const userStore = useUserStore()
const statusOptions = ['待处理', '已确认', '已完成', '已取消', '已驳回']
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const list = ref([])
const parrotList = ref([])

const query = reactive({
  status: ''
})

const form = reactive(defaultForm())
const rules = {
  parrotId: [{ required: true, message: '请选择预约鹦鹉', trigger: 'change' }],
  appointmentDate: [{ required: true, message: '请选择预约日期', trigger: 'change' }],
  timeSlot: [{ required: true, message: '请选择时间段', trigger: 'change' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }]
}

function defaultForm() {
  return {
    parrotId: '',
    appointmentDate: '',
    timeSlot: '',
    phone: userStore.phone || '',
    email: userStore.email || '',
    consultType: '领养咨询',
    content: ''
  }
}

async function loadParrots() {
  parrotList.value = await getPublicParrots() || []
}

async function loadList() {
  loading.value = true
  try {
    list.value = await getMyAppointments(cleanParams(query)) || []
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.status = ''
  loadList()
}

function openAdd() {
  Object.assign(form, defaultForm())
  if (route.query.parrotId) {
    form.parrotId = Number(route.query.parrotId)
  }
  dialogVisible.value = true
}

async function submit() {
  await formRef.value.validate()
  saving.value = true
  try {
    await submitAppointment(cleanParams({ ...form }))
    ElMessage.success('预约提交成功')
    dialogVisible.value = false
    loadList()
  } finally {
    saving.value = false
  }
}

async function cancel(row) {
  await ElMessageBox.confirm(`确定取消“${row.parrotName}”在 ${row.appointmentDate} 的预约吗？`, '操作确认', { type: 'warning' })
  await cancelMyAppointment(row.id)
  ElMessage.success('预约已取消')
  loadList()
}

function resetForm() {
  formRef.value?.clearValidate()
  Object.assign(form, defaultForm())
}

function cleanParams(source) {
  return Object.fromEntries(Object.entries(source).filter(([, value]) => value !== '' && value !== null && value !== undefined))
}

onMounted(async () => {
  await loadParrots()
  await loadList()
  if (route.query.parrotId) {
    openAdd()
  }
})
</script>

<style scoped>
.portal-page {
  max-width: 1000px;
  margin: 0 auto;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 12px;
}

.full-line {
  grid-column: span 2;
}

@media (max-width: 720px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .full-line {
    grid-column: auto;
  }
}
</style>
