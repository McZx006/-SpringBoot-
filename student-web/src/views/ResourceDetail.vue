<template>
  <section v-if="resource">
    <div class="detail-card">
      <img class="detail-cover" :src="resourceCover(resource)" :alt="resource.title" />
      <div class="detail-body">
        <p class="type-name">{{ resource.typeName }}</p>
        <h1>{{ resource.title }}</h1>
        <p>{{ resource.summary }}</p>
        <div class="meta-line">
          <span>浏览 {{ resource.viewCount || 0 }}</span>
          <span>下载 {{ resource.downloadCount || 0 }}</span>
          <span>上传人 {{ resource.author || '管理员' }}</span>
        </div>
        <div class="detail-actions">
          <el-button v-if="previewUrl" type="success" @click="preview">在线预览</el-button>
          <el-button type="primary" @click="download">下载资料</el-button>
          <el-button type="warning" @click="storeup">收藏资料</el-button>
          <el-button @click="$router.push('/resources')">返回列表</el-button>
        </div>
      </div>
    </div>

    <section class="notice-list resource-comments">
      <div class="section-header">
        <h2>资料评论</h2>
        <span>{{ comments.length }} 条</span>
      </div>
      <div class="comment-form">
        <el-input
          v-model="commentText"
          type="textarea"
          :rows="3"
          maxlength="500"
          show-word-limit
          placeholder="请输入你对这份资料的学习心得或问题"
        />
        <div class="comment-actions">
          <el-button type="primary" @click="submitComment">发表评论</el-button>
        </div>
      </div>
      <div v-if="comments.length" class="comment-list">
        <article v-for="item in comments" :key="item.id" class="comment-item">
          <div class="comment-head">
            <strong>{{ item.username || '匿名用户' }}</strong>
            <span>{{ item.addtime }}</span>
          </div>
          <p>{{ item.content }}</p>
        </article>
      </div>
      <p v-else class="empty-text">还没有评论，来发第一条吧。</p>
    </section>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../api/request'
import { resourceCover } from '../utils/resourceCover'
import { STUDENT_TOKEN_KEY } from '../constants/app'

const route = useRoute()
const router = useRouter()
const resource = ref(null)
const comments = ref([])
const commentText = ref('')
const previewUrl = ref('')

function preview() {
  if (!previewUrl.value) {
    ElMessage.warning('当前资料暂不支持预览')
    return
  }
  window.open(previewUrl.value, '_blank')
}

async function download() {
  const res = await request.get(`/resources/download/${route.params.id}`)
  window.open(res.data.data.downloadUrl || res.data.data.fileUrl, '_blank')
  if (resource.value) {
    resource.value.downloadCount = (resource.value.downloadCount || 0) + 1
  }
}

async function storeup() {
  if (!localStorage.getItem(STUDENT_TOKEN_KEY)) {
    ElMessage.warning('请先登录后再收藏资料')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  await request.post('/storeup/save', {
    refId: resource.value.id,
    type: 'resource',
    title: resource.value.title
  })
  ElMessage.success('收藏成功')
}

async function loadDetail() {
  const res = await request.get(`/resources/detail/${route.params.id}`)
  resource.value = res.data.data
  previewUrl.value = resource.value.videoUrl || resource.value.fileUrl || ''
}

async function loadComments() {
  const res = await request.get(`/resources/comments/${route.params.id}`)
  comments.value = res.data.data || []
}

async function submitComment() {
  if (!localStorage.getItem(STUDENT_TOKEN_KEY)) {
    ElMessage.warning('请先登录后再发表评论')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  if (!commentText.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  await request.post('/resources/comment', {
    resourceId: route.params.id,
    content: commentText.value
  })
  commentText.value = ''
  ElMessage.success('评论成功')
  await loadComments()
}

onMounted(async () => {
  await loadDetail()
  await loadComments()
})
</script>
