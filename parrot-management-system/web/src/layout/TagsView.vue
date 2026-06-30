<template>
  <div class="tags-view" @click="menu.visible = false">
    <div
      v-for="tag in tags"
      :key="tag.path"
      class="tags-view__item"
      :class="{ active: tag.path === route.path }"
      @click="go(tag.path)"
      @contextmenu.prevent.stop="openMenu($event, tag)"
    >
      <span>{{ tag.title }}</span>
      <el-icon v-if="!tag.fixed" @click.stop="closeTag(tag)"><Close /></el-icon>
    </div>

    <div
      v-if="menu.visible"
      class="tags-view__menu"
      :style="{ left: `${menu.x}px`, top: `${menu.y}px` }"
    >
      <div @click="closeTag(menu.tag)">关闭当前</div>
      <div @click="closeOther(menu.tag)">关闭其他</div>
      <div @click="closeAll">关闭所有</div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const tags = ref([{ path: '/admin/dashboard', title: '首页看板', fixed: true }])
const menu = reactive({ visible: false, x: 0, y: 0, tag: null })

watch(
  () => route.fullPath,
  () => {
    if (!route.path.startsWith('/admin')) return
    const title = route.meta.title || '未命名页面'
    if (!tags.value.some(item => item.path === route.path)) {
      tags.value.push({ path: route.path, title, fixed: route.path === '/admin/dashboard' })
    }
  },
  { immediate: true }
)

function go(path) {
  router.push(path)
}

function openMenu(event, tag) {
  menu.visible = true
  menu.x = event.clientX
  menu.y = event.clientY
  menu.tag = tag
}

function closeTag(tag) {
  if (!tag || tag.fixed) return
  const index = tags.value.findIndex(item => item.path === tag.path)
  tags.value = tags.value.filter(item => item.path !== tag.path)
  if (route.path === tag.path) {
    const next = tags.value[index - 1] || tags.value[0]
    router.push(next.path)
  }
}

function closeOther(tag) {
  tags.value = tags.value.filter(item => item.fixed || item.path === tag.path)
  router.push(tag.path)
}

function closeAll() {
  tags.value = tags.value.filter(item => item.fixed)
  router.push('/admin/dashboard')
}
</script>

<style scoped>
.tags-view {
  position: relative;
  height: 40px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 14px;
  overflow-x: auto;
  background: #fff;
  border-bottom: 1px solid var(--pm-border);
}

.tags-view__item {
  height: 26px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 0 10px;
  white-space: nowrap;
  border: 1px solid var(--pm-border);
  border-radius: 4px;
  background: #fff;
  color: var(--pm-text-light);
  cursor: pointer;
  transition: all 0.16s ease;
}

.tags-view__item:hover {
  color: var(--pm-primary);
  border-color: rgba(22, 160, 133, 0.35);
  background: #F8FBFA;
}

.tags-view__item.active {
  color: #fff;
  background: var(--pm-primary);
  border-color: var(--pm-primary);
}

.tags-view__menu {
  position: fixed;
  z-index: 20;
  width: 104px;
  padding: 4px 0;
  background: #fff;
  border: 1px solid var(--pm-border);
  border-radius: 4px;
  box-shadow: var(--pm-shadow);
}

.tags-view__menu div {
  padding: 8px 12px;
  cursor: pointer;
}

.tags-view__menu div:hover {
  background: var(--pm-bg);
  color: var(--pm-primary);
}
</style>
