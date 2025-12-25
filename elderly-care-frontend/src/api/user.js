import apiClient from './axios'

const userAPI = {
  // 获取用户列表（分页）
  getUserList(params) {
    return apiClient.get('/users', { params })
  },

  // 根据ID获取用户信息
  getUserById(id) {
    return apiClient.get(`/users/${id}`)
  },

  // 创建用户
  createUser(data) {
    return apiClient.post('/users', data)
  },

  // 更新用户
  updateUser(id, data) {
    return apiClient.put(`/users/${id}`, data)
  },

  // 删除用户
  deleteUser(id) {
    return apiClient.delete(`/users/${id}`)
  },

  // 启用/禁用用户
  updateUserStatus(id, status) {
    return apiClient.put(`/users/${id}/status`, { status })
  }
}

export default userAPI
