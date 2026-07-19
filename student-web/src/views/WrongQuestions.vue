<template>
  <section class="profile-card">
    <h1>错题本</h1>
    <el-table :data="rows">
      <el-table-column prop="paperName" label="试卷" />
      <el-table-column prop="score" label="得分" />
      <el-table-column prop="wrongCount" label="错题数" />
      <el-table-column prop="submitTime" label="提交时间" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="primary" link @click="$router.push(`/profile/exam-records/${row.id}`)">查看错题</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import request from '../api/request'

const rows = ref([])

onMounted(async () => {
  const res = await request.get('/wrong-questions/page', { params: { page: 1, limit: 20 } })
  rows.value = res.data.data.list.filter(item => Number(item.wrongCount || 0) > 0)
})
</script>
