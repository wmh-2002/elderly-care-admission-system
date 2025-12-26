<template>
  <div class="elder-analysis">
    <div class="page-header">
      <h2>老人分析</h2>
      <p>老人数据统计与分析</p>
    </div>

    <div class="analysis-content">
      <!-- 老人统计指标 -->
      <div class="elder-metrics">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="metric-card">
              <div class="metric-content">
                <div class="metric-icon">
                  <el-icon><User /></el-icon>
                </div>
                <div class="metric-info">
                  <div class="metric-number">{{ elderStats.totalElders }}</div>
                  <div class="metric-label">总老人数</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="metric-card">
              <div class="metric-content">
                <div class="metric-icon">
                  <el-icon><UserFilled /></el-icon>
                </div>
                <div class="metric-info">
                  <div class="metric-number">{{ elderStats.maleCount }}</div>
                  <div class="metric-label">男性老人</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="metric-card">
              <div class="metric-content">
                <div class="metric-icon">
                  <el-icon><Female /></el-icon>
                </div>
                <div class="metric-info">
                  <div class="metric-number">{{ elderStats.femaleCount }}</div>
                  <div class="metric-label">女性老人</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="metric-card">
              <div class="metric-content">
                <div class="metric-icon">
                  <el-icon><Clock /></el-icon>
                </div>
                <div class="metric-info">
                  <div class="metric-number">{{ elderStats.avgAge }}</div>
                  <div class="metric-label">平均年龄</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 图表区域 -->
      <div class="charts-section">
        <el-row :gutter="20">
          <!-- 性别比例 -->
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>性别比例分布</span>
                  <el-button type="text" size="small" @click="refreshData">刷新</el-button>
                </div>
              </template>
              <div class="chart-container">
                <div id="genderChart" class="chart"></div>
              </div>
            </el-card>
          </el-col>

          <!-- 年龄分布 -->
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>年龄段分布</span>
                  <el-button type="text" size="small" @click="refreshData">刷新</el-button>
                </div>
              </template>
              <div class="chart-container">
                <div id="ageChart" class="chart"></div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="20" style="margin-top: 20px;">
          <!-- 护理等级分布 -->
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>护理等级分布</span>
                  <el-button type="text" size="small" @click="refreshData">刷新</el-button>
                </div>
              </template>
              <div class="chart-container">
                <div id="careLevelChart" class="chart"></div>
              </div>
            </el-card>
          </el-col>

          <!-- 入住时长分布 -->
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>入住时长分布</span>
                  <el-button type="text" size="small" @click="refreshData">刷新</el-button>
                </div>
              </template>
              <div class="chart-container">
                <div id="stayDurationChart" class="chart"></div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 老人列表 -->
      <div class="elder-list-section">
        <el-card class="list-card">
          <template #header>
            <div class="card-header">
              <span>老人详细信息</span>
              <div class="header-actions">
                <el-input
                  v-model="searchText"
                  placeholder="搜索老人姓名或编号"
                  style="width: 200px; margin-right: 10px;"
                  clearable
                />
                <el-select v-model="filterLevel" placeholder="护理等级" style="width: 120px; margin-right: 10px;" clearable>
                  <el-option label="三级护理" value="L1" />
                  <el-option label="二级护理" value="L2" />
                  <el-option label="一级护理" value="L3" />
                </el-select>
                <el-button type="primary" size="small" @click="exportData">导出数据</el-button>
              </div>
            </div>
          </template>
          <el-table :data="filteredElders" stripe style="width: 100%" height="400">
            <el-table-column prop="elderNo" label="老人编号" width="120"></el-table-column>
            <el-table-column prop="name" label="姓名" width="100"></el-table-column>
            <el-table-column prop="gender" label="性别" width="80">
              <template #default="scope">
                <el-tag :type="scope.row.gender === 'M' ? 'primary' : 'danger'">
                  {{ scope.row.gender === 'M' ? '男' : '女' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="age" label="年龄" width="80"></el-table-column>
            <el-table-column prop="careLevel" label="护理等级" width="100">
              <template #default="scope">
                <el-tag :type="getCareLevelTagType(scope.row.careLevel)">
                  {{ getCareLevelText(scope.row.careLevel) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="checkinDate" label="入住日期" width="120"></el-table-column>
            <el-table-column prop="stayDays" label="入住天数" width="100"></el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button type="text" size="small" @click="viewDetail(scope.row)">详情</el-button>
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
import { User, UserFilled, Female, Clock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import api from '@/api'

// 老人统计数据
const elderStats = ref({
  totalElders: 156,
  maleCount: 78,
  femaleCount: 78,
  avgAge: 72
})

// 图表实例
let genderChart = null
let ageChart = null
let careLevelChart = null
let stayDurationChart = null

// 动态导入echarts
let echarts = null

// 搜索和筛选
const searchText = ref('')
const filterLevel = ref('')

// 老人数据
const elders = ref([
  {
    elderNo: 'E001',
    name: '张三',
    gender: 'M',
    age: 68,
    careLevel: 'L1',
    checkinDate: '2023-06-15',
    stayDays: 520
  },
  {
    elderNo: 'E002',
    name: '李四',
    gender: 'F',
    age: 75,
    careLevel: 'L2',
    checkinDate: '2023-08-20',
    stayDays: 430
  },
  {
    elderNo: 'E003',
    name: '王五',
    gender: 'M',
    age: 82,
    careLevel: 'L3',
    checkinDate: '2022-12-10',
    stayDays: 715
  },
  {
    elderNo: 'E004',
    name: '赵六',
    gender: 'F',
    age: 69,
    careLevel: 'L1',
    checkinDate: '2023-09-05',
    stayDays: 385
  },
  {
    elderNo: 'E005',
    name: '孙七',
    gender: 'M',
    age: 76,
    careLevel: 'L2',
    checkinDate: '2023-04-18',
    stayDays: 585
  }
])

// 筛选后的老人数据
const filteredElders = computed(() => {
  return elders.value.filter(elder => {
    const matchesSearch = !searchText.value ||
      elder.name.includes(searchText.value) ||
      elder.elderNo.includes(searchText.value)
    const matchesLevel = !filterLevel.value || elder.careLevel === filterLevel.value
    return matchesSearch && matchesLevel
  })
})

// 获取护理等级标签类型
const getCareLevelTagType = (level) => {
  const types = {
    'L1': 'success',
    'L2': 'warning',
    'L3': 'danger'
  }
  return types[level] || 'info'
}

// 获取护理等级文本
const getCareLevelText = (level) => {
  const texts = {
    'L1': '三级护理',
    'L2': '二级护理',
    'L3': '一级护理'
  }
  return texts[level] || level
}

// 渲染性别比例图表
const renderGenderChart = async () => {
  const chartDom = document.getElementById('genderChart')
  if (!chartDom) return

  if (!echarts) {
    echarts = await import('echarts')
  }

  genderChart = echarts.init(chartDom)
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
        name: '性别比例',
        type: 'pie',
        radius: '50%',
        data: [
          { value: elderStats.value.maleCount, name: '男性' },
          { value: elderStats.value.femaleCount, name: '女性' }
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
  genderChart.setOption(option)
}

// 渲染年龄分布图表
const renderAgeChart = async () => {
  const chartDom = document.getElementById('ageChart')
  if (!chartDom) return

  if (!echarts) {
    echarts = await import('echarts')
  }

  ageChart = echarts.init(chartDom)
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
        data: ['60-69岁', '70-79岁', '80-89岁', '90岁以上'],
        axisTick: {
          alignWithLabel: true
        }
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '人数'
      }
    ],
    series: [
      {
        name: '年龄分布',
        type: 'bar',
        barWidth: '60%',
        data: [
          elderStats.value.ageDistribution?.find(item => item.name === '60-69岁')?.value || 0,
          elderStats.value.ageDistribution?.find(item => item.name === '70-79岁')?.value || 0,
          elderStats.value.ageDistribution?.find(item => item.name === '80-89岁')?.value || 0,
          elderStats.value.ageDistribution?.find(item => item.name === '90岁以上')?.value || 0
        ],
        itemStyle: {
          color: '#67C23A'
        }
      }
    ]
  }
  ageChart.setOption(option)
}

// 渲染护理等级分布图表
const renderCareLevelChart = async () => {
  const chartDom = document.getElementById('careLevelChart')
  if (!chartDom) return

  if (!echarts) {
    echarts = await import('echarts')
  }

  careLevelChart = echarts.init(chartDom)
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
        name: '护理等级',
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
        data: elderStats.value.careLevelDistribution || [
          { value: 0, name: '三级护理' },
          { value: 0, name: '二级护理' },
          { value: 0, name: '一级护理' }
        ]
      }
    ]
  }
  careLevelChart.setOption(option)
}

