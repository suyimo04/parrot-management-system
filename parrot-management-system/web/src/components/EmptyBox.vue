<template>
  <div class="app-empty">
    <el-empty :description="description" />
  </div>
</template>

<script setup>
defineProps({
  description: {
    type: String,
    default: '暂无数据'
  }
})
</script>
