<template>
  <section>
    <div class="filter-bar">
      <el-input v-model="query.keyword" placeholder="搜索资料名称" />
      <el-select v-model="query.typeId" clearable placeholder="资料类型">
        <el-option v-for="item in types" :key="item.id" :label="item.name" :value="item.id" />
      </el-select>
      <el-button type="primary" @click="loadResources">搜索</el-button>
      <el-button @click="reset">重置</el-button>
    </div>
    <div class="resource-grid">
      <article class="resource-card" v-for="item in resources" :key="item.id">
        <img class="cover" :src="resourceCover(item)" :alt="item.title" />
        <h3>{{ item.title }}</h3>
        <p class="type-name">{{ item.typeName || '未分类' }}</p>
        <p>{{ item.summary }}</p>
        <div class="meta-line">
          <span>浏览 {{ item.viewCount || 0 }}</span>
          <span>下载 {{ item.downloadCount || 0 }}</span>
        </div>
        <el-button type="primary" link @click="$router.push(`/resources/${item.id}`)">查看详情</el-button>
      </article>
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import request from '../api/request'
import { resourceCover } from '../utils/resourceCover'

const query = reactive({ page: 1, limit: 12, keyword: '', typeId: '' })
const resources = ref([])
const types = ref([])

async function loadResources() {
  const res = await request.get('/resources/page', { params: query })
  resources.value = res.data.data.list
}

function reset() {
  query.keyword = ''
  query.typeId = ''
  loadResources()
}

onMounted(async () => {
  const typeRes = await request.get('/resource-types/list')
  types.value = typeRes.data.data
  await loadResources()
})
</script>
