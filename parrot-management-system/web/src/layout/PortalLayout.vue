<template>
  <div class="portal-layout">
    <header class="portal-header">
      <router-link class="portal-logo" to="/portal/home">
        <el-icon><Star /></el-icon>
        <span>青羽鹦鹉园</span>
      </router-link>
      <nav class="portal-nav">
        <router-link to="/portal/home">首页</router-link>
        <router-link to="/portal/parrot">鹦鹉展示</router-link>
        <router-link to="/portal/notice">公告知识</router-link>
        <router-link v-if="userStore.isLogin && userStore.role === 'CUSTOMER'" to="/portal/user">个人中心</router-link>
        <router-link v-if="userStore.isLogin && userStore.role === 'CUSTOMER'" to="/portal/appointment">我的预约</router-link>
      </nav>
      <div class="portal-user">
        <template v-if="userStore.isLogin">
          <span>{{ userStore.showName }}</span>
          <el-button text :icon="SwitchButton" @click="logout">退出</el-button>
        </template>
        <template v-else>
          <router-link to="/login">登录</router-link>
          <el-button type="primary" size="small" @click="router.push('/register')">注册</el-button>
        </template>
      </div>
    </header>

    <main class="portal-main">
      <router-view />
    </main>

    <footer class="portal-footer">
      © 2026 青羽鹦鹉管理系统 · 毕业设计演示项目
    </footer>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Star, SwitchButton } from '@element-plus/icons-vue'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()

function logout() {
  userStore.logout()
  router.push('/portal/home')
}
</script>

<style scoped>
.portal-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--pm-bg);
}

.portal-header {
  position: sticky;
  top: 0;
  z-index: 10;
  height: 64px;
  display: flex;
  align-items: center;
  gap: 26px;
  padding: 0 32px;
  background: rgba(255, 255, 255, 0.96);
  border-bottom: 1px solid var(--pm-border);
}

.portal-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--pm-text);
  font-size: 18px;
  font-weight: 700;
}

.portal-logo .el-icon {
  color: var(--pm-primary);
}

.portal-nav {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 18px;
}

.portal-nav a {
  color: var(--pm-text-light);
}

.portal-nav a.router-link-active {
  color: var(--pm-primary);
  font-weight: 700;
}

.portal-user {
  display: flex;
  align-items: center;
  gap: 12px;
}

.portal-main {
  flex: 1;
}

.portal-footer {
  padding: 18px;
  color: var(--pm-text-light);
  text-align: center;
  border-top: 1px solid var(--pm-border);
  background: #fff;
}

@media (max-width: 768px) {
  .portal-header {
    height: auto;
    flex-wrap: wrap;
    gap: 12px;
    padding: 12px;
  }

  .portal-nav {
    order: 3;
    width: 100%;
    overflow-x: auto;
  }
}
</style>
