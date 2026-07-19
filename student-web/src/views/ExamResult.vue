<template>
  <section class="result-card">
    <h1>考试结果</h1>
    <div class="score">{{ result.score || detail.score || 0 }}</div>
    <p>总分：{{ result.totalScore || detail.totalScore || 0 }}</p>
    <p>答对：{{ result.correctCount || detail.correctCount || 0 }} 题，答错：{{ result.wrongCount || detail.wrongCount || 0 }} 题</p>
    <div class="exam-actions">
      <el-button type="primary" @click="$router.push('/exams')">返回试卷列表</el-button>
      <el-button @click="$router.push(`/profile/exam-records/${recordId}`)">查看答题详情</el-button>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import request from '../api/request'

const route = useRoute()
const recordId = route.params.recordId
const result = JSON.parse(sessionStorage.getItem('examResult') || '{}')
const detail = ref({})

onMounted(async () => {
  if (!recordId) return
  const res = await request.get(`/examrecords/${recordId}`)
  detail.value = res.data.data
})
</script>
