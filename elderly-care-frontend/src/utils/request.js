/**
 * API请求工具类
 * 使用Axios封装HTTP请求
 */

import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useStore } from '@/store'

// 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:8080/api', // API的基础URL，实际开发中应该从环境变量获取
  timeout: 10000, // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从Vuex store中获取token
    // const token = localStorage.getItem('token')
    const token = 'fake-jwt-token' // 使用假token进行演示
    
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    
    // 设置请求头
    config.headers['Content-Type'] = 'application/json'
    
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const { code, message, data } = response.data
    
    // 根据后端返回的code判断请求是否成功
    if (code === 200) {
      return { data, message, code }
    } else {
      // 请求失败，显示错误信息
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message || '请求失败'))
    }
  },
  error => {
    console.error('响应错误:', error)
    
    // 根据HTTP状态码处理错误
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          // 跳转到登录页面的逻辑可以在这里实现
          break
        case 403:
          ElMessage.error('没有权限访问该资源')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(data.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    
    return Promise.reject(error)
  }
)

// 导出service实例
export default service

// API接口定义
export const api = {
  // 用户相关API
  user: {
    login: (data) => service.post('/login', data),
    logout: () => service.post('/logout'),
    getUserInfo: () => service.get('/user/info'),
    getUsers: (params) => service.get('/user/list', { params }),
    getUserById: (id) => service.get(`/user/${id}`),
    createUser: (data) => service.post('/user/create', data),
    updateUser: (data) => service.put('/user/update', data),
    deleteUser: (id) => service.delete(`/user/delete/${id}`),
    resetPassword: (id) => service.post(`/user/reset-password/${id}`),
    toggleUserStatus: (id, status) => service.put(`/user/status/${id}`, { status })
  },
  
  // 老人相关API
  elder: {
    getElders: (params) => service.get('/elder/list', { params }),
    getElderById: (id) => service.get(`/elder/${id}`),
    createElder: (data) => service.post('/elder/save', data),
    updateElder: (data) => service.put('/elder/update', data),
    deleteElder: (id) => service.delete(`/elder/delete/${id}`),
    getIn院ElderCount: () => service.get('/elder/in院-count')
  },
  
  // 房间相关API
  room: {
    getRooms: (params) => service.get('/room/list', { params }),
    getRoomById: (id) => service.get(`/room/${id}`),
    createRoom: (data) => service.post('/room/save', data),
    updateRoom: (data) => service.put('/room/update', data),
    deleteRoom: (id) => service.delete(`/room/delete/${id}`),
    getRoomCount: () => service.get('/room/count'),
    getAvailableRoomCount: () => service.get('/room/available-count'),
    getUsedRoomCount: () => service.get('/room/used-count')
  },
  
  // 床位相关API
  bed: {
    getBeds: (params) => service.get('/bed/list', { params }),
    getBedById: (id) => service.get(`/bed/${id}`),
    createBed: (data) => service.post('/bed/save', data),
    updateBed: (data) => service.put('/bed/update', data),
    deleteBed: (id) => service.delete(`/bed/delete/${id}`),
    getBedsByRoomId: (roomId) => service.get(`/bed/room/${roomId}`),
    getBedsByStatus: (status) => service.get(`/bed/status/${status}`),
    getAvailableBedCount: () => service.get('/bed/available-count'),
    getUsedBedCount: () => service.get('/bed/used-count'),
    getUnderRepairBedCount: () => service.get('/bed/repair-count')
  },
  
  // 护理相关API
  care: {
    getCareRecords: (params) => service.get('/care/list', { params }),
    getCareRecordById: (id) => service.get(`/care/${id}`),
    createCareRecord: (data) => service.post('/care/save', data),
    updateCareRecord: (data) => service.put('/care/update', data),
    deleteCareRecord: (id) => service.delete(`/care/delete/${id}`)
  },
  
  // 费用相关API
  bill: {
    getBills: (params) => service.get('/bill/list', { params }),
    getBillById: (id) => service.get(`/bill/${id}`),
    createBill: (data) => service.post('/bill/save', data),
    updateBill: (data) => service.put('/bill/update', data),
    deleteBill: (id) => service.delete(`/bill/delete/${id}`),
    createPayment: (data) => service.post('/bill/payment', data)
  }
}