<template>
  <section>
    <h1>公告信息</h1>
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索公告标题" />
      <el-button type="primary" @click="loadRows">搜索</el-button>
      <el-button @click="reset">重置</el-button>
    </div>
    <div class="notice-list">
      <div class="notice-item" v-for="item in rows" :key="item.id" @click="$router.push(`/news/${item.id}`)">
        <strong>{{ item.title }}</strong>
        <span>{{ item.addtime }}</span>
        <span>{{ item.summary }}</span>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import request from '../api/request'

const keyword = ref('')
const rows = ref([])

async function loadRows() {
  const res = await request.get('/news/page', { params: { page: 1, limit: 20, keyword: keyword.value } })
  rows.value = res.data.data.list
}

function reset() {
  keyword.value = ''
  loadRows()
}

onMounted(loadRows)
</script>
