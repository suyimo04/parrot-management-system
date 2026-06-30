<template>
  <div class="page-wrap portal-page">
    <div v-if="loading" class="app-empty">正在加载...</div>
    <BaseCard v-else-if="detail.id">
      <article class="notice-detail">
        <div class="notice-detail__head">
          <StatusTag :text="detail.type" />
          <h1>{{ detail.title }}</h1>
          <p>{{ detail.publishTime || detail.createTime || '暂无时间' }}</p>
        </div>
        <el-image v-if="detail.coverImage" class="notice-detail__cover" :src="detail.coverImage" fit="cover" />
        <div class="notice-detail__content">{{ detail.content }}</div>
      </article>
    </BaseCard>
    <BaseCard v-else>
      <EmptyBox description="公告不存在或暂未发布" />
    </BaseCard>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import BaseCard from '../../components/BaseCard.vue'
import EmptyBox from '../../components/EmptyBox.vue'
import StatusTag from '../../components/StatusTag.vue'
import { getPublicNotice } from '../../api/public'

const route = useRoute()
const loading = ref(false)
const detail = ref({})

async function loadDetail() {
  loading.value = true
  try {
    detail.value = await getPublicNotice(route.params.id)
  } finally {
    loading.value = false
  }
}

onMounted(loadDetail)
</script>

<style scoped>
.portal-page {
  max-width: 900px;
  margin: 0 auto;
}

.notice-detail__head {
  text-align: center;
}

.notice-detail h1 {
  margin: 14px 0 10px;
  font-size: 28px;
  line-height: 1.4;
}

.notice-detail p {
  margin: 0;
  color: var(--pm-text-light);
}

.notice-detail__cover {
  width: 100%;
  height: 320px;
  margin-top: 18px;
  border-radius: 8px;
}

.notice-detail__content {
  margin-top: 22px;
  color: var(--pm-text);
  font-size: 15px;
  line-height: 2;
  white-space: pre-wrap;
}

@media (max-width: 640px) {
  .notice-detail h1 {
    font-size: 22px;
  }

  .notice-detail__cover {
    height: 220px;
  }
}
</style>
