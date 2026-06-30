<template>
  <div class="page-wrap">
    <h2 class="page-title">公告知识管理</h2>

    <div class="filter-card">
      <el-form :model="query" inline>
        <el-form-item label="标题">
          <el-input v-model="query.title" clearable placeholder="公告标题" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.type" clearable placeholder="全部" style="width: 140px">
            <el-option label="系统公告" value="系统公告" />
            <el-option label="饲养知识" value="饲养知识" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布状态">
          <el-select v-model="query.publishStatus" clearable placeholder="全部" style="width: 140px">
            <el-option label="已发布" value="已发布" />
            <el-option label="未发布" value="未发布" />
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
        <el-button v-if="canDo('notice:add')" type="primary" :icon="Plus" @click="openAdd">新增公告</el-button>
      </template>
      <el-table v-loading="loading" :data="list" row-key="id">
        <el-table-column label="封面" width="90">
          <template #default="{ row }">
            <el-image v-if="row.coverImage" class="thumb" :src="row.coverImage" fit="cover" :preview-src-list="[row.coverImage]" />
            <span v-else class="muted">暂无</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="220" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" min-width="110" />
        <el-table-column label="发布状态" min-width="110">
          <template #default="{ row }"><StatusTag :text="row.publishStatus" /></template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" min-width="170" />
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column prop="creatorName" label="创建人" min-width="110" />
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <div class="table-actions">
              <el-tooltip v-if="canDo('notice:edit')" content="编辑" placement="top">
                <el-button circle size="small" :icon="Edit" @click="openEdit(row)" />
              </el-tooltip>
              <el-tooltip v-if="canDo('notice:delete')" content="删除" placement="top">
                <el-button circle size="small" type="danger" :icon="Delete" @click="remove(row)" />
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </PageTable>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑公告知识' : '新增公告知识'" width="820px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="92px" class="form-grid">
        <el-form-item label="封面图片" class="full-line">
          <ImageUpload v-model="form.coverImage" />
        </el-form-item>
        <el-form-item label="标题" prop="title" class="full-line">
          <el-input v-model="form.title" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" style="width: 100%">
            <el-option label="系统公告" value="系统公告" />
            <el-option label="饲养知识" value="饲养知识" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布状态" prop="publishStatus">
          <el-radio-group v-model="form.publishStatus">
            <el-radio value="已发布">已发布</el-radio>
            <el-radio value="未发布">未发布</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="内容" prop="content" class="full-line">
          <el-input v-model="form.content" type="textarea" :rows="10" placeholder="填写公告或饲养知识正文，前台会按换行展示" />
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
import { addNotice, deleteNotice, getNoticePage, updateNotice } from '../../api/notice'
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
  title: '',
  type: '',
  publishStatus: ''
})

const form = reactive(defaultForm())
const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  publishStatus: [{ required: true, message: '请选择发布状态', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const canSave = computed(() => form.id ? canDo('notice:edit') : canDo('notice:add'))

function canDo(code) {
  return hasAction(userStore, code)
}

function defaultForm() {
  return {
    id: '',
    title: '',
    content: '',
    type: '系统公告',
    coverImage: '',
    publishStatus: '未发布'
  }
}

async function loadList() {
  loading.value = true
  try {
    const data = await getNoticePage(cleanParams(query))
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
  Object.assign(query, { page: 1, size: 10, title: '', type: '', publishStatus: '' })
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
    const data = cleanParams({ ...form })
    if (form.id) {
      await updateNotice(form.id, data)
    } else {
      await addNotice(data)
    }
    ElMessage.success(form.publishStatus === '已发布' ? '保存成功，前台已可查看' : '保存成功')
    dialogVisible.value = false
    loadList()
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除公告“${row.title}”吗？`, '操作确认', { type: 'warning' })
  await deleteNotice(row.id)
  ElMessage.success('删除成功')
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

@media (max-width: 760px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .full-line {
    grid-column: auto;
  }
}
</style>
