import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Resources from '../views/Resources.vue'
import ResourceDetail from '../views/ResourceDetail.vue'
import Exams from '../views/Exams.vue'
import ExamTake from '../views/ExamTake.vue'
import ExamResult from '../views/ExamResult.vue'
import Profile from '../views/Profile.vue'
import Storeup from '../views/Storeup.vue'
import ExamRecords from '../views/ExamRecords.vue'
import ExamRecordDetail from '../views/ExamRecordDetail.vue'
import WrongQuestions from '../views/WrongQuestions.vue'
import Messages from '../views/Messages.vue'
import Forum from '../views/Forum.vue'
import ForumCreate from '../views/ForumCreate.vue'
import ForumDetail from '../views/ForumDetail.vue'
import News from '../views/News.vue'
import NewsDetail from '../views/NewsDetail.vue'
import { STUDENT_TOKEN_KEY } from '../constants/app'

const routes = [
  { path: '/', component: Home },
  { path: '/login', component: Login },
  { path: '/register', component: Login },
  { path: '/resources', component: Resources },
  { path: '/resources/:id', component: ResourceDetail },
  { path: '/exams', component: Exams },
  { path: '/exam/result/:recordId', component: ExamResult, meta: { requiresAuth: true } },
  { path: '/exam/:paperId', component: ExamTake, meta: { requiresAuth: true } },
  { path: '/forum', component: Forum },
  { path: '/forum/create', component: ForumCreate, meta: { requiresAuth: true } },
  { path: '/forum/:id', component: ForumDetail },
  { path: '/news', component: News },
  { path: '/news/:id', component: NewsDetail },
  { path: '/messages', component: Messages, meta: { requiresAuth: true } },
  { path: '/profile', component: Profile, meta: { requiresAuth: true } },
  { path: '/profile/storeup', component: Storeup, meta: { requiresAuth: true } },
  { path: '/profile/exam-records', component: ExamRecords, meta: { requiresAuth: true } },
  { path: '/profile/exam-records/:id', component: ExamRecordDetail, meta: { requiresAuth: true } },
  { path: '/profile/wrong-questions', component: WrongQuestions, meta: { requiresAuth: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if (to.meta.requiresAuth && !localStorage.getItem(STUDENT_TOKEN_KEY)) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  return true
})

export default router
