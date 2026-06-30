<template>
  <div class="page-wrap">
    <h2 class="page-title">菜单管理</h2>

    <div class="permission-layout">
      <section class="panel-card permission-side">
        <el-tabs v-model="activeTab" stretch @tab-change="changeTab">
          <el-tab-pane label="按身份配置" name="role">
            <el-form label-position="top">
              <el-form-item label="身份类型">
                <el-select v-model="roleForm.role" style="width: 100%" @change="loadRoleMenus">
                  <el-option label="管理员" value="ADMIN" />
                  <el-option label="饲养员" value="KEEPER" />
                  <el-option label="客户" value="CUSTOMER" />
                </el-select>
              </el-form-item>
            </el-form>
            <div class="tip-text">身份权限适合批量配置，同一身份的账号会共享这些后台菜单。</div>
          </el-tab-pane>

          <el-tab-pane label="按用户配置" name="user">
            <el-form label-position="top">
              <el-form-item label="选择用户">
                <el-select
                  v-model="userForm.userId"
                  filterable
                  clearable
                  placeholder="请选择用户"
                  style="width: 100%"
                  @change="loadUserMenus"
                >
                  <el-option
                    v-for="item in users"
                    :key="item.id"
                    :label="`${item.username}（${roleText(item.role)}）`"
                    :value="item.id"
                  />
                </el-select>
              </el-form-item>
            </el-form>
            <div class="tip-text">用户权限用于单独加开菜单，会和身份权限合并生效。</div>
          </el-tab-pane>
        </el-tabs>

        <div class="side-actions">
          <el-button :icon="RefreshLeft" @click="reloadChecked">重新读取</el-button>
          <el-button v-if="canDo('system:menu:save')" type="primary" :icon="Check" :loading="saving" @click="savePermission">保存配置</el-button>
        </div>
      </section>

      <section class="panel-card permission-main">
        <div class="tree-head">
          <div>
            <h3>可访问页面和功能</h3>
            <p>勾选后，对应菜单会显示在后台左侧，同时后端接口也会做权限判断。</p>
          </div>
          <div class="tree-tools">
            <el-button size="small" @click="checkAll">全选</el-button>
            <el-button size="small" @click="clearAll">清空</el-button>
          </div>
        </div>

        <el-tree
          ref="treeRef"
          v-loading="loading"
          :data="menuTree"
          node-key="id"
          show-checkbox
          default-expand-all
          :props="treeProps"
          :check-strictly="true"
          class="menu-tree"
        >
          <template #default="{ data }">
            <div class="tree-node">
              <span>{{ data.menuName }}</span>
              <el-tag v-if="data.path" size="small" effect="plain">{{ data.path }}</el-tag>
            </div>
          </template>
        </el-tree>
      </section>
    </div>
  </div>
</template>

<script setup>
import { nextTick, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Check, RefreshLeft } from '@element-plus/icons-vue'
import { getUserPage } from '../../api/user'
import { getMenuTree, getRoleMenus, getUserMenus, saveRoleMenus, saveUserMenus } from '../../api/menu'
import { useUserStore } from '../../store/user'
import { hasAction } from '../../utils/auth'

const userStore = useUserStore()
const activeTab = ref('role')
const loading = ref(false)
const saving = ref(false)
const menuTree = ref([])
const users = ref([])
const treeRef = ref()

const roleForm = reactive({
  role: 'KEEPER'
})

const userForm = reactive({
  userId: ''
})

const treeProps = {
  label: 'menuName',
  children: 'children'
}

async function loadBaseData() {
  loading.value = true
  try {
    const [treeData, userData] = await Promise.all([
      getMenuTree(),
      getUserPage({ page: 1, size: 100 })
    ])
    menuTree.value = treeData || []
    users.value = userData.records || []
    await nextTick()
    await loadRoleMenus()
  } finally {
    loading.value = false
  }
}

async function loadRoleMenus() {
  if (!roleForm.role) return
  const ids = await getRoleMenus(roleForm.role)
  setChecked(ids)
}

async function loadUserMenus() {
  if (!userForm.userId) {
    clearAll()
    return
  }
  const ids = await getUserMenus(userForm.userId)
  setChecked(ids)
}

function changeTab() {
  reloadChecked()
}

function reloadChecked() {
  if (activeTab.value === 'role') {
    loadRoleMenus()
  } else {
    loadUserMenus()
  }
}

async function savePermission() {
  const checkedIds = collectCheckedIds()
  saving.value = true
  try {
    if (activeTab.value === 'role') {
      await saveRoleMenus({ role: roleForm.role, menuIds: checkedIds })
    } else {
      if (!userForm.userId) {
        ElMessage.warning('请先选择用户')
        return
      }
      await saveUserMenus({ userId: userForm.userId, menuIds: checkedIds })
    }
    ElMessage.success('菜单权限已保存')
  } finally {
    saving.value = false
  }
}

function collectCheckedIds() {
  return treeRef.value?.getCheckedKeys(false) || []
}

function setChecked(ids) {
  nextTick(() => {
    treeRef.value?.setCheckedKeys(ids || [], false)
  })
}

function checkAll() {
  treeRef.value?.setCheckedKeys(flatMenuIds(menuTree.value), false)
}

function clearAll() {
  treeRef.value?.setCheckedKeys([], false)
}

function flatMenuIds(list) {
  const ids = []
  list.forEach(item => {
    ids.push(item.id)
    if (item.children?.length) {
      ids.push(...flatMenuIds(item.children))
    }
  })
  return ids
}

function roleText(role) {
  if (role === 'ADMIN') return '管理员'
  if (role === 'KEEPER') return '饲养员'
  return '客户'
}

function canDo(code) {
  return hasAction(userStore, code)
}

onMounted(loadBaseData)
</script>

<style scoped>
.permission-layout {
  display: grid;
  grid-template-columns: 320px minmax(0, 1fr);
  gap: 16px;
}

.permission-side,
.permission-main {
  padding: 18px;
}

.tip-text {
  color: var(--pm-text-light);
  line-height: 1.7;
}

.side-actions {
  display: flex;
  gap: 10px;
  margin-top: 18px;
}

.tree-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.tree-head h3 {
  margin: 0 0 6px;
  font-size: 16px;
}

.tree-head p {
  margin: 0;
  color: var(--pm-text-light);
  line-height: 1.6;
}

.tree-tools {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.menu-tree {
  min-height: 420px;
  padding: 8px 0;
}

.tree-node {
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.tree-node .el-tag {
  max-width: 260px;
}

@media (max-width: 900px) {
  .permission-layout {
    grid-template-columns: 1fr;
  }

  .tree-head {
    flex-direction: column;
  }
}
</style>
