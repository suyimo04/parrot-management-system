<template>
  <div class="page-wrap">
    <h2 class="page-title">训练记录管理</h2>

    <div class="filter-card">
      <el-form :model="query" inline>
        <el-form-item label="鹦鹉">
          <el-select v-model="query.parrotId" clearable filterable placeholder="全部鹦鹉" style="width: 160px">
            <el-option v-for="item in parrotList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="训练项目">
          <el-input v-model="query.project" clearable placeholder="如握手、回笼" />
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
        <el-button v-if="canDo('care:training:add')" type="primary" :icon="Plus" @click="openAdd">新增训练记录</el-button>
      </template>
      <el-table v-loading="loading" :data="list" row-key="id">
        <el-table-column prop="parrotName" label="鹦鹉" min-width="120" />
        <el-table-column prop="trainingDate" label="训练日期" min-width="110" />
        <el-table-column prop="project" label="项目" min-width="130" />
        <el-table-column label="时长" min-width="90">
          <template #default="{ row }">{{ row.duration ?? 0 }}分钟</template>
        </el-table-column>
        <el-table-column prop="cooperation" label="配合程度" min-width="110" />
        <el-table-column prop="completion" label="完成情况" min-width="110" />
        <el-table-column prop="reward" label="奖励" min-width="130" show-overflow-tooltip />
        <el-table-column prop="recorderName" label="记录人" min-width="110" />
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <div class="table-actions">
              <el-tooltip v-if="canDo('care:training:edit')" content="编辑" placement="top">
                <el-button circle size="small" :icon="Edit" @click="openEdit(row)" />
              </el-tooltip>
              <el-tooltip v-if="canDo('care:training:delete')" content="删除" placement="top">
                <el-button circle size="small" type="danger" :icon="Delete" @click="remove(row)" />
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </PageTable>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑训练记录' : '新增训练记录'" width="820px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="92px" class="form-grid">
        <el-form-item label="鹦鹉" prop="parrotId">
          <el-select v-model="form.parrotId" filterable style="width: 100%">
            <el-option v-for="item in parrotList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="训练日期" prop="trainingDate">
          <el-date-picker v-model="form.trainingDate" value-format="YYYY-MM-DD" type="date" style="width: 100%" />
        </el-form-item>
        <el-form-item label="训练项目" prop="project">
          <el-input v-model="form.project" placeholder="如回笼、握手、简单口令" />
        </el-form-item>
        <el-form-item label="时长(分钟)">
          <el-input-number v-model="form.duration" :min="0" :max="300" style="width: 100%" />
        </el-form-item>
        <el-form-item label="配合程度">
          <el-select v-model="form.cooperation" clearable style="width: 100%">
            <el-option label="很配合" value="很配合" />
            <el-option label="一般" value="一般" />
            <el-option label="抗拒" value="抗拒" />
          </el-select>
        </el-form-item>
        <el-form-item label="完成情况">
          <el-select v-model="form.completion" clearable style="width: 100%">
            <el-option label="完成较好" value="完成较好" />
            <el-option label="部分完成" value="部分完成" />
            <el-option label="未完成" value="未完成" />
          </el-select>
        </el-form-item>
        <el-form-item label="奖励" class="full-line">
          <el-input v-model="form.reward" placeholder="如葵花籽、苹果丁、口头奖励" />
        </el-form-item>
        <el-form-item label="训练内容" class="full-line">
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="记录训练过程和表现" />
        </el-form-item>
        <el-form-item label="下次建议" class="full-line">
          <el-input v-model="form.nextSuggestion" type="textarea" :rows="3" />
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
import { addTraining, deleteTraining, getTrainingPage, updateTraining } from '../../api/training'
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
  project: ''
})

const form = reactive(defaultForm())
const rules = {
  parrotId: [{ required: true, message: '请选择鹦鹉', trigger: 'change' }],
  trainingDate: [{ required: true, message: '请选择训练日期', trigger: 'change' }],
  project: [{ required: true, message: '请输入训练项目', trigger: 'blur' }]
}

const canSave = computed(() => form.id ? canDo('care:training:edit') : canDo('care:training:add'))

function canDo(code) {
  return hasAction(userStore, code)
}

function defaultForm() {
  return {
    id: '',
    parrotId: '',
    trainingDate: '',
    duration: 30,
    project: '',
    content: '',
    cooperation: '',
    completion: '',
    reward: '',
    nextSuggestion: '',
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
    const data = await getTrainingPage(buildParams())
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
  Object.assign(query, { page: 1, size: 10, parrotId: '', project: '' })
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
  Object.assign(form, defaultForm(), { trainingDate: todayText() })
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
      await updateTraining(form.id, data)
    } else {
      await addTraining(data)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadList()
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除“${row.parrotName}”的“${row.project}”训练记录吗？`, '操作确认', { type: 'warning' })
  await deleteTraining(row.id)
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
