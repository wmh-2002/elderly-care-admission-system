<template>
  <div class="operation-analysis">
    <div class="page-header">
      <h2>运营分析</h2>
      <p>设施运营数据统计与分析</p>
    </div>

    <div class="analysis-content">
      <!-- 运营统计指标 -->
      <div class="operation-metrics">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="metric-card primary">
              <div class="metric-content">
                <div class="metric-icon">
                  <el-icon><OfficeBuilding /></el-icon>
                </div>
                <div class="metric-info">
                  <div class="metric-number">{{ operationStats.totalRooms }}</div>
                  <div class="metric-label">总房间数</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="metric-card success">
              <div class="metric-content">
                <div class="metric-icon">
                  <el-icon><UserFilled /></el-icon>
                </div>
                <div class="metric-info">
                  <div class="metric-number">{{ operationStats.totalBeds }}</div>
                  <div class="metric-label">总床位数</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="metric-card warning">
              <div class="metric-content">
                <div class="metric-icon">
                  <el-icon><Check /></el-icon>
                </div>
                <div class="metric-info">
                  <div class="metric-number">{{ operationStats.occupiedBeds }}</div>
                  <div class="metric-label">已入住床位</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="metric-card info">
              <div class="metric-content">
                <div class="metric-icon">
                  <el-icon><PieChart /></el-icon>
                </div>
                <div class="metric-info">
                  <div class="metric-number">{{ operationStats.occupancyRate }}%</div>
                  <div class="metric-label">入住率</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 图表区域 -->
      <div class="charts-section">
        <el-row :gutter="20">
          <!-- 房间类型分布 -->
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>房间类型分布</span>
                  <el-button type="text" size="small" @click="refreshData">刷新</el-button>
                </div>
              </template>
              <div class="chart-container">
                <div id="roomTypeChart" class="chart"></div>
              </div>
            </el-card>
          </el-col>

          <!-- 床位状态分布 -->
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>床位状态分布</span>
                  <el-button type="text" size="small" @click="refreshData">刷新</el-button>
                </div>
              </template>
              <div class="chart-container">
                <div id="bedStatusChart" class="chart"></div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="20" style="margin-top: 20px;">
          <!-- 月度入住趋势 -->
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>月度入住趋势</span>
                  <el-button type="text" size="small" @click="refreshData">刷新</el-button>
                </div>
              </template>
              <div class="chart-container">
                <div id="occupancyTrendChart" class="chart"></div>
              </div>
            </el-card>
          </el-col>

          <!-- 楼层分布 -->
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>楼层分布统计</span>
                  <el-button type="text" size="small" @click="refreshData">刷新</el-button>
                </div>
              </template>
              <div class="chart-container">
                <div id="floorDistributionChart" class="chart"></div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 设施管理 -->
      <div class="facilities-section">
        <el-card class="facilities-card">
          <template #header>
            <div class="card-header">
              <span>设施管理</span>
              <div class="header-actions">
                <el-select v-model="facilityFilter" placeholder="设施状态" style="width: 120px; margin-right: 10px;" clearable>
                  <el-option label="正常" value="normal" />
                  <el-option label="维修中" value="maintenance" />
                  <el-option label="故障" value="fault" />
                </el-select>
                <el-button type="primary" size="small" @click="exportFacilities">导出报告</el-button>
              </div>
            </div>
          </template>

          <!-- 设施概览 -->
          <div class="facilities-overview">
            <el-row :gutter="20">
              <el-col :span="6">
                <div class="facility-stat">
                  <div class="stat-number">{{ facilityStats.totalRooms }}</div>
                  <div class="stat-label">总房间</div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="facility-stat">
                  <div class="stat-number">{{ facilityStats.availableRooms }}</div>
                  <div class="stat-label">可用房间</div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="facility-stat">
                  <div class="stat-number">{{ facilityStats.maintenanceRooms }}</div>
                  <div class="stat-label">维修中</div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="facility-stat">
                  <div class="stat-number">{{ facilityStats.occupiedRate }}%</div>
                  <div class="stat-label">占用率</div>
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 设施列表 -->
          <el-table :data="filteredFacilities" stripe style="width: 100%; margin-top: 20px;" height="300">
            <el-table-column prop="roomNo" label="房间号" width="100"></el-table-column>
            <el-table-column prop="roomType" label="房间类型" width="120"></el-table-column>
            <el-table-column prop="floor" label="楼层" width="80"></el-table-column>
            <el-table-column prop="maxBeds" label="最大床位" width="100"></el-table-column>
            <el-table-column prop="occupiedBeds" label="已占用" width="100"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusTagType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="lastMaintenance" label="最后维修" width="120"></el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button type="text" size="small" @click="viewFacilityDetail(scope.row)">详情</el-button>
                <el-button type="text" size="small" @click="scheduleMaintenance(scope.row)">维修</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { OfficeBuilding, UserFilled, Check, PieChart } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import api from '@/api'

