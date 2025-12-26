<template>
  <div class="revenue-analysis">
    <div class="page-header">
      <h2>营收分析</h2>
      <p>费用收支统计与分析</p>
    </div>

    <div class="analysis-content">
      <!-- 营收统计指标 -->
      <div class="revenue-metrics">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="metric-card primary">
              <div class="metric-content">
                <div class="metric-icon">
                  <el-icon><Money /></el-icon>
                </div>
                <div class="metric-info">
                  <div class="metric-number">¥{{ revenueStats.monthlyRevenue }}</div>
                  <div class="metric-label">本月营收</div>
                  <div class="metric-change positive">
                    <el-icon><Top /></el-icon>
                    +15.3%
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="metric-card success">
              <div class="metric-content">
                <div class="metric-icon">
                  <el-icon><Wallet /></el-icon>
                </div>
                <div class="metric-info">
                  <div class="metric-number">¥{{ revenueStats.totalRevenue }}</div>
                  <div class="metric-label">累计营收</div>
                  <div class="metric-change positive">
                    <el-icon><Top /></el-icon>
                    +12.8%
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="metric-card warning">
              <div class="metric-content">
                <div class="metric-icon">
                  <el-icon><Document /></el-icon>
                </div>
                <div class="metric-info">
                  <div class="metric-number">¥{{ revenueStats.pendingPayment }}</div>
                  <div class="metric-label">待收款</div>
                  <div class="metric-change negative">
                    <el-icon><Bottom /></el-icon>
                    -5.2%
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="metric-card info">
              <div class="metric-content">
                <div class="metric-icon">
                  <el-icon><TrendCharts /></el-icon>
                </div>
                <div class="metric-info">
                  <div class="metric-number">{{ revenueStats.avgRevenuePerElder }}</div>
                  <div class="metric-label">人均营收</div>
                  <div class="metric-change positive">
                    <el-icon><Top /></el-icon>
                    +8.7%
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 图表区域 -->
      <div class="charts-section">
        <el-row :gutter="20">
          <!-- 营收趋势 -->
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>月度营收趋势</span>
                  <el-button type="text" size="small" @click="refreshData">刷新</el-button>
                </div>
              </template>
              <div class="chart-container">
                <div id="revenueTrendChart" class="chart"></div>
              </div>
            </el-card>
          </el-col>

          <!-- 费用项目占比 -->
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>费用项目占比</span>
                  <el-button type="text" size="small" @click="refreshData">刷新</el-button>
                </div>
              </template>
              <div class="chart-container">
                <div id="feeItemChart" class="chart"></div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="20" style="margin-top: 20px;">
          <!-- 支付方式统计 -->
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>支付方式统计</span>
                  <el-button type="text" size="small" @click="refreshData">刷新</el-button>
                </div>
              </template>
              <div class="chart-container">
                <div id="paymentMethodChart" class="chart"></div>
              </div>
            </el-card>
          </el-col>

          <!-- 年度对比 -->
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>年度营收对比</span>
                  <el-button type="text" size="small" @click="refreshData">刷新</el-button>
                </div>
              </template>
              <div class="chart-container">
                <div id="yearlyComparisonChart" class="chart"></div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 账单管理 -->
      <div class="bills-section">
        <el-card class="bills-card">
          <template #header>
            <div class="card-header">
              <span>账单管理</span>
              <div class="header-actions">
                <el-select v-model="billStatus" placeholder="账单状态" style="width: 120px; margin-right: 10px;" clearable>
                  <el-option label="未缴清" value="0" />
                  <el-option label="已缴清" value="1" />
                </el-select>
                <el-date-picker
                  v-model="dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  style="width: 300px; margin-right: 10px;"
                />
                <el-button type="primary" size="small" @click="exportBills">导出账单</el-button>
              </div>
            </div>
          </template>
          <el-table :data="filteredBills" stripe style="width: 100%" height="400">
            <el-table-column prop="billMonth" label="账单月份" width="100"></el-table-column>
            <el-table-column prop="elderName" label="老人姓名" width="100"></el-table-column>
            <el-table-column prop="totalAmount" label="应收金额" width="120">
              <template #default="scope">
                ¥{{ scope.row.totalAmount }}
              </template>
            </el-table-column>
            <el-table-column prop="paidAmount" label="已付金额" width="120">
              <template #default="scope">
                ¥{{ scope.row.paidAmount }}
              </template>
            </el-table-column>
            <el-table-column prop="pendingAmount" label="待付金额" width="120">
              <template #default="scope">
                ¥{{ scope.row.pendingAmount }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'warning'">
                  {{ scope.row.status === 1 ? '已缴清' : '未缴清' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="payMethod" label="支付方式" width="100"></el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button type="text" size="small" @click="viewBillDetail(scope.row)">详情</el-button>
                <el-button
                  v-if="scope.row.status === 0"
                  type="text"
                  size="small"
                  @click="markAsPaid(scope.row)"
                >
                  标记已付
                </el-button>
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
import { Money, Wallet, Document, TrendCharts, Top, Bottom } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import api from '@/api'

// 营收统计数据
const revenueStats = ref({
  monthlyRevenue: 456780,
  totalRevenue: 5200000,
  pendingPayment: 125000,
  avgRevenuePerElder: 3300
})

// 图表实例
let revenueTrendChart = null
let feeItemChart = null
let paymentMethodChart = null
let yearlyComparisonChart = null

// 动态导入echarts
let echarts = null

// 筛选条件
const billStatus = ref('')
const dateRange = ref([])

// 账单数据
const bills = ref([
  {
    billMonth: '2024-12',
    elderName: '张三',
    totalAmount: 15000,
    paidAmount: 15000,
    pendingAmount: 0,
    status: 1,
    payMethod: '微信支付'
  },
  {
    billMonth: '2024-12',
    elderName: '李四',
    totalAmount: 18000,
    paidAmount: 12000,
    pendingAmount: 6000,
    status: 0,
    payMethod: '现金'
  },
  {
    billMonth: '2024-12',
    elderName: '王五',
    totalAmount: 20000,
    paidAmount: 20000,
    pendingAmount: 0,
    status: 1,
    payMethod: '支付宝'
  },
  {
    billMonth: '2024-11',
    elderName: '赵六',
    totalAmount: 15000,
    paidAmount: 15000,
    pendingAmount: 0,
    status: 1,
    payMethod: '银行转账'
  },
  {
    billMonth: '2024-11',
    elderName: '孙七',
    totalAmount: 16000,
    paidAmount: 8000,
    pendingAmount: 8000,
    status: 0,
    payMethod: '现金'
  }
])

// 筛选后的账单数据
const filteredBills = computed(() => {
  return bills.value.filter(bill => {
    const matchesStatus = !billStatus.value || bill.status.toString() === billStatus.value
    return matchesStatus
  })
})

// 渲染营收趋势图表
const renderRevenueTrendChart = async () => {
  const chartDom = document.getElementById('revenueTrendChart')
  if (!chartDom) return

  if (!echarts) {
    echarts = await import('echarts')
  }

  revenueTrendChart = echarts.init(chartDom)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['营收金额', '目标金额']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: revenueStats.value.revenueTrend?.map(item => item.name) || ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    },
    yAxis: {
      type: 'value',
      name: '金额(元)'
    },
    series: [
      {
        name: '营收金额',
        type: 'line',
        data: revenueStats.value.revenueTrend?.map(item => item.value) || [320000, 350000, 380000, 420000, 450000, 480000, 520000, 550000, 580000, 620000, 650000, 680000],
        smooth: true,
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '目标金额',
        type: 'line',
        data: [300000, 330000, 360000, 390000, 420000, 450000, 480000, 510000, 540000, 570000, 600000, 630000],
        smooth: true,
        itemStyle: { color: '#F56C6C' },
        lineStyle: { type: 'dashed' }
      }
    ]
  }
  revenueTrendChart.setOption(option)
}

// 渲染费用项目占比图表
const renderFeeItemChart = async () => {
  const chartDom = document.getElementById('feeItemChart')
  if (!chartDom) return

  if (!echarts) {
    echarts = await import('echarts')
  }

  feeItemChart = echarts.init(chartDom)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c}% ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '费用项目',
        type: 'pie',
        radius: '50%',
        data: [
          { value: 35, name: '住宿费' },
          { value: 30, name: '护理费' },
          { value: 20, name: '餐饮费' },
          { value: 10, name: '医疗费' },
          { value: 5, name: '其他费用' }
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
  feeItemChart.setOption(option)
}

// 渲染支付方式统计图表
const renderPaymentMethodChart = async () => {
  const chartDom = document.getElementById('paymentMethodChart')
  if (!chartDom) return

  if (!echarts) {
    echarts = await import('echarts')
  }

  paymentMethodChart = echarts.init(chartDom)
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
        data: revenueStats.value.feeItemDistribution?.map(item => item.name) || ['现金', '微信', '支付宝', '银行卡', '其他'],
        axisTick: {
          alignWithLabel: true
        }
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '金额(元)'
      }
    ],
    series: [
      {
        name: '支付方式',
        type: 'bar',
        barWidth: '60%',
        data: revenueStats.value.feeItemDistribution?.map(item => item.value) || [850000, 1200000, 950000, 780000, 320000],
        itemStyle: {
          color: '#E6A23C'
        }
      }
    ]
  }
  paymentMethodChart.setOption(option)
}

