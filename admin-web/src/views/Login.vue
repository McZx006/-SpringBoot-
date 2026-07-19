<template>
  <div class="login-card">
    <h1>后台登录</h1>
    <el-form>
      <el-form-item label="账号">
        <el-input v-model="form.username" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="form.password" type="password" />
      </el-form-item>
      <el-button type="primary" class="full-btn" @click="login">登录</el-button>
    </el-form>
  </div>
</template>

<script>
import request from '../api/request'

export default {
  data() {
    return {
      form: { username: 'admin', password: '123456', role: 'admin' }
    }
  },
  methods: {
    async login() {
      const res = await request.post('/auth/login', this.form)
      localStorage.setItem('adminToken', res.data.data.token)
      this.$router.push(this.$route.query.redirect || '/admin/dashboard')
    }
  }
}
</script>
