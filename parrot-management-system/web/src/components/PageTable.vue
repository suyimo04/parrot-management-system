<template>
  <div class="table-card page-table">
    <div v-if="$slots.toolbar" class="toolbar">
      <slot name="toolbar" />
    </div>
    <slot />
    <div class="page-table__pager">
      <el-pagination
        background
        layout="total, prev, pager, next, sizes"
        :current-page="current"
        :page-size="size"
        :total="total"
        @current-change="$emit('page-change', $event)"
        @size-change="$emit('size-change', $event)"
      />
    </div>
  </div>
</template>

<script setup>
defineProps({
  current: {
    type: Number,
    default: 1
  },
  size: {
    type: Number,
    default: 10
  },
  total: {
    type: Number,
    default: 0
  }
})

defineEmits(['page-change', 'size-change'])
</script>

<style scoped>
.page-table__pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

@media (max-width: 768px) {
  .page-table__pager {
    justify-content: flex-start;
    overflow-x: auto;
  }
}
</style>
