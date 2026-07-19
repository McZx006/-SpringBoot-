<template>
  <section>
    <el-carousel v-if="banners.length" class="hero-carousel" height="280px">
      <el-carousel-item v-for="item in banners" :key="item.id">
        <div class="hero hero-slide" :style="{ backgroundImage: `linear-gradient(rgba(37,99,235,.48), rgba(22,163,74,.35)), url(${item.value})` }">
          <h1>线上教学平台</h1>
          <p>{{ item.remark || '学习资料、在线考试、交流互动、留言反馈和公告通知一站式完成。' }}</p>
        </div>
      </el-carousel-item>
    </el-carousel>
    <div v-else class="hero">
      <h1>线上教学平台</h1>
      <p>学习资料、在线考试、交流互动、留言反馈和公告通知一站式完成。</p>
    </div>

    <div class="quick-grid">
      <router-link class="quick-card" to="/resources">学习资料</router-link>
      <router-link class="quick-card" to="/exams">在线考试</router-link>
      <router-link class="quick-card" to="/forum">交流论坛</router-link>
      <router-link class="quick-card" to="/messages">留言反馈</router-link>
    </div>

    <h2>推荐学习资料</h2>
    <div class="resource-grid">
      <article class="resource-card" v-for="item in resources" :key="item.id" @click="$router.push(`/resources/${item.id}`)">
        <img class="cover" :src="resourceCover(item)" :alt="item.title" />
        <h3>{{ item.title }}</h3>
        <p>{{ item.summary }}</p>
      </article>
    </div>

    <h2>最新公告</h2>
    <div class="notice-list">
      <div class="notice-item" v-for="item in news" :key="item.id" @click="$router.push(`/news/${item.id}`)">
        <strong>{{ item.title }}</strong>
        <span>{{ item.summary }}</span>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import request from '../api/request'
import { resourceCover } from '../utils/resourceCover'

const resources = ref([])
const news = ref([])
const banners = ref([])

onMounted(async () => {
  const [resourceRes, newsRes, bannerRes] = await Promise.all([
    request.get('/resources/page', { params: { page: 1, limit: 4 } }),
    request.get('/news/page', { params: { page: 1, limit: 5 } }),
    request.get('/banners/list')
  ])
  resources.value = resourceRes.data.data.list
  news.value = newsRes.data.data.list
  banners.value = bannerRes.data.data || []
})
</script>
