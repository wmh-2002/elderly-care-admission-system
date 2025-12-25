import apiClient from './axios'

const dashboardAPI = {
  // 获取仪表盘数据
  getDashboardData() {
    return apiClient.get('/dashboard')
  }
}

export default dashboardAPI