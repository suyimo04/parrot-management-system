<template>
  <div class="portal-home">
    <section class="home-hero">
      <div class="home-hero__content">
        <span class="home-hero__label">鹦鹉养护 · 预约咨询 · 园区展示</span>
        <h1>青羽鹦鹉园</h1>
        <p>记录每只鹦鹉的状态与故事，让客户能清楚了解园区信息，也让预约咨询流程更顺畅。</p>
        <div class="home-hero__actions">
          <el-button type="primary" size="large" @click="$router.push('/portal/parrot')">查看鹦鹉</el-button>
          <el-button size="large" @click="$router.push('/portal/notice')">查看公告</el-button>
        </div>
      </div>
      <div class="home-hero__visual" aria-hidden="true">
        <div class="bird-card bird-card--main">
          <span class="bird-mark">青羽</span>
          <strong>今日开放预约</strong>
          <p>认养咨询、互动体验、养护答疑</p>
        </div>
        <div class="bird-card bird-card--small">
          <span>健康记录</span>
          <strong>持续更新</strong>
        </div>
      </div>
    </section>

    <section class="home-section">
      <BaseCard title="公开鹦鹉推荐">
        <template #extra>
          <el-button text type="primary" @click="$router.push('/portal/parrot')">更多</el-button>
        </template>
        <div v-if="parrots.length" class="parrot-row">
          <article v-for="item in parrots" :key="item.id" class="parrot-mini" @click="$router.push(`/portal/parrot/${item.id}`)">
            <div class="parrot-mini__cover">
              <el-image class="parrot-mini__img" :src="item.image || defaultParrotImage" fit="cover">
                <template #error>
                  <div class="image-fallback">青羽</div>
                </template>
              </el-image>
            </div>
            <div class="parrot-mini__info">
              <h4>{{ item.name }}</h4>
              <p>{{ item.speciesName || '暂无品种' }}</p>
              <span>{{ item.personality || '性格资料待补充' }}</span>
            </div>
          </article>
        </div>
        <EmptyBox v-else description="暂无公开鹦鹉" />
      </BaseCard>

      <BaseCard title="最新公告知识">
        <template #extra>
          <el-button text type="primary" @click="$router.push('/portal/notice')">更多</el-button>
        </template>
        <div v-if="notices.length" class="notice-list">
          <article v-for="item in notices" :key="item.id" class="notice-item" @click="$router.push(`/portal/notice/${item.id}`)">
            <StatusTag :text="item.type" />
            <div>
              <h4>{{ item.title }}</h4>
              <p>{{ item.publishTime || item.createTime || '暂无时间' }}</p>
            </div>
          </article>
        </div>
        <EmptyBox v-else description="暂无已发布公告" />
      </BaseCard>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import BaseCard from '../../components/BaseCard.vue'
import EmptyBox from '../../components/EmptyBox.vue'
import StatusTag from '../../components/StatusTag.vue'
import { getPublicNotices, getPublicParrots } from '../../api/public'

const defaultParrotImage = 'https://images.unsplash.com/photo-1552728089-571681646280?auto=format&fit=crop&w=800&q=80'
const parrots = ref([])
const notices = ref([])

async function loadHomeData() {
  const [parrotData, noticeData] = await Promise.all([
    getPublicParrots({ page: 1, size: 4 }),
    getPublicNotices({ page: 1, size: 5 })
  ])
  parrots.value = (parrotData || []).slice(0, 4)
  notices.value = (noticeData || []).slice(0, 5)
}

onMounted(loadHomeData)
</script>

<style scoped>
.portal-home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 28px 24px 32px;
}

.home-hero {
  position: relative;
  min-height: 360px;
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(320px, 0.95fr);
  align-items: center;
  gap: 34px;
  padding: 46px 52px;
  color: #fff;
  overflow: hidden;
  border-radius: 10px;
  box-shadow: var(--pm-shadow);
  background:
    linear-gradient(135deg, #153F3A 0%, #16A085 48%, #D9F3E9 100%);
}

.home-hero::before {
  position: absolute;
  inset: auto -120px -170px auto;
  width: 430px;
  height: 430px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.24);
  content: "";
}

.home-hero::after {
  position: absolute;
  top: -120px;
  right: 260px;
  width: 260px;
  height: 260px;
  border-radius: 50%;
  background: rgba(244, 180, 0, 0.2);
  content: "";
}

.home-hero__content,
.home-hero__visual {
  position: relative;
  z-index: 1;
}

.home-hero__label {
  display: inline-flex;
  align-items: center;
  margin-bottom: 14px;
  padding: 6px 12px;
  border: 1px solid rgba(255, 255, 255, 0.34);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.14);
  color: rgba(255, 255, 255, 0.92);
  font-size: 13px;
}

.home-hero h1 {
  margin: 0 0 12px;
  font-size: 44px;
  line-height: 1.2;
  letter-spacing: 0;
}

