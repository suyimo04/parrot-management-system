<template>
  <div class="page-wrap">
    <h2 class="page-title">健康记录管理</h2>

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
        <el-form-item label="异常">
          <el-select v-model="query.hasAbnormal" clearable placeholder="全部" style="width: 120px">
            <el-option label="有异常" :value="1" />
            <el-option label="无异常" :value="0" />
          </el-select>
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
        <el-button v-if="canDo('care:health:add')" type="primary" :icon="Plus" @click="openAdd">新增健康记录</el-button>
      </template>
      <el-table v-loading="loading" :data="list" row-key="id">
        <el-table-column prop="parrotName" label="鹦鹉" min-width="120" />
        <el-table-column prop="recordDate" label="日期" min-width="110" />
        <el-table-column label="体重" min-width="90">
          <template #default="{ row }">{{ row.weight ? `${row.weight}g` : '-' }}</template>
        </el-table-column>
        <el-table-column prop="spirit" label="精神" min-width="100" />
        <el-table-column prop="appetite" label="食欲" min-width="100" />
        <el-table-column prop="feather" label="羽毛" min-width="100" />
        <el-table-column prop="excrement" label="粪便" min-width="100" />
        <el-table-column label="异常" min-width="100">
          <template #default="{ row }">
            <StatusTag :text="row.abnormalSymptoms ? '有异常' : '无异常'" />
          </template>
        </el-table-column>
        <el-table-column label="复查" min-width="90">
          <template #default="{ row }">{{ row.needReview === 1 ? '需要' : '不需要' }}</template>
        </el-table-column>
        <el-table-column prop="reviewDate" label="复查日期" min-width="110" />
        <el-table-column prop="recorderName" label="记录人" min-width="110" />
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <div class="table-actions">
              <el-tooltip v-if="canDo('care:health:edit')" content="编辑" placement="top">
                <el-button circle size="small" :icon="Edit" @click="openEdit(row)" />
              </el-tooltip>
              <el-tooltip v-if="canDo('care:health:delete')" content="删除" placement="top">
                <el-button circle size="small" type="danger" :icon="Delete" @click="remove(row)" />
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </PageTable>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑健康记录' : '新增健康记录'" width="820px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="92px" class="form-grid">
        <el-form-item label="鹦鹉" prop="parrotId">
          <el-select v-model="form.parrotId" filterable style="width: 100%">
            <el-option v-for="item in parrotList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="记录日期" prop="recordDate">
          <el-date-picker v-model="form.recordDate" value-format="YYYY-MM-DD" type="date" style="width: 100%" />
        </el-form-item>
        <el-form-item label="体重(g)">
          <el-input-number v-model="form.weight" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="精神状态">
          <el-select v-model="form.spirit" clearable style="width: 100%">
            <el-option label="活跃" value="活跃" />
            <el-option label="正常" value="正常" />
            <el-option label="较弱" value="较弱" />
            <el-option label="嗜睡" value="嗜睡" />
          </el-select>
        </el-form-item>
        <el-form-item label="食欲">
          <el-select v-model="form.appetite" clearable style="width: 100%">
            <el-option label="良好" value="良好" />
            <el-option label="一般" value="一般" />
            <el-option label="较差" value="较差" />
          </el-select>
        </el-form-item>
        <el-form-item label="羽毛">
          <el-input v-model="form.feather" placeholder="如光亮、蓬松、掉羽" />
        </el-form-item>
        <el-form-item label="粪便">
          <el-input v-model="form.excrement" placeholder="如正常、偏稀、颜色异常" />
        </el-form-item>
        <el-form-item label="是否复查">
          <el-radio-group v-model="form.needReview">
            <el-radio :value="1">需要</el-radio>
            <el-radio :value="0">不需要</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="复查日期">
          <el-date-picker v-model="form.reviewDate" value-format="YYYY-MM-DD" type="date" style="width: 100%" />
        </el-form-item>
        <el-form-item label="异常症状" class="full-line">
          <el-input v-model="form.abnormalSymptoms" type="textarea" :rows="3" placeholder="没有异常可以留空" />
        </el-form-item>
        <el-form-item label="治疗情况" class="full-line">
          <el-input v-model="form.treatment" type="textarea" :rows="3" placeholder="记录观察、用药或处理情况" />
        </el-form-item>
        <el-form-item label="备注" class="full-line">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button v-if="canSave" type="primary" :loading="saving" @click="submit">保存</el-button>
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
import { addHealth, deleteHealth, getHealthPage, updateHealth } from '../../api/health'
import { getParrotPage } from '../../api/parrot'
import { useUserStore } from '../../store/user'
import { hasAction } from '../../utils/auth'

const userStore = useUserStore()
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
  hasAbnormal: ''
})

const form = reactive(defaultForm())
const rules = {
  parrotId: [{ required: true, message: '请选择鹦鹉', trigger: 'change' }],
  recordDate: [{ required: true, message: '请选择记录日期', trigger: 'change' }]
}

const canSave = computed(() => form.id ? canDo('care:health:edit') : canDo('care:health:add'))

function canDo(code) {
  return hasAction(userStore, code)
}

function defaultForm() {
  return {
    id: '',
    parrotId: '',
    recordDate: '',
    weight: 0,
    spirit: '正常',
    appetite: '良好',
    feather: '',
    excrement: '',
    abnormalSymptoms: '',
    treatment: '',
    needReview: 0,
    reviewDate: '',
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
    const params = buildParams()
    const data = await getHealthPage(params)
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
  Object.assign(query, { page: 1, size: 10, parrotId: '', hasAbnormal: '' })
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
  Object.assign(form, defaultForm(), { recordDate: todayText() })
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
      await updateHealth(form.id, data)
    } else {
      await addHealth(data)
    }
    if (form.abnormalSymptoms?.trim()) {
      ElMessage.success('保存成功，已同步将鹦鹉标记为观察中')
    } else {
      ElMessage.success('保存成功')
    }
    dialogVisible.value = false
    loadList()
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除“${row.parrotName}”在 ${row.recordDate} 的健康记录吗？`, '操作确认', { type: 'warning' })
  await deleteHealth(row.id)
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
