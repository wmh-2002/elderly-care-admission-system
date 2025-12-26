import request from './axios'

// 获取老人分析数据
export function getElderAnalysisData() {
  return request({
    url: '/data-analysis/elder',
    method: 'get'
  })
}

// 获取营收分析数据
export function getRevenueAnalysisData() {
  return request({
    url: '/data-analysis/revenue',
    method: 'get'
  })
}

// 获取运营分析数据
export function getOperationAnalysisData() {
  return request({
    url: '/data-analysis/operation',
    method: 'get'
  })
}

// 获取完整的数据分析数据
export function getAllAnalysisData() {
  return request({
    url: '/data-analysis',
    method: 'get'
  })
}
