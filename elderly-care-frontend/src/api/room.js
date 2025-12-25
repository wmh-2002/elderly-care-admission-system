import apiClient from './axios'

const roomAPI = {
  // 获取房间列表（分页）
  getRoomList(params) {
    return apiClient.get('/rooms', { params })
  },
  
  // 根据ID获取房间信息
  getRoomById(id) {
    return apiClient.get(`/rooms/${id}`)
  },
  
  // 获取空闲房间
  getAvailableRooms(params) {
    return apiClient.get('/rooms/available', { params })
  },
  
  // 创建房间
  createRoom(data) {
    return apiClient.post('/rooms', data)
  },
  
  // 更新房间
  updateRoom(id, data) {
    return apiClient.put(`/rooms/${id}`, data)
  },
  
  // 删除房间
  deleteRoom(id) {
    return apiClient.delete(`/rooms/${id}`)
  },
  
  // 获取指定房间的可用床位
  getAvailableBedsByRoomId(roomId) {
    return apiClient.get(`/beds/available/room/${roomId}`)
  }
}

export default roomAPI