// 渲染年度对比图表
const renderYearlyComparisonChart = async () => {
  const chartDom = document.getElementById('yearlyComparisonChart')
  if (!chartDom) return

  if (!echarts) {
    echarts = await import('echarts')
  }

  yearlyComparisonChart = echarts.init(chartDom)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['2023年', '2024年']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: revenueStats.value.yearlyComparison?.map(item => item.month) || ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    },
    yAxis: {
      type: 'value',
      name: '金额(元)'
    },
    series: [
      {
        name: '去年',
        type: 'bar',
        data: revenueStats.value.yearlyComparison?.map(item => item.lastYear) || [280000, 310000, 340000, 370000, 400000, 430000, 460000, 490000, 520000, 550000, 580000, 610000],
        itemStyle: { color: '#909399' }
      },
      {
        name: '今年',
        type: 'bar',
        data: revenueStats.value.yearlyComparison?.map(item => item.currentYear) || [320000, 350000, 380000, 420000, 450000, 480000, 520000, 550000, 580000, 620000, 650000, 680000],
        itemStyle: { color: '#409EFF' }
      }
    ]
  }
  yearlyComparisonChart.setOption(option)
}

// 刷新数据
const refreshData = () => {
  ElMessage.success('数据刷新中...')
  setTimeout(() => {
    renderRevenueTrendChart()
    renderFeeItemChart()
    renderPaymentMethodChart()
    renderYearlyComparisonChart()
    ElMessage.success('数据刷新完成')
  }, 1000)
}

