<template>
  <section class="profile-card">
    <h1>考试记录</h1>
    <el-table v-loading="loading" :data="rows" empty-text="暂无考试记录，请先完成一次在线考试">
      <el-table-column prop="paperName" label="试卷" />
      <el-table-column prop="score" label="得分" />
      <el-table-column prop="correctCount" label="正确题数" />
      <el-table-column prop="wrongCount" label="错题数" />
      <el-table-column prop="submitTime" label="提交时间" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="primary" link @click="$router.push(`/profile/exam-records/${row.id}`)">查看详情</el-button>
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

onMounted(async () => {
  loading.value = true
  try {
    const res = await request.get('/examrecords/page', { params: { page: 1, limit: 20 } })
    rows.value = res.data.data.list
  } finally {
    loading.value = false
  }
})
</script>