// 运营统计数据
const operationStats = ref({
  totalRooms: 25,
  totalBeds: 65,
  occupiedBeds: 58,
  occupancyRate: 89
})

// 设施统计数据
const facilityStats = ref({
  totalRooms: 25,
  availableRooms: 22,
  maintenanceRooms: 3,
  occupiedRate: 89
})

// 图表实例
let roomTypeChart = null
let bedStatusChart = null
let occupancyTrendChart = null
let floorDistributionChart = null

// 动态导入echarts
let echarts = null

// 筛选条件
const facilityFilter = ref('')

// 设施数据
const facilities = ref([
  {
    roomNo: '101',
    roomType: '单人间',
    floor: 1,
    maxBeds: 1,
    occupiedBeds: 1,
    status: 'normal',
    lastMaintenance: '2024-10-15'
  },
  {
    roomNo: '102',
    roomType: '双人间',
    floor: 1,
    maxBeds: 2,
    occupiedBeds: 2,
    status: 'normal',
    lastMaintenance: '2024-09-20'
  },
  {
    roomNo: '201',
    roomType: '三人间',
    floor: 2,
    maxBeds: 3,
    occupiedBeds: 3,
    status: 'normal',
    lastMaintenance: '2024-11-05'
  },
  {
    roomNo: '202',
    roomType: '单人间',
    floor: 2,
    maxBeds: 1,
    occupiedBeds: 1,
    status: 'maintenance',
    lastMaintenance: '2024-12-20'
  },
  {
    roomNo: '301',
    roomType: '双人间',
    floor: 3,
    maxBeds: 2,
    occupiedBeds: 2,
    status: 'normal',
    lastMaintenance: '2024-08-30'
  }
])

// 筛选后的设施数据
const filteredFacilities = computed(() => {
  return facilities.value.filter(facility => {
    const matchesFilter = !facilityFilter.value || facility.status === facilityFilter.value
    return matchesFilter
  })
})

// 获取状态标签类型
const getStatusTagType = (status) => {
  const types = {
    'normal': 'success',
    'maintenance': 'warning',
    'fault': 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    'normal': '正常',
    'maintenance': '维修中',
    'fault': '故障'
  }
  return texts[status] || status
}

// 渲染房间类型分布图表
const renderRoomTypeChart = async () => {
  const chartDom = document.getElementById('roomTypeChart')
  if (!chartDom) return

  if (!echarts) {
    echarts = await import('echarts')
  }

  roomTypeChart = echarts.init(chartDom)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '房间类型',
        type: 'pie',
        radius: '50%',
        data: operationStats.value.roomTypeDistribution || [
          { value: 8, name: '单人间' },
          { value: 12, name: '双人间' },
          { value: 5, name: '三人间' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  roomTypeChart.setOption(option)
}

// 渲染床位状态分布图表
const renderBedStatusChart = async () => {
  const chartDom = document.getElementById('bedStatusChart')
  if (!chartDom) return

  if (!echarts) {
    echarts = await import('echarts')
  }

  bedStatusChart = echarts.init(chartDom)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '床位状态',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: operationStats.value.bedStatusDistribution || [
          { value: 58, name: '已入住' },
          { value: 5, name: '空闲' },
          { value: 2, name: '维修中' }
        ]
      }
    ]
  }
  bedStatusChart.setOption(option)
}

