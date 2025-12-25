import apiClient from './axios'

const bedAPI = {
  // 获取床位列表（分页）
  getBedList(params) {
    return apiClient.get('/beds', { params })
  },
  
  // 根据ID获取床位信息
  getBedById(id) {
    return apiClient.get(`/beds/${id}`)
  },
  
  // 获取空闲床位
  getAvailableBeds() {
    return apiClient.get('/beds/available')
  },
  
  // 创建床位
  createBed(data) {
    return apiClient.post('/beds', data)
  },
  
  // 更新床位
  updateBed(id, data) {
    return apiClient.put(`/beds/${id}`, data)
  },
  
  // 删除床位
  deleteBed(id) {
    return apiClient.delete(`/beds/${id}`)
  }
}

export default bedAPI