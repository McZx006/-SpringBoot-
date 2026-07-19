<template>
  <div class="admin-shell">
    <aside v-if="$route.path !== '/admin/login'" class="sidebar">
      <h1>线上教学平台</h1>
      <router-link
        v-for="item in menuItems"
        :key="item.path"
        :to="item.path"
      >
        {{ item.label }}
      </router-link>
    </aside>
    <main class="admin-main">
      <header v-if="$route.path !== '/admin/login'" class="admin-top">
        <span>后台管理</span>
        <router-link to="/admin/login">退出登录</router-link>
      </header>
      <section class="admin-content">
        <router-view />
      </section>
    </main>
  </div>
</template>

<script>
import { ADMIN_MENU_ITEMS as menuItems } from './constants/app'

export default {
  data() {
    return {
      menuItems
    }
  }
}
</script>