// 渲染入住趋势图表
const renderOccupancyTrendChart = async () => {
  const chartDom = document.getElementById('occupancyTrendChart')
  if (!chartDom) return

  if (!echarts) {
    echarts = await import('echarts')
  }

  occupancyTrendChart = echarts.init(chartDom)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['入住率', '目标率']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: operationStats.value.occupancyTrend?.map(item => item.name) || ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    },
    yAxis: {
      type: 'value',
      name: '入住率(%)'
    },
    series: [
      {
        name: '入住率',
        type: 'line',
        data: operationStats.value.occupancyTrend?.map(item => item.value) || [82, 85, 87, 88, 89, 90, 91, 91, 90, 89, 91, 91],
        smooth: true,
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '目标率',
        type: 'line',
        data: [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85],
        smooth: true,
        itemStyle: { color: '#F56C6C' },
        lineStyle: { type: 'dashed' }
      }
    ]
  }
  occupancyTrendChart.setOption(option)
}

// 渲染楼层分布图表
const renderFloorDistributionChart = async () => {
  const chartDom = document.getElementById('floorDistributionChart')
  if (!chartDom) return

  if (!echarts) {
    echarts = await import('echarts')
  }

  floorDistributionChart = echarts.init(chartDom)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: [
      {
        type: 'category',
        data: operationStats.value.floorDistribution?.map(item => item.name) || ['1楼', '2楼', '3楼', '4楼', '5楼'],
        axisTick: {
          alignWithLabel: true
        }
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '房间数量'
      }
    ],
    series: [
      {
        name: '楼层分布',
        type: 'bar',
        barWidth: '60%',
        data: operationStats.value.floorDistribution?.map(item => item.value) || [6, 8, 6, 4, 1],
        itemStyle: {
          color: '#67C23A'
        }
      }
    ]
  }
  floorDistributionChart.setOption(option)
}

// 刷新数据
const refreshData = () => {
  ElMessage.success('数据刷新中...')
  setTimeout(() => {
    renderRoomTypeChart()
    renderBedStatusChart()
    renderOccupancyTrendChart()
    renderFloorDistributionChart()
    ElMessage.success('数据刷新完成')
  }, 1000)
}

// 导出设施报告
const exportFacilities = () => {
  ElMessage.success('设施报告导出功能开发中...')
}

// 查看设施详情
const viewFacilityDetail = (row) => {
  ElMessage.info(`查看房间 ${row.roomNo} 的详细信息`)
}

// 安排维修
const scheduleMaintenance = (row) => {
  row.status = 'maintenance'
  row.lastMaintenance = new Date().toISOString().split('T')[0]
  ElMessage.success(`已安排房间 ${row.roomNo} 维修`)
}

// 渲染所有图表
const renderCharts = async () => {
  setTimeout(async () => {
    await renderRoomTypeChart()
    await renderBedStatusChart()
    await renderOccupancyTrendChart()
    await renderFloorDistributionChart()
  }, 100)
}

// 窗口大小改变时重新渲染图表
const handleResize = () => {
  if (roomTypeChart) roomTypeChart.resize()
  if (bedStatusChart) bedStatusChart.resize()
  if (occupancyTrendChart) occupancyTrendChart.resize()
  if (floorDistributionChart) floorDistributionChart.resize()
}

