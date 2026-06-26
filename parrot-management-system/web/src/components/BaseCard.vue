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
  padding: 16px;
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
  margin: 0;
  font-size: 16px;
}

.base-card__extra {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
