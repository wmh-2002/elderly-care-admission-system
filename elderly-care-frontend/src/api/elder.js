import apiClient from './axios'

const elderAPI = {
  // 获取老人列表（分页）
  getElderList(params) {
    return apiClient.get('/elders', { params })
  },
  
  // 根据ID获取老人信息
  getElderById(id) {
    return apiClient.get(`/elders/${id}`)
  },
  
  // 创建老人档案
  createElder(data) {
    return apiClient.post('/elders', data)
  },
  
  // 更新老人档案
  updateElder(id, data) {
    return apiClient.put(`/elders/${id}`, data)
  },
  
  // 删除老人档案
  deleteElder(id) {
    return apiClient.delete(`/elders/${id}`)
  }
}

export default elderAPI