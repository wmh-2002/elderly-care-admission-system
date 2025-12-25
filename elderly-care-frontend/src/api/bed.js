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
  },

  // 根据房间ID获取床位列表
  getBedsByRoomId(roomId) {
    return apiClient.get(`/beds/room/${roomId}`)
  },

  // 更新床位状态
  updateBedStatus(id, status) {
    return apiClient.put(`/beds/${id}/status`, { status })
  },

  // 设为空闲状态
  setBedAvailable(id) {
    return apiClient.put(`/beds/${id}/available`)
  },

  // 设为已入住状态
  setBedOccupied(id) {
    return apiClient.put(`/beds/${id}/occupied`)
  },

  // 设为维修状态
  setBedMaintenance(id) {
    return apiClient.put(`/beds/${id}/maintenance`)
  },

  // 获取指定房间的空闲床位
  getAvailableBedsByRoom(roomId) {
    return apiClient.get(`/beds/available/room/${roomId}`)
  }
}

export default bedAPI