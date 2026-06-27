<template>
  <div class="page-wrap">
    <h2 class="page-title">鹦鹉档案管理</h2>

    <div class="filter-card">
      <el-form :model="query" inline>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" clearable placeholder="编号或名称" />
        </el-form-item>
        <el-form-item label="品种">
          <el-select v-model="query.speciesId" clearable filterable placeholder="全部" style="width: 150px">
            <el-option v-for="item in speciesList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="健康状态">
          <el-select v-model="query.healthStatus" clearable placeholder="全部" style="width: 140px">
            <el-option v-for="item in healthOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="公开">
          <el-select v-model="query.isPublic" clearable placeholder="全部" style="width: 120px">
            <el-option label="公开" :value="1" />
            <el-option label="不公开" :value="0" />
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
        <el-button type="primary" :icon="Plus" @click="openAdd">新增鹦鹉</el-button>
      </template>
      <el-table v-loading="loading" :data="list" row-key="id">
        <el-table-column label="图片" width="90">
          <template #default="{ row }">
            <el-image v-if="row.image" class="thumb" :src="row.image" fit="cover" :preview-src-list="[row.image]" />
            <span v-else class="muted">暂无</span>
          </template>
        </el-table-column>
        <el-table-column prop="code" label="编号" min-width="110" />
        <el-table-column prop="name" label="名称" min-width="120" />
        <el-table-column prop="speciesName" label="品种" min-width="130" />
        <el-table-column prop="gender" label="性别" width="80" />
        <el-table-column prop="age" label="年龄" width="80" />
        <el-table-column label="健康状态" min-width="110">
          <template #default="{ row }"><StatusTag :text="row.healthStatus" /></template>
        </el-table-column>
        <el-table-column prop="currentStatus" label="当前状态" min-width="110" />
        <el-table-column label="公开状态" min-width="100">
          <template #default="{ row }"><StatusTag :text="row.isPublic === 1 ? '公开' : '不公开'" /></template>
        </el-table-column>
        <el-table-column label="操作" width="170" fixed="right">
          <template #default="{ row }">
            <el-button size="small" :icon="Edit" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" :icon="Delete" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </PageTable>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑鹦鹉档案' : '新增鹦鹉档案'" width="860px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="92px" class="form-grid">
        <el-form-item label="图片" class="full-line">
          <ImageUpload v-model="form.image" />
        </el-form-item>
        <el-form-item label="编号" prop="code">
          <el-input v-model="form.code" placeholder="如 P-001" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="品种" prop="speciesId">
          <el-select v-model="form.speciesId" filterable style="width: 100%">
            <el-option v-for="item in speciesList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender" clearable style="width: 100%">
            <el-option label="雄性" value="雄性" />
            <el-option label="雌性" value="雌性" />
            <el-option label="未知" value="未知" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="form.age" :min="0" :max="120" style="width: 100%" />
        </el-form-item>
        <el-form-item label="出生日期">
          <el-date-picker v-model="form.birthDate" value-format="YYYY-MM-DD" type="date" style="width: 100%" />
        </el-form-item>
        <el-form-item label="颜色">
          <el-input v-model="form.color" />
        </el-form-item>
        <el-form-item label="健康状态" prop="healthStatus">
          <el-select v-model="form.healthStatus" style="width: 100%">
            <el-option v-for="item in healthOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="来源">
          <el-input v-model="form.source" />
        </el-form-item>
        <el-form-item label="入园日期">
          <el-date-picker v-model="form.entryDate" value-format="YYYY-MM-DD" type="date" style="width: 100%" />
        </el-form-item>
        <el-form-item label="当前状态" prop="currentStatus">
          <el-select v-model="form.currentStatus" style="width: 100%">
            <el-option label="在园" value="在园" />
            <el-option label="观察中" value="观察中" />
            <el-option label="已预约" value="已预约" />
            <el-option label="已停用" value="已停用" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否公开" prop="isPublic">
          <el-radio-group v-model="form.isPublic">
            <el-radio :value="1">公开</el-radio>
            <el-radio :value="0">不公开</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="性格" class="full-line">
          <el-input v-model="form.personality" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="养护说明" class="full-line">
          <el-input v-model="form.careNotes" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="描述" class="full-line">
          <el-input v-model="form.description" type="textarea" :rows="3" />
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
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Edit, Plus, Search } from '@element-plus/icons-vue'
import PageTable from '../../components/PageTable.vue'
import StatusTag from '../../components/StatusTag.vue'
import ImageUpload from '../../components/ImageUpload.vue'
import { addParrot, deleteParrot, getParrotPage, updateParrot } from '../../api/parrot'
import { getSpeciesList } from '../../api/species'

const healthOptions = ['正常', '观察中', '治疗中', '已预约', '已停用']
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const list = ref([])
const total = ref(0)
const speciesList = ref([])

const query = reactive({
  page: 1,
  size: 10,
  keyword: '',
  speciesId: '',
  healthStatus: '',
  isPublic: ''
})

const form = reactive(defaultForm())
const rules = {
  code: [{ required: true, message: '请输入档案编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入鹦鹉名称', trigger: 'blur' }],
  speciesId: [{ required: true, message: '请选择品种', trigger: 'change' }],
  healthStatus: [{ required: true, message: '请选择健康状态', trigger: 'change' }],
  currentStatus: [{ required: true, message: '请选择当前状态', trigger: 'change' }],
  isPublic: [{ required: true, message: '请选择公开状态', trigger: 'change' }]
}

function defaultForm() {
  return {
    id: '',
    code: '',
    name: '',
    speciesId: '',
    gender: '',
    age: 0,
    birthDate: '',
    color: '',
    image: '',
    healthStatus: '正常',
    source: '',
    entryDate: '',
    personality: '',
    careNotes: '',
    currentStatus: '在园',
    description: '',
    isPublic: 0
  }
}

async function loadSpecies() {
  speciesList.value = await getSpeciesList()
}

async function loadList() {
  loading.value = true
  try {
    const data = await getParrotPage(cleanParams(query))
    list.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function cleanParams(source) {
  return Object.fromEntries(Object.entries(source).filter(([, value]) => value !== '' && value !== null && value !== undefined))
}

function search() {
  query.page = 1
  loadList()
}

function resetQuery() {
  Object.assign(query, { page: 1, size: 10, keyword: '', speciesId: '', healthStatus: '', isPublic: '' })
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
  Object.assign(form, defaultForm())
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
    if (form.id) {
      await updateParrot(form.id, form)
    } else {
      await addParrot(form)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadList()
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除或停用鹦鹉“${row.name}”吗？`, '操作确认', { type: 'warning' })
  await deleteParrot(row.id)
  ElMessage.success('操作成功')
  loadList()
}

function resetForm() {
  formRef.value?.clearValidate()
  Object.assign(form, defaultForm())
}

onMounted(async () => {
  await loadSpecies()
  loadList()
})
</script>

<style scoped>
.thumb {
  width: 52px;
  height: 52px;
  border-radius: 8px;
  border: 1px solid var(--pm-border);
}

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
