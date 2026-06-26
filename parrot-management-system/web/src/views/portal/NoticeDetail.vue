<template>
  <div class="page-wrap portal-page">
    <BaseCard title="公告详情">
      <EmptyBox description="后续接入公告详情接口后展示标题、封面和正文" />
    </BaseCard>
  </div>
</template>

<script setup>
import BaseCard from '../../components/BaseCard.vue'
import EmptyBox from '../../components/EmptyBox.vue'
</script>

<style scoped>
.portal-page {
  max-width: 900px;
  margin: 0 auto;
}
</style>
