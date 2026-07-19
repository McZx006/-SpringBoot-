<template>
  <section v-if="post" class="profile-card">
    <div class="section-header">
      <h1>{{ post.title }}</h1>
      <el-button
        v-if="canDelete"
        type="danger"
        plain
        @click="removePost"
      >
        删除帖子
      </el-button>
    </div>
    <div class="meta-line">
      <span>作者 {{ post.username || '学员' }}</span>
      <span>浏览 {{ post.viewCount || 0 }}</span>
      <span>{{ post.addtime }}</span>
    </div>
    <p class="forum-content">{{ post.content }}</p>

    <h2>评论</h2>
    <div class="message-form">
      <el-input v-model="comment" type="textarea" placeholder="请输入评论" />
      <el-button type="primary" @click="submitComment">发表评论</el-button>
    </div>
    <div class="comment-list">
      <div v-for="item in comments" :key="item.id" class="comment-item">
        <strong>{{ item.username || '学员' }}</strong>
        <p>{{ item.content }}</p>
        <span>{{ item.addtime }}</span>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../api/request'
import { STUDENT_TOKEN_KEY } from '../constants/app'

const route = useRoute()
const router = useRouter()
const post = ref(null)
const comments = ref([])
const comment = ref('')
const currentUser = ref({})

const canDelete = computed(() => {
  if (!post.value || !currentUser.value.userId) return false
  return currentUser.value.role === 'admin' || Number(post.value.userId) === Number(currentUser.value.userId)
})

async function loadDetail() {
  const [postRes, commentRes] = await Promise.all([
    request.get(`/forum/${route.params.id}`),
    request.get(`/forum/comments/${route.params.id}`)
  ])
  post.value = postRes.data.data
  comments.value = commentRes.data.data
  if (localStorage.getItem(STUDENT_TOKEN_KEY)) {
    const userRes = await request.get('/auth/info')
    currentUser.value = userRes.data.data || {}
  } else {
    currentUser.value = {}
  }
}

async function submitComment() {
  if (!localStorage.getItem(STUDENT_TOKEN_KEY)) {
    ElMessage.warning('请先登录后再发表评论')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  if (!comment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  await request.post('/forum/comment', { forumId: Number(route.params.id), content: comment.value })
  comment.value = ''
  await loadDetail()
}

async function removePost() {
  await ElMessageBox.confirm('确认删除这篇帖子吗？删除后无法恢复。', '删除确认', { type: 'warning' })
  await request.delete('/forum/delete', { data: [post.value.id] })
  ElMessage.success('删除成功')
  window.history.back()
}

onMounted(loadDetail)
</script>