// 渲染入住时长分布图表
const renderStayDurationChart = async () => {
  const chartDom = document.getElementById('stayDurationChart')
  if (!chartDom) return

  if (!echarts) {
    echarts = await import('echarts')
  }

  stayDurationChart = echarts.init(chartDom)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['入住时长']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['0-6月', '6-12月', '1-2年', '2-3年', '3年以上']
    },
    yAxis: {
      type: 'value',
      name: '人数'
    },
    series: [
      {
        name: '入住时长',
        type: 'line',
        data: [
          elderStats.value.stayDurationDistribution?.find(item => item.name === '0-6月')?.value || 0,
          elderStats.value.stayDurationDistribution?.find(item => item.name === '6-12月')?.value || 0,
          elderStats.value.stayDurationDistribution?.find(item => item.name === '1-2年')?.value || 0,
          elderStats.value.stayDurationDistribution?.find(item => item.name === '2-3年')?.value || 0,
          elderStats.value.stayDurationDistribution?.find(item => item.name === '3年以上')?.value || 0
        ],
        smooth: true,
        itemStyle: {
          color: '#E6A23C'
        },
        areaStyle: {
          color: 'rgba(230, 162, 60, 0.3)'
        }
      }
    ]
  }
  stayDurationChart.setOption(option)
}

