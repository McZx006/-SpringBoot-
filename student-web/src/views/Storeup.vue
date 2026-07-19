<template>
  <section class="profile-card">
    <h1>我的收藏</h1>
    <el-table v-loading="loading" :data="rows" empty-text="暂无收藏记录，请先在学习资料详情页点击收藏资料">
      <el-table-column prop="title" label="收藏名称" />
      <el-table-column prop="type" label="类型" />
      <el-table-column prop="addtime" label="收藏时间" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="danger" link @click="remove(row)">取消收藏</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import request from '../api/request'

const rows = ref([])
const loading = ref(false)

async function loadRows() {
  loading.value = true
  try {
    const res = await request.get('/storeup/page', { params: { page: 1, limit: 20 } })
    rows.value = res.data.data.list
  } finally {
    loading.value = false
  }
}

async function remove(row) {
  await request.delete('/storeup/cancel', { data: { refId: row.refId, type: row.type } })
  await loadRows()
}

onMounted(loadRows)
</script>
