<template>
  <section class="profile-card">
    <div class="section-header">
      <h1>{{ detail.paperName || '答题详情' }}</h1>
      <el-button @click="$router.push('/profile/exam-records')">返回记录</el-button>
    </div>
    <p class="meta-text">
      得分 {{ detail.score || 0 }} / {{ detail.totalScore || 0 }}，
      正确 {{ detail.correctCount || 0 }} 题，错误 {{ detail.wrongCount || 0 }} 题
    </p>
    <div v-if="detail.items && detail.items.length" class="record-question-list">
      <article v-for="(item, index) in detail.items" :key="item.id" class="question-panel">
        <div class="question-title">
          <span>第 {{ index + 1 }} 题</span>
          <strong :class="item.correct ? 'answer-correct' : 'answer-wrong'">
            {{ item.correct ? '回答正确' : '回答错误' }}
          </strong>
        </div>
        <h3>{{ item.questionName }}</h3>
        <p>你的答案：{{ item.userAnswer || '未作答' }}</p>
        <p>正确答案：{{ item.answer || '无' }}</p>
        <p v-if="item.analysis">解析：{{ item.analysis }}</p>
      </article>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import request from '../api/request'

const route = useRoute()
const detail = ref({})

onMounted(async () => {
  const res = await request.get(`/examrecords/${route.params.id}`)
  detail.value = res.data.data
})
</script>
