import axios from 'axios'

// 创建专门用于认证的axios实例，不包含认证头
const authApiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

const authAPI = {
  // 用户登录
  login(credentials) {
    return authApiClient.post('/auth/login', credentials)
  },
  
  // 用户登出
  logout() {
    return authApiClient.post('/auth/logout')
  }
}

export default authAPI