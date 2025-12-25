import dashboardAPI from './dashboard'
import authAPI from './auth'
import elderAPI from './elder'
import roomAPI from './room'

// 将所有API模块导出
const api = {
  dashboard: dashboardAPI,
  auth: authAPI,
  elder: elderAPI,
  room: roomAPI
}

export default api