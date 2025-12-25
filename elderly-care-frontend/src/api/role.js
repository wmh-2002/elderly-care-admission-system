import apiClient from './axios'

const roleAPI = {
  // 获取角色列表（分页）
  getRoleList(params) {
    return apiClient.get('/roles', { params })
  },

  // 根据ID获取角色信息
  getRoleById(id) {
    return apiClient.get(`/roles/${id}`)
  },

  // 创建角色
  createRole(data) {
    return apiClient.post('/roles', data)
  },

  // 更新角色
  updateRole(id, data) {
    return apiClient.put(`/roles/${id}`, data)
  },

  // 删除角色
  deleteRole(id) {
    return apiClient.delete(`/roles/${id}`)
  }
}

export default roleAPI
