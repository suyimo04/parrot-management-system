<template>
  <div class="page-wrap">
    <h2 class="page-title">预约咨询管理</h2>

    <div class="filter-card">
      <el-form :model="query" inline>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部" style="width: 140px">
            <el-option v-for="item in statusOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="客户">
          <el-input v-model="query.customerKeyword" clearable placeholder="用户名或姓名" />
        </el-form-item>
        <el-form-item label="鹦鹉">
          <el-select v-model="query.parrotId" clearable filterable placeholder="全部鹦鹉" style="width: 160px">
            <el-option v-for="item in parrotList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="预约日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="search">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <PageTable
      :current="query.page"
      :size="query.size"
      :total="total"
      @page-change="changePage"
      @size-change="changeSize"
    >
      <el-table v-loading="loading" :data="list" row-key="id">
        <el-table-column prop="customerName" label="客户" min-width="110" />
        <el-table-column prop="parrotName" label="鹦鹉" min-width="120" />
        <el-table-column prop="appointmentDate" label="预约日期" min-width="110" />
        <el-table-column prop="timeSlot" label="时间段" min-width="120" />
        <el-table-column prop="phone" label="电话" min-width="130" />
        <el-table-column prop="email" label="邮箱" min-width="170" show-overflow-tooltip />
        <el-table-column prop="consultType" label="咨询类型" min-width="110" />
        <el-table-column label="状态" min-width="100">
          <template #default="{ row }"><StatusTag :text="row.status" /></template>
        </el-table-column>
        <el-table-column prop="handlerName" label="处理人" min-width="110" />
        <el-table-column prop="handleTime" label="处理时间" min-width="170" />
        <el-table-column label="操作" width="260" fixed="right" align="center">
          <template #default="{ row }">
            <div class="table-actions table-actions--wide">
              <el-tooltip content="详情" placement="top">
                <el-button circle size="small" :icon="View" @click="openDetail(row)" />
              </el-tooltip>
              <el-tooltip v-if="row.status === '待处理' && canDo('appointment:confirm')" content="确认" placement="top">
                <el-button circle size="small" type="success" :icon="Check" @click="confirmRow(row)" />
              </el-tooltip>
              <el-tooltip v-if="row.status === '待处理' && canDo('appointment:reject')" content="驳回" placement="top">
                <el-button circle size="small" type="danger" :icon="Close" @click="openHandle(row, 'reject')" />
              </el-tooltip>
              <el-tooltip v-if="row.status === '已确认' && canDo('appointment:finish')" content="完成" placement="top">
                <el-button circle size="small" type="primary" :icon="Select" @click="openHandle(row, 'finish')" />
              </el-tooltip>
              <el-tooltip v-if="canCancel(row.status) && canDo('appointment:cancel')" content="取消" placement="top">
                <el-button circle size="small" type="warning" :icon="Remove" @click="openHandle(row, 'cancel')" />
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </PageTable>

    <el-dialog v-model="detailVisible" title="预约详情" width="680px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="客户">{{ currentRow.customerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="鹦鹉">{{ currentRow.parrotName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="预约日期">{{ currentRow.appointmentDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="时间段">{{ currentRow.timeSlot || '-' }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ currentRow.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentRow.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="咨询类型">{{ currentRow.consultType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态"><StatusTag :text="currentRow.status" /></el-descriptions-item>
        <el-descriptions-item label="咨询内容" :span="2">{{ currentRow.content || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理结果" :span="2">{{ currentRow.result || '-' }}</el-descriptions-item>
        <el-descriptions-item label="后台备注" :span="2">{{ currentRow.backendRemark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="handleVisible" :title="handleTitle" width="560px" @closed="resetHandleForm">
      <el-form ref="handleFormRef" :model="handleForm" :rules="handleRules" label-width="92px">
        <el-form-item :label="handleLabel" prop="result">
          <el-input v-model="handleForm.result" type="textarea" :rows="4" :placeholder="handlePlaceholder" />
        </el-form-item>
        <el-form-item label="后台备注">
          <el-input v-model="handleForm.backendRemark" type="textarea" :rows="3" placeholder="内部备注，可不填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitHandle">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check, Close, Remove, Search, Select, View } from '@element-plus/icons-vue'
import PageTable from '../../components/PageTable.vue'
import StatusTag from '../../components/StatusTag.vue'
import {
  cancelAppointmentByAdmin,
  confirmAppointment,
  finishAppointment,
  getAppointmentPage,
  rejectAppointment
} from '../../api/appointment'
import { getParrotPage } from '../../api/parrot'
import { useUserStore } from '../../store/user'
import { hasAction } from '../../utils/auth'

const userStore = useUserStore()
const statusOptions = ['待处理', '已确认', '已完成', '已取消', '已驳回']
const loading = ref(false)
const saving = ref(false)
const detailVisible = ref(false)
const handleVisible = ref(false)
const handleFormRef = ref()
const list = ref([])
const parrotList = ref([])
const total = ref(0)
const dateRange = ref([])
const currentRow = ref({})
const handleType = ref('')

const query = reactive({
  page: 1,
  size: 10,
  status: '',
  customerKeyword: '',
  parrotId: ''
})

const handleForm = reactive({
  result: '',
  backendRemark: ''
})

const handleRules = {
  result: [{ required: true, message: '请填写处理说明', trigger: 'blur' }]
}

const handleTitle = computed(() => {
  if (handleType.value === 'finish') return '完成预约'
  if (handleType.value === 'reject') return '驳回预约'
  return '取消预约'
})

const handleLabel = computed(() => {
  if (handleType.value === 'finish') return '处理结果'
  if (handleType.value === 'reject') return '驳回原因'
  return '取消原因'
})

const handlePlaceholder = computed(() => {
  if (handleType.value === 'finish') return '填写本次咨询结果，客户可在我的预约中查看'
  if (handleType.value === 'reject') return '请写明驳回原因'
  return '请写明取消原因'
})

async function loadParrots() {
  const data = await getParrotPage({ page: 1, size: 100 })
  parrotList.value = data.records || []
}

async function loadList() {
  loading.value = true
  try {
    const data = await getAppointmentPage(buildParams())
    list.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function buildParams() {
  const params = { ...query }
  if (dateRange.value?.length === 2) {
    params.startDate = dateRange.value[0]
    params.endDate = dateRange.value[1]
  }
  return cleanParams(params)
}

function cleanParams(source) {
  return Object.fromEntries(Object.entries(source).filter(([, value]) => value !== '' && value !== null && value !== undefined))
}

function search() {
  query.page = 1
  loadList()
}

function resetQuery() {
  Object.assign(query, { page: 1, size: 10, status: '', customerKeyword: '', parrotId: '' })
  dateRange.value = []
  loadList()
}

function changePage(page) {
  query.page = page
  loadList()
}

function changeSize(size) {
  query.size = size
  query.page = 1
  loadList()
}

function openDetail(row) {
  currentRow.value = row
  detailVisible.value = true
}

async function confirmRow(row) {
  await ElMessageBox.confirm(`确定确认“${row.customerName || '客户'}”的预约吗？`, '操作确认', { type: 'warning' })
  await confirmAppointment(row.id)
  ElMessage.success('预约已确认')
  loadList()
}

function openHandle(row, type) {
  currentRow.value = row
  handleType.value = type
  handleVisible.value = true
}

async function submitHandle() {
  await handleFormRef.value.validate()
  await ElMessageBox.confirm(`确定${handleTitle.value}吗？`, '操作确认', { type: 'warning' })
  saving.value = true
  try {
    const data = cleanParams({ ...handleForm })
    if (handleType.value === 'finish') {
      await finishAppointment(currentRow.value.id, data)
    } else if (handleType.value === 'reject') {
      await rejectAppointment(currentRow.value.id, data)
    } else {
      await cancelAppointmentByAdmin(currentRow.value.id, data)
    }
    ElMessage.success('处理成功')
    handleVisible.value = false
    loadList()
  } finally {
    saving.value = false
  }
}

function canCancel(status) {
  return status === '待处理' || status === '已确认'
}

function canDo(code) {
  return hasAction(userStore, code)
}

function resetHandleForm() {
  handleFormRef.value?.clearValidate()
  Object.assign(handleForm, { result: '', backendRemark: '' })
  handleType.value = ''
}

onMounted(async () => {
  await loadParrots()
  loadList()
})
</script>
