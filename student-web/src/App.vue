<template>
  <div>
    <header class="topbar">
      <div class="brand">线上教学平台</div>
      <nav>
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
        >
          {{ item.label }}
        </router-link>
      </nav>
      <button v-if="isLoggedIn" class="login-link logout-btn" @click="logout">退出登录</button>
      <router-link v-else class="login-link" to="/login">登录</router-link>
    </header>
    <main class="page">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { onMounted, onBeforeUnmount, ref } from 'vue'
import { useRouter } from 'vue-router'
import { STUDENT_NAV_ITEMS as navItems, STUDENT_TOKEN_KEY } from './constants/app'

const router = useRouter()
const isLoggedIn = ref(false)

function syncLoginState() {
  isLoggedIn.value = !!localStorage.getItem(STUDENT_TOKEN_KEY)
}

function logout() {
  localStorage.removeItem(STUDENT_TOKEN_KEY)
  syncLoginState()
  router.push('/')
}

onMounted(() => {
  syncLoginState()
  window.addEventListener('storage', syncLoginState)
  window.addEventListener('student-auth-change', syncLoginState)
})

onBeforeUnmount(() => {
  window.removeEventListener('storage', syncLoginState)
  window.removeEventListener('student-auth-change', syncLoginState)
})
</script>