// 初始化数据
const loadData = async () => {
  console.log('开始加载运营分析数据...')
  try {
    console.log('调用 getOperationAnalysisData API...')
    const response = await api.dataAnalysis.getOperationAnalysisData()
    console.log('API响应:', response)

    if (response.data && response.data.code === 200) {
      const data = response.data.data
      console.log('API返回的数据:', data)

      operationStats.value = {
        totalRooms: data.totalRooms || 0,
        totalBeds: data.totalBeds || 0,
        occupiedBeds: data.occupiedBeds || 0,
        occupancyRate: Math.round((data.occupancyRate || 0) * 10) / 10
      }

      facilityStats.value = {
        totalRooms: data.totalRooms || 0,
        availableRooms: (data.totalRooms || 0) - (data.facilityList ? data.facilityList.filter(f => f.status === 'maintenance').length : 0),
        maintenanceRooms: data.facilityList ? data.facilityList.filter(f => f.status === 'maintenance').length : 0,
        occupiedRate: Math.round((data.occupancyRate || 0) * 10) / 10
      }

      // 更新设施列表
      facilities.value = data.facilityList ? data.facilityList.map(item => ({
        roomNo: item.roomNo || '',
        roomType: item.roomType || '',
        floor: item.floor || '',
        maxBeds: item.maxBeds || 0,
        occupiedBeds: item.occupiedBeds || 0,
        status: item.status || '',
        lastMaintenance: item.lastMaintenance || ''
      })) : []

      console.log('operationStats已更新:', operationStats.value)
      console.log('facilityStats已更新:', facilityStats.value)
      console.log('facilities列表已更新，长度:', facilities.value.length)
    } else {
      console.warn('API响应格式不正确:', response.data)
    }
    // 如果API调用失败，继续使用默认的模拟数据
  } catch (error) {
    console.error('获取运营分析数据失败:', error)
    console.warn('使用默认数据')
    // 使用默认数据
  }

  // 渲染图表
  renderCharts()
}

// 组件挂载时初始化
onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

// 组件卸载时清理
const cleanup = () => {
  window.removeEventListener('resize', handleResize)
  if (roomTypeChart) roomTypeChart.dispose()
  if (bedStatusChart) bedStatusChart.dispose()
  if (occupancyTrendChart) occupancyTrendChart.dispose()
  if (floorDistributionChart) floorDistributionChart.dispose()
}

defineExpose({ cleanup })
</script>

<style scoped>
.operation-analysis {
  padding: 0px;
}

.page-header {
  margin-bottom: 30px;
  text-align: left;
}

.page-header h2 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 28px;
  font-weight: 500;
}

.page-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.operation-metrics {
  margin-bottom: 30px;
}

.metric-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.metric-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.metric-card.primary {
  border-left: 4px solid #409EFF;
}

.metric-card.success {
  border-left: 4px solid #67C23A;
}

.metric-card.warning {
  border-left: 4px solid #E6A23C;
}

.metric-card.info {
  border-left: 4px solid #909399;
}

.metric-content {
  display: flex;
  align-items: center;
  padding: 20px;
}

.metric-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  color: white;
  font-size: 24px;
}

.metric-info {
  flex: 1;
}

.metric-number {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.metric-label {
  font-size: 14px;
  color: #909399;
}

.charts-section, .facilities-section {
  margin-bottom: 30px;
}

.chart-card, .facilities-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}

.chart-container {
  height: 300px;
  width: 100%;
}

.chart {
  width: 100%;
  height: 100%;
}

.facilities-overview {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.facility-stat {
  text-align: center;
  padding: 10px;
}

.facility-stat .stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.facility-stat .stat-label {
  font-size: 12px;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .operation-analysis {
    padding: 10px;
  }

  .metric-content {
    flex-direction: column;
    text-align: center;
  }

  .metric-icon {
    margin-right: 0;
    margin-bottom: 10px;
  }

  .charts-section .el-col {
    margin-bottom: 20px;
  }

  .header-actions {
    flex-direction: column;
    gap: 10px;
  }

  .facilities-overview .el-col {
    margin-bottom: 15px;
  }
}
</style>
