import apiClient from './axios'

const nursingCategoryAPI = {
  // 获取护理分类列表（分页）
  getNursingCategoryList(params) {
    return apiClient.get('/care-levels', { params })
  },
  
  // 根据编码获取护理分类信息
  getNursingCategoryByCode(levelCode) {
    return apiClient.get(`/care-levels/${levelCode}`)
  },
  
  // 创建护理分类
  createNursingCategory(data) {
    return apiClient.post('/care-levels', data)
  },
  
  // 更新护理分类
  updateNursingCategory(levelCode, data) {
    return apiClient.put(`/care-levels/${levelCode}`, data)
  },
  
  // 删除护理分类
  deleteNursingCategory(levelCode) {
    return apiClient.delete(`/care-levels/${levelCode}`)
  }
}

export default nursingCategoryAPI