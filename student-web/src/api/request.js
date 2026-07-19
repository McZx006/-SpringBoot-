import axios from 'axios'
import { API_BASE_URL, STUDENT_TOKEN_KEY } from '../constants/app'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem(STUDENT_TOKEN_KEY)
  if (token) config.headers.Token = token
  return config
})

request.interceptors.response.use(
  response => response,
  error => {
    const status = error.response && error.response.status
    if (status === 401) {
      localStorage.removeItem(STUDENT_TOKEN_KEY)
      ElMessage.warning('请先登录后再操作')
      if (window.location.pathname !== '/login') {
        window.location.href = `/login?redirect=${encodeURIComponent(window.location.pathname + window.location.search)}`
      }
    } else if (status === 403) {
      ElMessage.error('当前账号无权执行该操作')
    } else if (error.response && error.response.data && error.response.data.msg) {
      ElMessage.error(error.response.data.msg)
    }
    return Promise.reject(error)
  }
)

export default request
