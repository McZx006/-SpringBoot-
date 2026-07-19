import axios from 'axios'
import { ADMIN_TOKEN_KEY, API_BASE_URL } from '../constants/app'

const request = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem(ADMIN_TOKEN_KEY)
  if (token) config.headers.Token = token
  return config
})

request.interceptors.response.use(
  response => response,
  error => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem(ADMIN_TOKEN_KEY)
      if (window.location.pathname !== '/admin/login') {
        window.location.href = `/admin/login?redirect=${encodeURIComponent(window.location.pathname + window.location.search)}`
      }
    }
    return Promise.reject(error)
  }
)

export default request
