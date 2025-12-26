import request from './axios'

const billAPI = {
  // 获取账单列表
  getBillList(params = {}) {
    return request({
      url: '/bills',
      method: 'get',
      params
    })
  },

  // 获取账单详情
  getBillById(id) {
    return request({
      url: `/bills/${id}`,
      method: 'get'
    })
  },

  // 创建账单
  createBill(data) {
    return request({
      url: '/bills',
      method: 'post',
      data
    })
  },

  // 更新账单
  updateBill(id, data) {
    return request({
      url: `/bills/${id}`,
      method: 'put',
      data
    })
  },

  // 删除账单
  deleteBill(id) {
    return request({
      url: `/bills/${id}`,
      method: 'delete'
    })
  },

  // 账单结算 - 根据老人信息自动计算费用并生成账单
  settleBill(data) {
    return request({
      url: '/bills/settle',
      method: 'post',
      data
    })
  },

  // 支付账单
  payBill(id, data) {
    return request({
      url: `/bills/${id}/pay`,
      method: 'post',
      data
    })
  }
}

export default billAPI
