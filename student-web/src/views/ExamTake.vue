<template>
  <section v-if="loading" class="profile-card">
    <h1>正在加载试卷...</h1>
  </section>
  <section v-else-if="loadError" class="profile-card">
    <h1>试卷加载失败</h1>
    <p class="empty-text">{{ loadError }}</p>
    <div class="exam-actions">
      <el-button type="primary" @click="loadExam">重新加载</el-button>
      <el-button @click="$router.push('/exams')">返回试卷列表</el-button>
    </div>
  </section>
  <section v-else-if="paper" class="exam-layout">
    <aside class="question-nav">
      <h3>{{ paper.name }}</h3>
      <p>总分：{{ paper.totalScore }} 分</p>
      <p :class="{ 'time-warning': remainingSeconds <= 300 }">剩余时间：{{ timeText }}</p>
      <div class="question-buttons">
        <button
          v-for="(question, index) in questions"
          :key="question.id"
          :class="{ active: currentIndex === index, answered: hasAnswer(question.id) }"
          @click="currentIndex = index"
        >
          {{ index + 1 }}
        </button>
      </div>
      <el-button type="primary" class="full-btn" @click="submitExam">提交试卷</el-button>
    </aside>

    <main class="question-panel" v-if="currentQuestion">
      <div class="question-title">
        <span>第 {{ currentIndex + 1 }} 题</span>
        <strong>{{ currentQuestion.score }} 分</strong>
      </div>
      <h2>{{ currentQuestion.questionName }}</h2>

      <el-radio-group
        v-if="currentQuestion.questionType === 'single' || currentQuestion.questionType === 'judge'"
        v-model="answers[currentQuestion.id]"
      >
        <el-radio v-for="option in parseOptions(currentQuestion.optionsJson)" :key="option.key" :label="option.key">
          {{ option.key }}. {{ option.value }}
        </el-radio>
      </el-radio-group>

      <el-checkbox-group
        v-if="currentQuestion.questionType === 'multiple'"
        v-model="answers[currentQuestion.id]"
      >
        <el-checkbox v-for="option in parseOptions(currentQuestion.optionsJson)" :key="option.key" :label="option.key">
          {{ option.key }}. {{ option.value }}
        </el-checkbox>
      </el-checkbox-group>

      <el-input
        v-if="currentQuestion.questionType === 'fill'"
        v-model="answers[currentQuestion.id]"
        placeholder="请输入答案"
      />

      <div class="exam-actions">
        <el-button :disabled="currentIndex === 0" @click="currentIndex--">上一题</el-button>
        <el-button :disabled="currentIndex === questions.length - 1" type="primary" @click="currentIndex++">下一题</el-button>
      </div>
    </main>
  </section>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../api/request'

const route = useRoute()
const router = useRouter()
const paper = ref(null)
const questions = ref([])
const currentIndex = ref(0)
const answers = reactive({})
const remainingSeconds = ref(0)
const submitted = ref(false)
const loading = ref(false)
const loadError = ref('')
let timer = null

const currentQuestion = computed(() => questions.value[currentIndex.value])
const timeText = computed(() => {
  const minutes = String(Math.floor(remainingSeconds.value / 60)).padStart(2, '0')
  const seconds = String(remainingSeconds.value % 60).padStart(2, '0')
  return `${minutes}:${seconds}`
})

function parseOptions(optionsJson) {
  try {
    return JSON.parse(optionsJson || '[]')
  } catch (error) {
    return []
  }
}

function hasAnswer(questionId) {
  const answer = answers[questionId]
  return Array.isArray(answer) ? answer.length > 0 : !!answer
}

function normalizedAnswer(question) {
  const answer = answers[question.id]
  if (Array.isArray(answer)) return answer.join(',')
  return answer || ''
}

async function submitExam() {
  await ElMessageBox.confirm('确认提交试卷吗？提交后不能修改。', '提交确认', { type: 'warning' })
  await doSubmit()
}

async function doSubmit() {
  if (submitted.value) {
    return
  }
  submitted.value = true
  clearTimer()
  try {
    const payload = {
      paperId: Number(route.params.paperId),
      answers: questions.value.map(question => ({
        questionId: question.id,
        answer: normalizedAnswer(question)
      }))
    }
    const res = await request.post('/exam/submit', payload)
    sessionStorage.setItem('examResult', JSON.stringify(res.data.data))
    router.push(`/exam/result/${res.data.data.recordId}`)
  } catch (error) {
    submitted.value = false
    startTimer()
    throw error
  }
}

function startTimer() {
  clearTimer()
  timer = window.setInterval(async () => {
    if (remainingSeconds.value > 0) {
      remainingSeconds.value -= 1
      return
    }
    clearTimer()
    ElMessage.warning('考试时间已到，系统将自动交卷')
    await doSubmit()
  }, 1000)
}

function clearTimer() {
  if (timer) {
    window.clearInterval(timer)
    timer = null
  }
}

async function loadExam() {
  loading.value = true
  loadError.value = ''
  clearTimer()
  try {
    const res = await request.get(`/exam/start/${route.params.paperId}`)
    paper.value = res.data.data.paper
    questions.value = res.data.data.questions || []
    if (!questions.value.length) {
      loadError.value = '当前试卷还没有配置试题，请联系管理员。'
      return
    }
    remainingSeconds.value = Number(paper.value.duration || 0) * 60
    questions.value.forEach(question => {
      answers[question.id] = question.questionType === 'multiple' ? [] : ''
    })
    startTimer()
  } catch (error) {
    if (!error.response || error.response.status !== 401) {
      loadError.value = (error.response && error.response.data && error.response.data.msg) || '试卷加载失败，请稍后重试。'
    }
  } finally {
    loading.value = false
  }
}

onMounted(loadExam)

onBeforeUnmount(() => {
  clearTimer()
})
</script>
