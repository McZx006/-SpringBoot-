<template>
  <section>
    <div class="section-header">
      <h1>交流论坛</h1>
      <el-button type="primary" @click="$router.push('/forum/create')">发布帖子</el-button>
    </div>
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索帖子" />
      <el-button type="primary" @click="loadRows">搜索</el-button>
      <el-button @click="reset">重置</el-button>
    </div>
    <div class="forum-list">
      <article v-for="item in rows" :key="item.id" class="forum-item" @click="$router.push(`/forum/${item.id}`)">
        <h2>{{ item.title }}</h2>
        <p>{{ item.content }}</p>
        <div class="meta-line">
          <span>作者 {{ item.username || '学员' }}</span>
          <span>浏览 {{ item.viewCount || 0 }}</span>
          <span>{{ item.addtime }}</span>
        </div>
      </article>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import request from '../api/request'

const keyword = ref('')
const rows = ref([])

async function loadRows() {
  const res = await request.get('/forum/page', { params: { page: 1, limit: 20, keyword: keyword.value, publicOnly: true } })
  rows.value = res.data.data.list
}

function reset() {
  keyword.value = ''
  loadRows()
}

onMounted(loadRows)
</script>
