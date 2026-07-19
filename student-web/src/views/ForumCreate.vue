<template>
  <section class="profile-card">
    <h1>发布帖子</h1>
    <el-form label-width="80px">
      <el-form-item label="标题">
        <el-input v-model="form.title" maxlength="100" show-word-limit />
      </el-form-item>
      <el-form-item label="内容">
        <el-input v-model="form.content" type="textarea" :rows="8" maxlength="2000" show-word-limit />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">发布</el-button>
        <el-button @click="$router.push('/forum')">返回</el-button>
      </el-form-item>
    </el-form>
  </section>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../api/request'

const router = useRouter()
const form = reactive({ title: '', content: '' })

async function submit() {
  if (!form.title.trim()) {
    ElMessage.warning('请输入帖子标题')
    return
  }
  if (!form.content.trim()) {
    ElMessage.warning('请输入帖子内容')
    return
  }
  await request.post('/forum/save', form)
  ElMessage.success('发布成功')
  router.push('/forum')
}
</script>

