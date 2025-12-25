import axios from 'axios'

// 创建axios实例
const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api', // 后端API基础URL
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
apiClient.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
apiClient.interceptors.response.use(
  response => {
    return response
  },
  error => {
    // 统一处理错误
    if (error.response?.status === 401) {
      // token过期或无效，跳转到登录页
      localStorage.removeItem('token')
      localStorage.removeItem('isLogin')
      // 这里需要根据实际路由系统调整
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default apiClient