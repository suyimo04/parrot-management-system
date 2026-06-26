<template>
  <div class="portal-home">
    <section class="home-hero">
      <div>
        <h1>青羽鹦鹉园</h1>
        <p>这里用于展示园区鹦鹉、发布养护知识，也方便客户在线提交预约咨询。</p>
        <div class="home-hero__actions">
          <el-button type="primary" size="large" @click="$router.push('/portal/parrot')">查看鹦鹉</el-button>
          <el-button size="large" @click="$router.push('/portal/notice')">查看公告</el-button>
        </div>
      </div>
    </section>
    <section class="home-section">
      <BaseCard title="公开鹦鹉推荐">
        <EmptyBox description="后续接入公开鹦鹉接口后展示推荐内容" />
      </BaseCard>
      <BaseCard title="公告知识入口">
        <EmptyBox description="后续接入公告接口后展示最新内容" />
      </BaseCard>
    </section>
  </div>
</template>

<script setup>
import BaseCard from '../../components/BaseCard.vue'
import EmptyBox from '../../components/EmptyBox.vue'
</script>

<style scoped>
.portal-home {
  max-width: 1180px;
  margin: 0 auto;
  padding: 24px;
}

.home-hero {
  min-height: 280px;
  display: flex;
  align-items: center;
  padding: 36px;
  color: #fff;
  border-radius: 12px;
  background:
    linear-gradient(90deg, rgba(36, 50, 63, 0.84), rgba(36, 50, 63, 0.34)),
    url('https://images.unsplash.com/photo-1552728089-571681646280?auto=format&fit=crop&w=1500&q=80') center/cover;
}

.home-hero h1 {
  margin: 0 0 12px;
  font-size: 36px;
}

.home-hero p {
  max-width: 560px;
  margin: 0 0 22px;
  line-height: 1.8;
}

.home-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-top: 16px;
}

@media (max-width: 768px) {
  .portal-home {
    padding: 12px;
  }

  .home-hero {
    padding: 24px;
  }

  .home-section {
    grid-template-columns: 1fr;
  }
}
</style>