// 导出账单
const exportBills = () => {
  ElMessage.success('账单导出功能开发中...')
}

// 查看账单详情
const viewBillDetail = (row) => {
  ElMessage.info(`查看 ${row.elderName} 的账单详情`)
}

// 标记为已支付
const markAsPaid = (row) => {
  row.status = 1
  row.paidAmount = row.totalAmount
  row.pendingAmount = 0
  ElMessage.success('已标记为已支付')
}

// 渲染所有图表
const renderCharts = async () => {
  setTimeout(async () => {
    await renderRevenueTrendChart()
    await renderFeeItemChart()
    await renderPaymentMethodChart()
    await renderYearlyComparisonChart()
  }, 100)
}

// 窗口大小改变时重新渲染图表
const handleResize = () => {
  if (revenueTrendChart) revenueTrendChart.resize()
  if (feeItemChart) feeItemChart.resize()
  if (paymentMethodChart) paymentMethodChart.resize()
  if (yearlyComparisonChart) yearlyComparisonChart.resize()
}

// 初始化数据
const loadData = async () => {
  console.log('开始加载营收分析数据...')
  console.log('revenueStats初始值:', revenueStats.value)
  try {
    console.log('调用 getRevenueAnalysisData API...')
    const response = await api.dataAnalysis.getRevenueAnalysisData()
    console.log('API响应:', response)

    if (response.data && response.data.code === 200) {
      const data = response.data.data
      console.log('API返回的数据:', data)

      const newRevenueStats = {
        monthlyRevenue: data.monthlyRevenue || 0,
        totalRevenue: data.totalRevenue || 0,
        pendingPayment: data.pendingPayment || 0,
        avgRevenuePerElder: data.averageRevenuePerElder || 0
      }

      console.log('设置新的revenueStats:', newRevenueStats)
      revenueStats.value = newRevenueStats
      console.log('revenueStats.value已更新为:', revenueStats.value)

      // 更新账单列表
      bills.value = data.billList ? data.billList.map(item => ({
        billMonth: item.billMonth || '',
        elderName: item.elderName || '',
        totalAmount: item.totalAmount || 0,
        paidAmount: item.paidAmount || 0,
        pendingAmount: item.pendingAmount || 0,
        status: item.status || '',
        payMethod: item.payMethod || ''
      })) : []

      console.log('bills列表已更新，长度:', bills.value.length)
    } else {
      console.warn('API响应格式不正确:', response.data)
    }
    // 如果API调用失败，继续使用默认的模拟数据
  } catch (error) {
    console.error('获取营收分析数据失败:', error)
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
  if (revenueTrendChart) revenueTrendChart.dispose()
  if (feeItemChart) feeItemChart.dispose()
  if (paymentMethodChart) paymentMethodChart.dispose()
  if (yearlyComparisonChart) yearlyComparisonChart.dispose()
}

defineExpose({ cleanup })
</script>

<style scoped>
.revenue-analysis {
  padding: 20px;
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

.revenue-metrics {
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
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  color: white;
  font-size: 24px;
}

.metric-card.primary .metric-icon {
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
}

.metric-card.success .metric-icon {
  background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%);
}

.metric-card.warning .metric-icon {
  background: linear-gradient(135deg, #E6A23C 0%, #ebb563 100%);
}

.metric-card.info .metric-icon {
  background: linear-gradient(135deg, #909399 0%, #a6a9ad 100%);
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
  margin-bottom: 5px;
}

.metric-change {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.metric-change.positive {
  color: #67C23A;
}

.metric-change.neutral {
  color: #909399;
}

.metric-change.negative {
  color: #F56C6C;
}

.charts-section, .bills-section {
  margin-bottom: 30px;
}

.chart-card, .bills-card {
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

/* 响应式设计 */
@media (max-width: 768px) {
  .revenue-analysis {
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
}
</style>
