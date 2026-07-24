<template>
  <div>
    <header class="topbar" :class="{ scrolled: isScrolled }">
      <div class="brand">
        <div class="brand-icon">
          <GraduationCap class="w-6 h-6" />
        </div>
        <span>线上教学平台</span>
      </div>
      <nav>
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
        >
          <component :is="item.icon" class="w-4 h-4" />
          {{ item.label }}
        </router-link>
      </nav>
      <div class="user-menu">
        <button
          v-if="isLoggedIn && userInfo"
          class="user-avatar"
          @click="toggleUserMenu"
        >
          <User class="w-5 h-5" />
        </button>
        <router-link v-else class="login-link" to="/login">
          <LogIn class="w-4 h-4" />
          登录
        </router-link>
        <button v-if="isLoggedIn" class="logout-btn" @click="logout">
          <LogOut class="w-4 h-4" />
        </button>
      </div>
      <div v-if="showUserMenu" class="user-dropdown">
        <router-link to="/profile">
          <User class="w-4 h-4" />
          个人中心
        </router-link>
        <router-link to="/profile/storeup">
          <Bookmark class="w-4 h-4" />
          我的收藏
        </router-link>
        <router-link to="/profile/exam-records">
          <FileText class="w-4 h-4" />
          考试记录
        </router-link>
        <router-link to="/messages">
          <MessageSquare class="w-4 h-4" />
          留言反馈
        </router-link>
        <div class="dropdown-divider"></div>
        <button class="logout-btn" @click="logout">
          <LogOut class="w-4 h-4" />
          退出登录
        </button>
      </div>
    </header>
    <main class="page">
      <router-view />
    </main>
    <button
      class="scroll-top-btn"
      :class="{ visible: showScrollTop }"
      @click="scrollToTop"
    >
      <ArrowUp class="w-5 h-5" />
    </button>
  </div>
</template>

<script setup>
import { onMounted, onBeforeUnmount, ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import {
  GraduationCap,
  LogIn,
  LogOut,
  User,
  Bookmark,
  FileText,
  MessageSquare,
  ArrowUp,
  Home,
  BookOpen,
  FileQuestion,
  MessageCircle,
  Bell
} from '@lucide/vue'
import { STUDENT_NAV_ITEMS as navItems, STUDENT_TOKEN_KEY } from './constants/app'

const router = useRouter()
const isLoggedIn = ref(false)
const isScrolled = ref(false)
const showScrollTop = ref(false)
const showUserMenu = ref(false)
const userInfo = reactive({})

const navItemsWithIcons = navItems.map(item => {
  const iconMap = {
    '首页': Home,
    '学习资料': BookOpen,
    '在线考试': FileQuestion,
    '交流论坛': MessageCircle,
    '公告信息': Bell
  }
  return {
    ...item,
    icon: iconMap[item.label] || Home
  }
})

function syncLoginState() {
  isLoggedIn.value = !!localStorage.getItem(STUDENT_TOKEN_KEY)
}

function logout() {
  localStorage.removeItem(STUDENT_TOKEN_KEY)
  showUserMenu.value = false
  syncLoginState()
  router.push('/')
}

function toggleUserMenu() {
  showUserMenu.value = !showUserMenu.value
}

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function handleScroll() {
  isScrolled.value = window.scrollY > 20
  showScrollTop.value = window.scrollY > 500
}

function handleClickOutside(event) {
  const target = event.target
  if (!target.closest('.user-menu') && !target.closest('.user-dropdown')) {
    showUserMenu.value = false
  }
}

onMounted(() => {
  syncLoginState()
  window.addEventListener('storage', syncLoginState)
  window.addEventListener('student-auth-change', syncLoginState)
  window.addEventListener('scroll', handleScroll)
  window.addEventListener('click', handleClickOutside)
})

onBeforeUnmount(() => {
  window.removeEventListener('storage', syncLoginState)
  window.removeEventListener('student-auth-change', syncLoginState)
  window.removeEventListener('scroll', handleScroll)
  window.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
nav a {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 0;
  transition: color var(--transition-fast);
}

.user-dropdown {
  position: fixed;
  top: calc(var(--height-header) + 8px);
  right: var(--spacing-2xl);
  background: var(--color-bg-card);
  border: 1px solid var(--color-gray-100);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
  padding: var(--spacing-sm);
  min-width: 180px;
  z-index: 200;
}

.user-dropdown a {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md);
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  transition: all var(--transition-fast);
}

.user-dropdown a:hover {
  background: rgba(37, 99, 235, 0.05);
  color: var(--color-primary);
}

.user-dropdown button {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  width: 100%;
  padding: var(--spacing-md);
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  transition: all var(--transition-fast);
  background: transparent;
  border: none;
  cursor: pointer;
}

.user-dropdown button:hover {
  background: rgba(220, 38, 38, 0.05);
  color: var(--color-danger);
}

.dropdown-divider {
  height: 1px;
  background: var(--color-gray-100);
  margin: var(--spacing-sm) 0;
}

@media (max-width: 768px) {
  .user-dropdown {
    right: var(--spacing-md);
  }
}
</style>
