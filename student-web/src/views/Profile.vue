<template>
  <section class="profile-layout">
    <aside class="profile-menu">
      <router-link to="/profile">个人资料</router-link>
      <router-link to="/profile/storeup">我的收藏</router-link>
      <router-link to="/profile/exam-records">考试记录</router-link>
      <router-link to="/profile/wrong-questions">错题本</router-link>
      <router-link to="/messages">留言反馈</router-link>
    </aside>
    <main class="profile-card">
      <h1>个人中心</h1>
      <div class="profile-grid">
        <label>
          <span>账号</span>
          <input :value="form.username || ''" disabled />
        </label>
        <label>
          <span>角色</span>
          <input :value="form.role || ''" disabled />
        </label>
        <label>
          <span>学号</span>
          <input v-model="form.xuehao" />
        </label>
        <label>
          <span>姓名</span>
          <input v-model="form.name" />
        </label>
        <label>
          <span>性别</span>
          <select v-model="form.gender">
            <option value="男">男</option>
            <option value="女">女</option>
          </select>
        </label>
        <label>
          <span>手机号</span>
          <input v-model="form.phone" />
        </label>
        <label>
          <span>邮箱</span>
          <input v-model="form.email" />
        </label>
        <label>
          <span>头像地址</span>
          <input v-model="form.avatar" />
        </label>
        <label class="full-row">
          <span>新密码</span>
          <input v-model="form.password" type="password" placeholder="不修改可留空" />
        </label>
      </div>
      <div class="profile-actions">
        <button class="primary-btn" @click="save">保存资料</button>
      </div>
    </main>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import request from '../api/request'

const form = ref({
  username: '',
  role: '',
  xuehao: '',
  name: '',
  gender: '男',
  phone: '',
  email: '',
  avatar: '',
  password: ''
})

onMounted(async () => {
  const res = await request.get('/auth/profile')
  form.value = {
    ...form.value,
    ...res.data.data,
    password: ''
  }
})

async function save() {
  await request.put('/auth/profile', form.value)
  form.value.password = ''
  window.alert('资料已更新')
}
</script>
