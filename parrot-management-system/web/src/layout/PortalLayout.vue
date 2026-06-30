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
  background: rgba(255, 255, 255, 0.97);
  border-bottom: 1px solid var(--pm-border);
  box-shadow: 0 4px 16px rgba(36, 50, 63, 0.05);
  backdrop-filter: blur(10px);
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
  font-size: 22px;
}

.portal-nav {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 18px;
}

.portal-nav a {
  position: relative;
  padding: 22px 0 20px;
  color: var(--pm-text-light);
  transition: color 0.16s ease;
}

.portal-nav a:hover {
  color: var(--pm-primary);
}

.portal-nav a.router-link-active {
  color: var(--pm-primary);
  font-weight: 700;
}

.portal-nav a.router-link-active::after {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 13px;
  height: 3px;
  border-radius: 3px;
  background: var(--pm-primary);
  content: "";
}

.portal-user {
  display: flex;
  align-items: center;
  gap: 12px;
  white-space: nowrap;
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
    gap: 16px;
    overflow-x: auto;
  }

  .portal-nav a {
    padding: 4px 0 8px;
  }

  .portal-nav a.router-link-active::after {
    bottom: 0;
  }
}
</style>
