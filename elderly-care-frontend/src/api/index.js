import dashboardAPI from './dashboard'
import authAPI from './auth'
import elderAPI from './elder'
import roomAPI from './room'
import nursingCategoryAPI from './nursingCategory'
import nursingPlanAPI from './nursingPlan'
import roleAPI from './role'
import userAPI from './user'
import billAPI from './bill'
import { getElderAnalysisData, getRevenueAnalysisData, getOperationAnalysisData, getAllAnalysisData } from './dataAnalysis'

// 将所有API模块导出
const api = {
  dashboard: dashboardAPI,
  auth: authAPI,
  elder: elderAPI,
  room: roomAPI,
  nursingCategory: nursingCategoryAPI,
  nursingPlan: nursingPlanAPI,
  role: roleAPI,
  user: userAPI,
  bill: billAPI,
  dataAnalysis: {
    getElderAnalysisData,
    getRevenueAnalysisData,
    getOperationAnalysisData,
    getAllAnalysisData
  }
}

export default api