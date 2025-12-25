import apiClient from './axios'

const nursingPlanAPI = {
  // 获取护理计划列表（分页）
  getNursingPlanList(params) {
    return apiClient.get('/care-plans', { params })
  },

  // 根据ID获取护理计划信息
  getNursingPlanById(id) {
    return apiClient.get(`/care-plans/${id}`)
  },

  // 根据老人ID获取最新的护理计划
  getLatestNursingPlanByElderId(elderId) {
    return apiClient.get(`/care-plans/elder/${elderId}/latest`)
  },

  // 创建护理计划
  createNursingPlan(data) {
    return apiClient.post('/care-plans', data)
  },

  // 更新护理计划
  updateNursingPlan(id, data) {
    return apiClient.put(`/care-plans/${id}`, data)
  },

  // 删除护理计划
  deleteNursingPlan(id) {
    return apiClient.delete(`/care-plans/${id}`)
  }
}

export default nursingPlanAPI