// 刷新数据
const refreshData = () => {
  ElMessage.success('数据刷新中...')
  setTimeout(() => {
    renderGenderChart()
    renderAgeChart()
    renderCareLevelChart()
    renderStayDurationChart()
    ElMessage.success('数据刷新完成')
  }, 1000)
}

// 导出数据
const exportData = () => {
  ElMessage.success('数据导出功能开发中...')
}

// 查看详情
const viewDetail = (row) => {
  ElMessage.info(`查看老人 ${row.name} 的详细信息`)
}

// 渲染所有图表
const renderCharts = async () => {
  setTimeout(async () => {
    await renderGenderChart()
    await renderAgeChart()
    await renderCareLevelChart()
    await renderStayDurationChart()
  }, 100)
}

// 窗口大小改变时重新渲染图表
const handleResize = () => {
  if (genderChart) genderChart.resize()
  if (ageChart) ageChart.resize()
  if (careLevelChart) careLevelChart.resize()
  if (stayDurationChart) stayDurationChart.resize()
}

// 初始化数据
const loadData = async () => {
  console.log('开始加载老人分析数据...')
  try {
    console.log('调用 getElderAnalysisData API...')
    const response = await api.dataAnalysis.getElderAnalysisData()
    console.log('API响应:', response)

    if (response.data && response.data.code === 200) {
      const data = response.data.data
      console.log('API返回的数据:', data)

      elderStats.value = {
        totalElders: data.totalElders || 0,
        maleCount: data.maleCount || 0,
        femaleCount: data.femaleCount || 0,
        avgAge: Math.round((data.averageAge || 0) * 10) / 10
      }

      // 更新老人列表
      elders.value = data.elderList ? data.elderList.map(item => ({
        elderNo: item.elderNo || '',
        name: item.name || '',
        gender: item.gender || '',
        age: item.age || 0,
        careLevel: item.careLevel || '',
        checkinDate: item.checkinDate || '',
        stayDays: item.stayDays || 0
      })) : []

      console.log('elderStats已更新:', elderStats.value)
      console.log('elders列表已更新，长度:', elders.value.length)
    } else {
      console.warn('API响应格式不正确:', response.data)
    }
    // 如果API调用失败，继续使用默认的模拟数据
  } catch (error) {
    console.error('获取老人分析数据失败:', error)
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
  if (genderChart) genderChart.dispose()
  if (ageChart) ageChart.dispose()
  if (careLevelChart) careLevelChart.dispose()
  if (stayDurationChart) stayDurationChart.dispose()
}

defineExpose({ cleanup })
</script>

<style scoped>
.elder-analysis {
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

.elder-metrics {
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

.charts-section, .elder-list-section {
  margin-bottom: 30px;
}

.chart-card, .list-card {
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
  .elder-analysis {
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
