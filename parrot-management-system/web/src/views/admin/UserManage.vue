<template>
  <div class="page-wrap">
    <h2 class="page-title">用户管理</h2>

    <div class="filter-card">
      <el-form :model="query" inline>
        <el-form-item label="用户名">
          <el-input v-model="query.username" clearable placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="query.role" clearable placeholder="全部" style="width: 140px">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="饲养员" value="KEEPER" />
            <el-option label="客户" value="CUSTOMER" />
          </el-select>
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
        <el-button v-if="canDo('system:user:add')" type="primary" :icon="Plus" @click="openAdd">新增用户</el-button>
      </template>
      <el-table v-loading="loading" :data="list" row-key="id">
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="realName" label="姓名" min-width="110" />
        <el-table-column prop="phone" label="手机" min-width="130" />
        <el-table-column prop="email" label="邮箱" min-width="170" show-overflow-tooltip />
        <el-table-column label="角色" min-width="100">
          <template #default="{ row }">{{ roleText(row.role) }}</template>
        </el-table-column>
        <el-table-column label="状态" min-width="90">
          <template #default="{ row }"><StatusTag :text="row.status === 1 ? '启用' : '禁用'" /></template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="240" fixed="right" align="center">
          <template #default="{ row }">
            <div class="table-actions">
              <el-tooltip v-if="canDo('system:user:edit')" content="编辑" placement="top">
                <el-button circle size="small" :icon="Edit" @click="openEdit(row)" />
              </el-tooltip>
              <el-tooltip v-if="canDo('system:user:reset')" content="重置密码" placement="top">
                <el-button circle size="small" type="warning" :icon="RefreshLeft" @click="resetPassword(row)" />
              </el-tooltip>
              <el-tooltip v-if="canDo('system:user:disable')" content="禁用" placement="top">
                <el-button circle size="small" type="danger" :icon="Delete" :disabled="row.status === 0" @click="disable(row)" />
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </PageTable>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑用户' : '新增用户'" width="680px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="88px" class="form-grid">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" maxlength="50" />
        </el-form-item>
        <el-form-item label="密码" :prop="form.id ? '' : 'password'">
          <el-input v-model="form.password" type="password" show-password placeholder="编辑时不填表示不修改" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机">
          <el-input v-model="form.phone" maxlength="11" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" style="width: 100%">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="饲养员" value="KEEPER" />
            <el-option label="客户" value="CUSTOMER" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
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
import { Delete, Edit, Plus, RefreshLeft, Search } from '@element-plus/icons-vue'
import PageTable from '../../components/PageTable.vue'
import StatusTag from '../../components/StatusTag.vue'
import { addUser, disableUser, getUserPage, resetUserPassword, updateUser } from '../../api/user'
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
  username: '',
  role: '',
  status: ''
})

const form = reactive(defaultForm())

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const canSave = computed(() => form.id ? canDo('system:user:edit') : canDo('system:user:add'))

function canDo(code) {
  return hasAction(userStore, code)
}

function defaultForm() {
  return {
    id: '',
    username: '',
    password: '',
    realName: '',
    phone: '',
    email: '',
    role: 'CUSTOMER',
    status: 1
  }
}

async function loadList() {
  loading.value = true
  try {
    const data = await getUserPage(cleanParams(query))
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
  Object.assign(query, { page: 1, size: 10, username: '', role: '', status: '' })
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
  Object.assign(form, defaultForm(), row, { password: '' })
  dialogVisible.value = true
}

async function submit() {
  await formRef.value.validate()
  saving.value = true
  try {
    const data = { ...form }
    if (form.id && !data.password) {
      delete data.password
    }
    if (form.id) {
      await updateUser(form.id, data)
    } else {
      await addUser(data)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadList()
  } finally {
    saving.value = false
  }
}

async function disable(row) {
  await ElMessageBox.confirm(`确定禁用用户“${row.username}”吗？`, '操作确认', { type: 'warning' })
  await disableUser(row.id)
  ElMessage.success('用户已禁用')
  loadList()
}

async function resetPassword(row) {
  await ElMessageBox.confirm(`确定将“${row.username}”的密码重置为 admin123 吗？`, '操作确认', { type: 'warning' })
  await resetUserPassword(row.id)
  ElMessage.success('密码已重置')
}

function resetForm() {
  formRef.value?.clearValidate()
  Object.assign(form, defaultForm())
}

function roleText(role) {
  if (role === 'ADMIN') return '管理员'
  if (role === 'KEEPER') return '饲养员'
  return '客户'
}

onMounted(loadList)
</script>

<style scoped>
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 12px;
}

@media (max-width: 720px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
