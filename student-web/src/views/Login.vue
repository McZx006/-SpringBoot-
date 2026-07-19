<template>
  <div class="auth-card">
    <h1>账号登录</h1>
    <el-form>
      <el-form-item label="账号">
        <el-input v-model="form.username" placeholder="请输入账号" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="form.password" type="password" placeholder="请输入密码" />
      </el-form-item>
      <el-button type="primary" class="full-btn" @click="login">登录</el-button>
    </el-form>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '../api/request'
import { STUDENT_TOKEN_KEY } from '../constants/app'

const router = useRouter()
const route = useRoute()
const form = reactive({ username: 'student', password: '123456', role: 'student' })

async function login() {
  const res = await request.post('/auth/login', form)
  localStorage.setItem(STUDENT_TOKEN_KEY, res.data.data.token)
  window.dispatchEvent(new Event('student-auth-change'))
  router.push(String(route.query.redirect || '/'))
}
</script>
