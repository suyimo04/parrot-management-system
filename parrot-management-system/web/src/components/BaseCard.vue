<template>
  <section class="base-card">
    <div v-if="title || $slots.extra" class="base-card__head">
      <h3 v-if="title">{{ title }}</h3>
      <div class="base-card__extra">
        <slot name="extra" />
      </div>
    </div>
    <slot />
  </section>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.base-card {
  padding: 18px;
  background: #fff;
  border: 1px solid var(--pm-border);
  border-radius: var(--pm-card-radius);
  box-shadow: var(--pm-shadow);
}

.base-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.base-card__head h3 {
  position: relative;
  margin: 0;
  padding-left: 10px;
  font-size: 16px;
  font-weight: 700;
  color: var(--pm-text);
}

/* 小卡片标题也带绿色标记，答辩演示时页面层次更清楚 */
.base-card__head h3::before {
  position: absolute;
  left: 0;
  top: 3px;
  width: 4px;
  height: 16px;
  border-radius: 4px;
  background: var(--pm-primary);
  content: "";
}

.base-card__extra {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
