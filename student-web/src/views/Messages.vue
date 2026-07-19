<template>
  <section class="profile-card">
    <h1>留言反馈</h1>
    <div class="message-form">
      <el-input v-model="content" type="textarea" placeholder="请输入留言内容" />
      <el-button type="primary" @click="submit">提交留言</el-button>
    </div>
    <el-table :data="rows">
      <el-table-column prop="content" label="留言内容" />
      <el-table-column prop="reply" label="管理员回复" />
      <el-table-column prop="statusText" label="状态" width="120" />
      <el-table-column prop="replyTime" label="回复时间" />
      <el-table-column prop="addtime" label="留言时间" />
    </el-table>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../api/request'

const content = ref('')
const rows = ref([])

async function loadRows() {
  const res = await request.get('/messages/page', { params: { page: 1, limit: 20 } })
  rows.value = res.data.data.list.map(item => ({
    ...item,
    reply: item.reply || '暂无回复',
    statusText: item.status === 1 ? '已回复' : '待处理'
  }))
}

async function submit() {
  if (!content.value.trim()) {
    ElMessage.warning('请输入留言内容')
    return
  }
  await request.post('/messages/save', { content: content.value })
  content.value = ''
  ElMessage.success('提交成功')
  await loadRows()
}

onMounted(loadRows)
</script>
