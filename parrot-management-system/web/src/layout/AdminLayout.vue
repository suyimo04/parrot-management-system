<template>
  <div class="admin-layout">
    <aside class="admin-sidebar" :class="{ collapsed }">
      <div class="admin-logo">
        <el-icon><Star /></el-icon>
        <span v-if="!collapsed">鹦鹉管理系统</span>
      </div>
      <el-menu
        :default-active="route.path"
        class="admin-menu"
        :collapse="collapsed"
        router
        background-color="#24323F"
        text-color="#C7D0DD"
        active-text-color="#2ECC71"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataBoard /></el-icon>
          <template #title>首页看板</template>
        </el-menu-item>
        <el-sub-menu index="parrot">
          <template #title>
            <el-icon><Star /></el-icon>
            <span>鹦鹉管理</span>
          </template>
          <el-menu-item index="/admin/parrot/list">鹦鹉档案管理</el-menu-item>
          <el-menu-item index="/admin/parrot/species">品种管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="care">
          <template #title>
            <el-icon><FirstAidKit /></el-icon>
            <span>养护管理</span>
          </template>
          <el-menu-item index="/admin/health">健康记录管理</el-menu-item>
          <el-menu-item index="/admin/feeding">喂养记录管理</el-menu-item>
          <el-menu-item index="/admin/training">训练记录管理</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/admin/appointment">
          <el-icon><Calendar /></el-icon>
          <template #title>预约咨询管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/notice">
          <el-icon><Document /></el-icon>
          <template #title>公告知识管理</template>
        </el-menu-item>
        <el-sub-menu v-if="isAdmin(userStore.role)" index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/admin/user">用户管理</el-menu-item>
          <el-menu-item index="/admin/login-log">登录日志</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </aside>

    <section class="admin-main">
      <header class="admin-header">
        <div class="admin-header__left">
          <el-button text :icon="Fold" @click="collapsed = !collapsed" />
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>后台管理</el-breadcrumb-item>
            <el-breadcrumb-item>{{ route.meta.title || '首页' }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="admin-header__right">
          <el-tag type="success" effect="light">{{ roleText }}</el-tag>
          <span>{{ userStore.showName }}</span>
          <el-button text :icon="SwitchButton" @click="logout">退出</el-button>
        </div>
      </header>

      <TagsView />
      <main class="admin-content">
        <router-view />
      </main>
    </section>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { DataBoard, FirstAidKit, Fold, Star, Calendar, Document, Setting, SwitchButton } from '@element-plus/icons-vue'
import TagsView from './TagsView.vue'
import { useUserStore } from '../store/user'
import { isAdmin } from '../utils/auth'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const collapsed = ref(false)

const roleText = computed(() => {
  if (userStore.role === 'ADMIN') return '管理员'
  if (userStore.role === 'KEEPER') return '饲养员'
  return '客户'
})

function logout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  display: flex;
  overflow: hidden;
  background: var(--pm-bg);
}

.admin-sidebar {
  width: 238px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  background: #24323F;
  transition: width 0.2s ease;
}

.admin-sidebar.collapsed {
  width: 64px;
}

.admin-logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 17px;
  font-weight: 700;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.admin-logo .el-icon {
  color: var(--pm-secondary);
}

.admin-menu {
  flex: 1;
  border-right: 0;
}

.admin-main {
  min-width: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.admin-header {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 18px;
  background: #fff;
  border-bottom: 1px solid var(--pm-border);
}

.admin-header__left,
.admin-header__right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.admin-content {
  flex: 1;
  min-height: 0;
  overflow: auto;
}

@media (max-width: 900px) {
  .admin-sidebar {
    width: 64px;
  }

  .admin-logo span {
    display: none;
  }
}
</style>
