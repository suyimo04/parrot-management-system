<template>
  <div class="page-wrap">
    <h2 class="page-title">喂养记录管理</h2>

    <div class="filter-card">
      <el-form :model="query" inline>
        <el-form-item label="鹦鹉">
          <el-select v-model="query.parrotId" clearable filterable placeholder="全部鹦鹉" style="width: 160px">
            <el-option v-for="item in parrotList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </el-form-item>
        <el-form-item label="记录人ID">
          <el-input v-model="query.recorderId" clearable placeholder="可按ID筛选" style="width: 120px" />
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
      <template #toolbar>
        <el-button type="primary" :icon="Plus" @click="openAdd">新增喂养记录</el-button>
      </template>
      <el-table v-loading="loading" :data="list" row-key="id">
        <el-table-column prop="parrotName" label="鹦鹉" min-width="120" />
        <el-table-column prop="feedingDate" label="喂养日期" min-width="110" />
        <el-table-column prop="feedingTime" label="喂养时间" min-width="100" />
        <el-table-column prop="mainFood" label="主食" min-width="130" show-overflow-tooltip />
        <el-table-column prop="supplement" label="辅食" min-width="120" show-overflow-tooltip />
        <el-table-column prop="amount" label="食量" min-width="100" />
        <el-table-column prop="water" label="水量" min-width="100" />
        <el-table-column label="吃完" min-width="90">
          <template #default="{ row }">
            <StatusTag :text="row.isFinished === 1 ? '已吃完' : '有剩余'" />
          </template>
        </el-table-column>
        <el-table-column prop="leftover" label="剩余情况" min-width="130" show-overflow-tooltip />
        <el-table-column prop="recorderName" label="记录人" min-width="110" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" :icon="Edit" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="adminRole" size="small" type="danger" :icon="Delete" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </PageTable>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑喂养记录' : '新增喂养记录'" width="780px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="92px" class="form-grid">
        <el-form-item label="鹦鹉" prop="parrotId">
          <el-select v-model="form.parrotId" filterable style="width: 100%">
            <el-option v-for="item in parrotList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="喂养日期" prop="feedingDate">
          <el-date-picker v-model="form.feedingDate" value-format="YYYY-MM-DD" type="date" style="width: 100%" />
        </el-form-item>
        <el-form-item label="喂养时间">
          <el-time-picker v-model="form.feedingTime" value-format="HH:mm:ss" format="HH:mm" style="width: 100%" />
        </el-form-item>
        <el-form-item label="主食">
          <el-input v-model="form.mainFood" placeholder="如混合谷物、滋养丸" />
        </el-form-item>
        <el-form-item label="辅食">
          <el-input v-model="form.supplement" placeholder="如苹果、青菜、蛋小米" />
        </el-form-item>
        <el-form-item label="食量">
          <el-input v-model="form.amount" placeholder="如 30g / 半碗" />
        </el-form-item>
        <el-form-item label="饮水">
          <el-input v-model="form.water" placeholder="如 已换新水" />
        </el-form-item>
        <el-form-item label="是否吃完">
          <el-radio-group v-model="form.isFinished">
            <el-radio :value="1">已吃完</el-radio>
            <el-radio :value="0">有剩余</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="剩余情况" class="full-line">
          <el-input v-model="form.leftover" placeholder="记录剩余食物或挑食情况" />
        </el-form-item>
        <el-form-item label="备注" class="full-line">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Edit, Plus, Search } from '@element-plus/icons-vue'
import PageTable from '../../components/PageTable.vue'
import StatusTag from '../../components/StatusTag.vue'
import { addFeeding, deleteFeeding, getFeedingPage, updateFeeding } from '../../api/feeding'
import { getParrotPage } from '../../api/parrot'
import { useUserStore } from '../../store/user'
import { isAdmin } from '../../utils/auth'

const userStore = useUserStore()
const adminRole = computed(() => isAdmin(userStore.role))
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const list = ref([])
const parrotList = ref([])
const total = ref(0)
const dateRange = ref([])

const query = reactive({
  page: 1,
  size: 10,
  parrotId: '',
  recorderId: ''
})

const form = reactive(defaultForm())
const rules = {
  parrotId: [{ required: true, message: '请选择鹦鹉', trigger: 'change' }],
  feedingDate: [{ required: true, message: '请选择喂养日期', trigger: 'change' }]
}

function defaultForm() {
  return {
    id: '',
    parrotId: '',
    feedingDate: '',
    feedingTime: '',
    mainFood: '',
    supplement: '',
    amount: '',
    water: '',
    isFinished: 1,
    leftover: '',
    remark: ''
  }
}

async function loadParrots() {
  const data = await getParrotPage({ page: 1, size: 100 })
  parrotList.value = data.records || []
}

async function loadList() {
  loading.value = true
  try {
    const data = await getFeedingPage(buildParams())
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
  Object.assign(query, { page: 1, size: 10, parrotId: '', recorderId: '' })
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

function openAdd() {
  Object.assign(form, defaultForm(), { feedingDate: todayText() })
  dialogVisible.value = true
}

function openEdit(row) {
  Object.assign(form, defaultForm(), row)
  dialogVisible.value = true
}

async function submit() {
  await formRef.value.validate()
  saving.value = true
  try {
    const data = cleanParams({ ...form })
    if (form.id) {
      await updateFeeding(form.id, data)
    } else {
      await addFeeding(data)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadList()
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除“${row.parrotName}”在 ${row.feedingDate} 的喂养记录吗？`, '操作确认', { type: 'warning' })
  await deleteFeeding(row.id)
  ElMessage.success('删除成功')
  loadList()
}

function resetForm() {
  formRef.value?.clearValidate()
  Object.assign(form, defaultForm())
}

function todayText() {
  return new Date().toISOString().slice(0, 10)
}

onMounted(async () => {
  await loadParrots()
  loadList()
})
</script>

<style scoped>
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 12px;
}

.full-line {
  grid-column: span 2;
}

@media (max-width: 760px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .full-line {
    grid-column: auto;
  }
}
</style>
