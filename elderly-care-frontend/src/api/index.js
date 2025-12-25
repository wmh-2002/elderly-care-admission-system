import dashboardAPI from './dashboard'
import authAPI from './auth'
import elderAPI from './elder'
import roomAPI from './room'
import nursingCategoryAPI from './nursingCategory'
import nursingPlanAPI from './nursingPlan'
import roleAPI from './role'
import userAPI from './user'

// 将所有API模块导出
const api = {
  dashboard: dashboardAPI,
  auth: authAPI,
  elder: elderAPI,
  room: roomAPI,
  nursingCategory: nursingCategoryAPI,
  nursingPlan: nursingPlanAPI,
  role: roleAPI,
  user: userAPI
}

export default api