.home-hero p {
  max-width: 560px;
  margin: 0 0 24px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  line-height: 1.8;
}

.home-hero__actions {
  display: flex;
  gap: 12px;
}

.home-hero__actions :deep(.el-button:not(.el-button--primary)) {
  color: #fff;
  border-color: rgba(255, 255, 255, 0.7);
  background: rgba(255, 255, 255, 0.12);
}

.home-hero__visual {
  min-height: 250px;
}

.bird-card {
  position: absolute;
  color: var(--pm-text);
  border: 1px solid rgba(255, 255, 255, 0.55);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 24px 50px rgba(20, 59, 54, 0.18);
}

.bird-card--main {
  right: 24px;
  top: 10px;
  width: 300px;
  min-height: 180px;
  padding: 26px;
}

.bird-card--main::before {
  position: absolute;
  right: 24px;
  bottom: 26px;
  width: 88px;
  height: 88px;
  border-radius: 28px 28px 42px 42px;
  background:
    radial-gradient(circle at 62% 34%, #24323F 0 4px, transparent 5px),
    radial-gradient(circle at 62% 30%, #F4B400 0 18px, transparent 19px),
    linear-gradient(135deg, #16A085, #2ECC71);
  transform: rotate(-7deg);
  content: "";
}

.bird-card--main::after {
  position: absolute;
  right: 92px;
  bottom: 72px;
  width: 42px;
  height: 54px;
  border-radius: 60% 20% 60% 30%;
  background: rgba(255, 255, 255, 0.58);
  transform: rotate(-22deg);
  content: "";
}

.bird-card--small {
  left: 34px;
  bottom: 8px;
  width: 180px;
  padding: 18px 20px;
}

.bird-mark {
  display: inline-flex;
  margin-bottom: 32px;
  padding: 5px 9px;
  border-radius: 999px;
  background: rgba(22, 160, 133, 0.12);
  color: var(--pm-primary);
  font-size: 12px;
  font-weight: 700;
}

.bird-card strong {
  display: block;
  font-size: 22px;
}

.bird-card--small strong {
  margin-top: 6px;
  color: var(--pm-primary);
  font-size: 18px;
}

.bird-card p,
.bird-card span {
  margin: 8px 0 0;
  color: var(--pm-text-light);
  font-size: 13px;
}

.home-section {
  display: grid;
  grid-template-columns: 1.25fr 1fr;
  gap: 16px;
  margin-top: 16px;
}

.parrot-row {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.parrot-mini {
  display: flex;
  gap: 12px;
  min-width: 0;
  padding: 10px;
  cursor: pointer;
  border: 1px solid var(--pm-border);
  border-radius: var(--pm-card-radius);
  transition: border-color 0.16s ease, box-shadow 0.16s ease, transform 0.16s ease;
}

.parrot-mini:hover,
.notice-item:hover {
  transform: translateY(-1px);
  border-color: rgba(22, 160, 133, 0.35);
  box-shadow: 0 8px 18px rgba(36, 50, 63, 0.07);
}

.parrot-mini__cover,
.parrot-mini__img {
  width: 78px;
  height: 78px;
  flex: none;
  border-radius: var(--pm-card-radius);
}

.parrot-mini__cover {
  overflow: hidden;
  background: #EAF7F3;
}

.parrot-mini__img {
  display: block;
}

.image-fallback {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--pm-primary);
  background:
    linear-gradient(135deg, rgba(22, 160, 133, 0.12), rgba(46, 204, 113, 0.18));
  font-weight: 700;
}

.parrot-mini__info {
  min-width: 0;
}

.parrot-mini h4,
.notice-item h4 {
  margin: 0 0 6px;
  font-size: 15px;
}

.parrot-mini p,
.parrot-mini span,
.notice-item p {
  margin: 0;
  color: var(--pm-text-light);
  line-height: 1.6;
}

.parrot-mini span {
  display: -webkit-box;
  overflow: hidden;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.notice-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 10px;
  cursor: pointer;
  border: 1px solid var(--pm-border);
  border-radius: var(--pm-card-radius);
  transition: border-color 0.16s ease, box-shadow 0.16s ease, transform 0.16s ease;
}

@media (max-width: 900px) {
  .home-hero {
    grid-template-columns: 1fr;
  }

  .home-hero__visual {
    min-height: 190px;
  }

  .bird-card--main {
    left: 0;
    right: auto;
    top: 0;
  }

  .bird-card--small {
    right: 0;
    left: auto;
  }

  .home-section,
  .parrot-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .portal-home {
    padding: 12px;
  }

  .home-hero {
    padding: 24px;
  }

  .home-hero h1 {
    font-size: 34px;
  }

  .home-hero__actions {
    flex-wrap: wrap;
  }

  .home-hero__visual {
    display: none;
  }
}
</style>
