<template>
  <section v-if="news" class="profile-card">
    <div v-if="news.picture" class="news-banner" :style="{ backgroundImage: `url(${news.picture})` }"></div>
    <h1>{{ news.title }}</h1>
    <p class="type-name">{{ news.addtime }}</p>
    <p>{{ news.summary }}</p>
    <div class="forum-content">{{ news.content }}</div>
    <el-button @click="$router.push('/news')">返回公告列表</el-button>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import request from '../api/request'

const route = useRoute()
const news = ref(null)

onMounted(async () => {
  const res = await request.get(`/news/${route.params.id}`)
  news.value = res.data.data
})
</script>
