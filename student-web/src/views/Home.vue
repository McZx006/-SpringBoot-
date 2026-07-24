<template>
  <section>
    <el-carousel v-if="banners.length" class="hero-carousel" height="280px">
      <el-carousel-item v-for="item in banners" :key="item.id">
        <div class="hero hero-slide" :style="{ backgroundImage: `url(${item.value})` }">
          <div class="hero-content">
            <h1>线上教学平台</h1>
            <p>{{ item.remark || '学习资料、在线考试、交流互动、留言反馈和公告通知一站式完成。' }}</p>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
    <div v-else class="hero">
      <div class="hero-content">
        <h1>线上教学平台</h1>
        <p>学习资料、在线考试、交流互动、留言反馈和公告通知一站式完成。</p>
      </div>
    </div>

    <div class="quick-grid">
      <router-link class="quick-card" to="/resources">
        <div class="quick-card-icon blue">
          <BookOpen class="w-8 h-8" />
        </div>
        <div class="quick-card-text">学习资料</div>
        <div class="quick-card-count">{{ stats?.resourceCount || 0 }}</div>
      </router-link>
      <router-link class="quick-card" to="/exams">
        <div class="quick-card-icon green">
          <FileQuestion class="w-8 h-8" />
        </div>
        <div class="quick-card-text">在线考试</div>
        <div class="quick-card-count">{{ stats?.examCount || 0 }}</div>
      </router-link>
      <router-link class="quick-card" to="/forum">
        <div class="quick-card-icon orange">
          <MessageCircle class="w-8 h-8" />
        </div>
        <div class="quick-card-text">交流论坛</div>
        <div class="quick-card-count">{{ stats?.forumCount || 0 }}</div>
      </router-link>
      <router-link class="quick-card" to="/messages">
        <div class="quick-card-icon purple">
          <MessageSquare class="w-8 h-8" />
        </div>
        <div class="quick-card-text">留言反馈</div>
        <div class="quick-card-count">{{ stats?.userCount || 0 }}</div>
      </router-link>
    </div>

    <div class="section-header">
      <h2>推荐学习资料</h2>
      <router-link to="/resources">查看更多</router-link>
    </div>
    <div class="resource-grid" v-if="resources.length">
      <article
        class="resource-card"
        v-for="item in resources"
        :key="item.id"
        @click="$router.push(`/resources/${item.id}`)"
      >
        <img class="cover" :src="resourceCover(item)" :alt="item.title" />
        <h3>{{ item.title }}</h3>
        <span class="type-name">{{ item.typeName || '未分类' }}</span>
        <p>{{ item.summary }}</p>
        <div class="meta-line">
          <span><Eye class="w-3 h-3" /> {{ item.viewCount || 0 }}</span>
          <span><Download class="w-3 h-3" /> {{ item.downloadCount || 0 }}</span>
        </div>
      </article>
    </div>
    <div v-else class="skeleton-grid">
      <div class="skeleton-card" v-for="i in 4" :key="i">
        <div class="skeleton cover-skeleton"></div>
        <div class="skeleton title-skeleton"></div>
        <div class="skeleton type-skeleton"></div>
        <div class="skeleton desc-skeleton"></div>
        <div class="skeleton meta-skeleton"></div>
      </div>
    </div>

    <div class="section-header">
      <h2>最新公告</h2>
      <router-link to="/news">查看更多</router-link>
    </div>
    <div class="notice-list" v-if="news.length">
      <div
        class="notice-item"
        v-for="item in news"
        :key="item.id"
        @click="$router.push(`/news/${item.id}`)"
      >
        <strong>{{ item.title }}</strong>
        <span>{{ item.addtime }}</span>
        <span>{{ item.summary }}</span>
      </div>
    </div>
    <div v-else class="skeleton-notice">
      <div class="skeleton notice-skeleton" v-for="i in 5" :key="i"></div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { BookOpen, FileQuestion, MessageCircle, MessageSquare, Eye, Download, Users } from '@lucide/vue'
import request from '../api/request'
import { resourceCover } from '../utils/resourceCover'

const resources = ref([])
const news = ref([])
const banners = ref([])
const stats = ref(null)

onMounted(async () => {
  const [resourceRes, newsRes, bannerRes] = await Promise.all([
    request.get('/resources/page', { params: { page: 1, limit: 4 } }),
    request.get('/news/page', { params: { page: 1, limit: 5 } }),
    request.get('/banners/list')
  ])
  resources.value = resourceRes.data.data.list
  news.value = newsRes.data.data.list
  banners.value = bannerRes.data.data || []
  
  try {
    const statRes = await request.get('/stats/dashboard')
    stats.value = statRes.data.data
  } catch {
    stats.value = { resourceCount: 0, examCount: 0, forumCount: 0, userCount: 0 }
  }
})
</script>

<style scoped>
.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-lg);
  margin: var(--spacing-2xl) 0;
}

.skeleton-card {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.skeleton.cover-skeleton {
  height: 140px;
  border-radius: var(--radius-md);
}

.skeleton.title-skeleton {
  height: 20px;
  width: 80%;
  border-radius: 4px;
}

.skeleton.type-skeleton {
  height: 20px;
  width: 40%;
  border-radius: 4px;
}

.skeleton.desc-skeleton {
  height: 16px;
  width: 100%;
  border-radius: 4px;
}

.skeleton-notice {
  background: var(--color-bg-card);
  border: 1px solid var(--color-gray-100);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.skeleton.notice-skeleton {
  height: 40px;
  width: 100%;
  border-radius: 4px;
}

@media (max-width: 768px) {
  .skeleton-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .skeleton-grid {
    grid-template-columns: 1fr;
  }
}
</style>
