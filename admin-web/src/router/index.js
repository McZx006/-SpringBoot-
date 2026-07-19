import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import ListPage from '../views/ListPage.vue'
import QuestionPage from '../views/QuestionPage.vue'
import OpsPage from '../views/OpsPage.vue'
import { ADMIN_TOKEN_KEY } from '../constants/app'

Vue.use(VueRouter)

const router = new VueRouter({
  mode: 'history',
  routes: [
    { path: '/admin/login', component: Login },
    { path: '/admin/dashboard', component: Dashboard },
    { path: '/admin/xueyuan', component: ListPage },
    { path: '/admin/resource-types', component: ListPage },
    { path: '/admin/resources', component: ListPage },
    { path: '/admin/storeup', component: OpsPage },
    { path: '/admin/exampapers', component: ListPage },
    { path: '/admin/examquestions', component: QuestionPage },
    { path: '/admin/examrecords', component: OpsPage },
    { path: '/admin/forum', component: OpsPage },
    { path: '/admin/messages', component: OpsPage },
    { path: '/admin/news', component: OpsPage },
    { path: '/admin/banners', component: OpsPage },
    { path: '*', redirect: '/admin/dashboard' }
  ]
})

router.beforeEach((to, from, next) => {
  if (to.path !== '/admin/login' && !localStorage.getItem(ADMIN_TOKEN_KEY)) {
    next({ path: '/admin/login', query: { redirect: to.fullPath } })
    return
  }
  next()
})

export default router
