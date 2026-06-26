<template>
  <el-tag :type="tagType" effect="light" round>
    {{ text || '未知' }}
  </el-tag>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  text: {
    type: String,
    default: ''
  }
})

const tagType = computed(() => {
  const value = props.text
  if (['成功', '已发布', '正常', '已确认', '已完成', '启用'].includes(value)) return 'success'
  if (['待处理', '观察中', '未发布'].includes(value)) return 'warning'
  if (['失败', '治疗中', '已驳回', '已停用', '禁用'].includes(value)) return 'danger'
  if (['已取消', '已预约'].includes(value)) return 'info'
  return 'primary'
})
</script>
