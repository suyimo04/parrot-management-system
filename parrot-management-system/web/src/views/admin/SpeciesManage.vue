<template>
  <div class="page-wrap">
    <h2 class="page-title">品种管理</h2>

    <div class="filter-card">
      <el-form :model="query" inline>
        <el-form-item label="品种名称">
          <el-input v-model="query.name" clearable placeholder="请输入品种名称" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部" style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
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
        <el-button v-if="canDo('parrot:species:add')" type="primary" :icon="Plus" @click="openAdd">新增品种</el-button>
      </template>
      <el-table v-loading="loading" :data="list" row-key="id">
        <el-table-column label="图片" width="90">
          <template #default="{ row }">
            <el-image v-if="row.image" class="thumb" :src="row.image" fit="cover" :preview-src-list="[row.image]" />
            <span v-else class="muted">暂无</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" min-width="130" />
        <el-table-column prop="englishName" label="英文名" min-width="150" show-overflow-tooltip />
        <el-table-column prop="origin" label="产地" min-width="140" show-overflow-tooltip />
        <el-table-column prop="size" label="体型" min-width="100" />
        <el-table-column prop="avgLifespan" label="寿命" min-width="110" />
        <el-table-column prop="difficulty" label="难度" min-width="100" />
        <el-table-column label="状态" min-width="90">
          <template #default="{ row }"><StatusTag :text="row.status === 1 ? '启用' : '禁用'" /></template>
        </el-table-column>
        <el-table-column v-if="canDo('parrot:species:edit') || canDo('parrot:species:delete')" label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <div class="table-actions">
              <el-tooltip v-if="canDo('parrot:species:edit')" content="编辑" placement="top">
                <el-button circle size="small" :icon="Edit" @click="openEdit(row)" />
              </el-tooltip>
              <el-tooltip v-if="canDo('parrot:species:delete')" content="删除" placement="top">
                <el-button circle size="small" type="danger" :icon="Delete" @click="remove(row)" />
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </PageTable>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑品种' : '新增品种'" width="760px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="92px" class="form-grid">
        <el-form-item label="品种图片" class="full-line">
          <ImageUpload v-model="form.image" />
        </el-form-item>
        <el-form-item label="品种名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="英文名">
          <el-input v-model="form.englishName" />
        </el-form-item>
        <el-form-item label="产地">
          <el-input v-model="form.origin" />
        </el-form-item>
        <el-form-item label="体型">
          <el-input v-model="form.size" placeholder="小型 / 中型 / 大型" />
        </el-form-item>
        <el-form-item label="平均寿命">
          <el-input v-model="form.avgLifespan" placeholder="如 20-30年" />
        </el-form-item>
        <el-form-item label="饲养难度">
          <el-select v-model="form.difficulty" clearable style="width: 100%">
            <el-option label="入门" value="入门" />
            <el-option label="中等" value="中等" />
            <el-option label="较难" value="较难" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="生活习性" class="full-line">
          <el-input v-model="form.habits" type="textarea" :rows="4" placeholder="简单记录该品种的活动、饮食和性格特点" />
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
import ImageUpload from '../../components/ImageUpload.vue'
import { addSpecies, deleteSpecies, getSpeciesPage, updateSpecies } from '../../api/species'
import { useUserStore } from '../../store/user'
import { hasAction } from '../../utils/auth'

const userStore = useUserStore()
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const list = ref([])
const total = ref(0)

const query = reactive({
  page: 1,
  size: 10,
  name: '',
  status: ''
})

const form = reactive(defaultForm())
const rules = {
  name: [{ required: true, message: '请输入品种名称', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const canSave = computed(() => form.id ? canDo('parrot:species:edit') : canDo('parrot:species:add'))

function canDo(code) {
  return hasAction(userStore, code)
}

function defaultForm() {
  return {
    id: '',
    name: '',
    englishName: '',
    origin: '',
    size: '',
    avgLifespan: '',
    habits: '',
    difficulty: '',
    image: '',
    status: 1
  }
}

async function loadList() {
  loading.value = true
  try {
    const data = await getSpeciesPage(cleanParams(query))
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
  Object.assign(query, { page: 1, size: 10, name: '', status: '' })
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
      await updateSpecies(form.id, form)
    } else {
      await addSpecies(form)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadList()
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除或禁用品种“${row.name}”吗？`, '操作确认', { type: 'warning' })
  await deleteSpecies(row.id)
  ElMessage.success('操作成功')
  loadList()
}

function resetForm() {
  formRef.value?.clearValidate()
  Object.assign(form, defaultForm())
}

onMounted(loadList)
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

@media (max-width: 720px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .full-line {
    grid-column: auto;
  }
}
</style>
