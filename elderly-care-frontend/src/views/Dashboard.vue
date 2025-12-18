<template>
  <div class="dashboard-container">
    <!-- 欢迎信息 -->
    <div class="welcome-section">
      <h1>欢迎回来，{{ username }}！</h1>
      <p>今天是 {{ currentDate }}，您有 {{ notificationCount }} 条未读消息</p>
    </div>
    
    <!-- 统计卡片区域 -->
    <el-row :gutter="20" class="dashboard-stats">
      <el-col :span="6" v-for="(stat, index) in statsData" :key="index">
        <el-card class="box-card stat-card" :class="stat.cardClass">
          <div class="stat-item">
            <div class="stat-icon" :style="{ backgroundColor: stat.color + '20' }">
              <el-icon :size="30" :color="stat.color">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 主要内容区域 -->
    <el-row :gutter="20" class="dashboard-content">
      <!-- 左侧 - 统计卡片和图表 -->
      <el-col :span="16">
        <!-- 统计卡片行2 -->
        <el-row :gutter="20" class="stats-row2">
          <el-col :span="8">
            <el-card class="box-card stat-card2">
              <div class="stat-item2">
                <div class="stat-icon2" style="background-color: rgba(255, 102, 102, 0.2); color: #ff6666;">
                  <el-icon :size="30"><Present /></el-icon>
                </div>
                <div class="stat-info2">
                  <div class="stat-number2">24</div>
                  <div class="stat-label2">今日活动</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card class="box-card stat-card2">
              <div class="stat-item2">
                <div class="stat-icon2" style="background-color: rgba(102, 204, 255, 0.2); color: #66ccff;">
                  <el-icon :size="30"><Phone /></el-icon>
                </div>
                <div class="stat-info2">
                  <div class="stat-number2">8</div>
                  <div class="stat-label2">家属探访</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card class="box-card stat-card2">
              <div class="stat-item2">
                <div class="stat-icon2" style="background-color: rgba(102, 255, 153, 0.2); color: #66ff99;">
                  <el-icon :size="30"><Document /></el-icon>
                </div>
                <div class="stat-info2">
                  <div class="stat-number2">5</div>
                  <div class="stat-label2">待处理事项</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        
        <!-- 图表区域 -->
        <el-row :gutter="20" class="charts-row">
          <el-col :span="12">
            <el-card class="box-card chart-card">
              <template #header>
                <div class="card-header">
                  <span>护理等级分布</span>
                </div>
              </template>
              <div id="careLevelChart" class="chart-container" style="height: 300px;"></div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card class="box-card chart-card">
              <template #header>
                <div class="card-header">
                  <span>房间入住情况</span>
                </div>
              </template>
              <div id="roomOccupancyChart" class="chart-container" style="height: 300px;"></div>
            </el-card>
          </el-col>
        </el-row>
        
        <!-- 最新动态（缩小版） -->
        <el-card class="box-card news-card-small">
          <template #header>
            <div class="card-header">
              <span>最新动态</span>
              <el-button type="text" @click="viewMore('news')">查看更多</el-button>
            </div>
          </template>
          <div class="news-list-small">
            <div 
              v-for="(item, index) in newsList" 
              :key="index"
              class="news-item-small"
            >
              <div class="news-icon-small" :class="'news-' + item.type">
                <el-icon><component :is="item.icon" /></el-icon>
              </div>
              <div class="news-content-small">
                <div class="news-title">{{ item.title }}</div>
                <div class="news-desc">{{ item.content }}</div>
              </div>
              <div class="news-time">{{ item.timestamp }}</div>
            </div>
          </div>
        </el-card>
        
        
      </el-col>
      
      <!-- 右侧 - 快速操作和待办事项 -->
      <el-col :span="8">
        <!-- 快速操作 -->
        <el-card class="box-card quick-actions-card">
          <template #header>
            <div class="card-header">
              <span>快速操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <div 
              v-for="(action, index) in quickActions" 
              :key="index"
              class="action-item"
              @click="goToPage(action.route)"
            >
              <div class="action-icon" :class="'action-' + action.type">
                <el-icon :size="24" :color="action.color">
                  <component :is="action.icon" />
                </el-icon>
              </div>
              <div class="action-text">{{ action.label }}</div>
            </div>
          </div>
        </el-card>
        
        
        
        <!-- 入住率图表 -->
        <el-card class="box-card occupancy-card">
          <template #header>
            <div class="card-header">
              <span>入住率统计</span>
              <el-button type="text" @click="viewMore('occupancy')">详情</el-button>
            </div>
          </template>
          <div class="occupancy-chart">
            <el-progress 
              type="dashboard" 
              :percentage="occupancyRate" 
              :color="occupancyColor"
              :width="140"
            />
            <div class="occupancy-text">
              <p>总体入住率</p>
              <h3>{{ occupancyRate }}%</h3>
            </div>
          </div>
        </el-card>
        
        </el-col>
    </el-row>
    
    
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { 
  User, OfficeBuilding, House, Avatar, 
  Bell, Check, Calendar, Document,
  ChatLineRound, Phone, Clock, Present
} from '@element-plus/icons-vue'

// 用户信息
const username = ref('管理员')
const currentDate = ref('')
const notificationCount = ref(3)

// 统计数据
const statsData = ref([
  { 
    value: 128, 
    label: '在院老人', 
    icon: 'User', 
    color: '#67C23A',
    cardClass: 'elder-card'
  },
  { 
    value: 86, 
    label: '房间总数', 
    icon: 'OfficeBuilding', 
    color: '#409EFF',
    cardClass: 'room-card'
  },
  { 
    value: 12, 
    label: '空闲床位', 
    icon: 'House', 
    color: '#E6A23C',
    cardClass: 'bed-card'
  },
  { 
    value: 24, 
    label: '员工总数', 
    icon: 'Avatar', 
    color: '#F56C6C',
    cardClass: 'staff-card'
  }
])

// 最新动态
const newsList = ref([
  {
    title: '新老人入住',
    content: '张三老人于今日成功办理入住手续，已分配至301房间',
    timestamp: '2023/4/17 20:46',
    type: 'success',
    icon: 'Check'
  },
  {
    title: '健康检查提醒',
    content: '提醒护理员明天为李四老人进行每周例行健康检查',
    timestamp: '2023/4/18 15:30',
    type: 'warning',
    icon: 'Bell'
  },
  {
    title: '房间维护完成',
    content: '201房间空调维护工作已完成，老人已返回房间',
    timestamp: '2023/4/18 20:46',
    type: 'info',
    icon: 'Document'
  },
  {
    title: '家属探访',
    content: '王五老人的家属将于明天下午来院探访',
    timestamp: '2023/4/19 10:15',
    type: 'primary',
    icon: 'Calendar'
  }
])



// 快速操作
const quickActions = ref([
  { label: '新增老人档案', icon: 'User', type: 'primary', route: '/elders', color: '#409EFF' },
  { label: '房间管理', icon: 'OfficeBuilding', type: 'success', route: '/rooms', color: '#67C23A' },
  { label: '床位分配', icon: 'House', type: 'warning', route: '/beds', color: '#E6A23C' },
  { label: '员工管理', icon: 'Avatar', type: 'info', route: '/users', color: '#909399' }
])



// 入住率
const occupancyRate = ref(85.3)

// 入住率颜色
const occupancyColor = ref([
  { color: '#67C23A', percentage: 70 },
  { color: '#E6A23C', percentage: 85 },
  { color: '#F56C6C', percentage: 100 }
])

// 格式化当前日期
const formatDate = () => {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const weekdays = ['日', '一', '二', '三', '四', '五', '六']
  const weekday = weekdays[date.getDay()]
  
  currentDate.value = `${year}年${month}月${day}日 星期${weekday}`
}

// 跳转页面
const goToPage = (route) => {
  console.log('Go to:', route)
}

// 查看更多
const viewMore = (type) => {
  console.log('View more:', type)
}

// 图表实例
let careLevelChart = null
let roomOccupancyChart = null

onMounted(async () => {
  formatDate()
  
  // 动态导入ECharts
  const echarts = await import('echarts')
  
  // 初始化护理等级分布图表
  if (document.getElementById('careLevelChart')) {
    careLevelChart = echarts.init(document.getElementById('careLevelChart'))
    const careLevelOption = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c}人 ({d}%)'
      },
      legend: {
        orient: 'horizontal',
        left: 'center',
        bottom: '10',
        textStyle: {
          fontSize: 12
        }
      },
      series: [
        {
          name: '护理等级',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 8,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: true,
            formatter: '{b}: {c}人'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '14',
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: true
          },
          data: [
            { value: 35, name: '一级护理', itemStyle: { color: '#F56C6C' } },
            { value: 58, name: '二级护理', itemStyle: { color: '#E6A23C' } },
            { value: 35, name: '三级护理', itemStyle: { color: '#67C23A' } }
          ]
        }
      ]
    }
    careLevelChart.setOption(careLevelOption)
  }
  
  // 初始化房间入住情况图表
  if (document.getElementById('roomOccupancyChart')) {
    roomOccupancyChart = echarts.init(document.getElementById('roomOccupancyChart'))
    const roomOccupancyOption = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      legend: {
        data: ['已入住', '空闲'],
        top: '10'
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '10%',
        top: '15%',
        containLabel: true
      },
      xAxis: {
        type: 'value',
        boundaryGap: [0, 0.01]
      },
      yAxis: {
        type: 'category',
        data: ['单人间', '双人间', '多人间']
      },
      series: [
        {
          name: '已入住',
          type: 'bar',
          stack: 'total',
          emphasis: {
            focus: 'series'
          },
          data: [8, 15, 21],
          itemStyle: { color: '#409EFF' }
        },
        {
          name: '空闲',
          type: 'bar',
          stack: 'total',
          emphasis: {
            focus: 'series'
          },
          data: [2, 5, 9],
          itemStyle: { color: '#909399' }
        }
      ]
    }
    roomOccupancyChart.setOption(roomOccupancyOption)
  }
  
  // 处理窗口大小变化
  const handleResize = () => {
    if (careLevelChart) careLevelChart.resize()
    if (roomOccupancyChart) roomOccupancyChart.resize()
  }
  
  window.addEventListener('resize', handleResize)
  
  // 组件卸载时清理
  onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
    if (careLevelChart) careLevelChart.dispose()
    if (roomOccupancyChart) roomOccupancyChart.dispose()
  })
})
</script>

<style scoped>
.dashboard-container {
  background-color: #f5f7fa;
  min-height: 100%;
}

.welcome-section {
  margin-bottom: 24px;
}

.welcome-section h1 {
  margin: 0;
  font-size: 1.8rem;
  color: #303133;
  font-weight: 500;
}

.welcome-section p {
  margin: 8px 0 0 0;
  color: #909399;
  font-size: 1rem;
}

.dashboard-stats {
  margin-bottom: 24px;
}

.stat-card {
  height: 120px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.12);
}

.stat-item {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 15px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-info {
  flex: 1;
  text-align: center;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.dashboard-content {
  margin-top: 20px;
}

.stats-row2 {
  margin-bottom: 20px;
}

.stat-card2 {
  height: 100px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.stat-card2:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px rgba(0,0,0,0.1);
}

.stat-item2 {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 15px;
}

.stat-icon2 {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  flex-shrink: 0;
}

.stat-info2 {
  flex: 1;
  text-align: left;
}

.stat-number2 {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 2px;
}

.stat-label2 {
  font-size: 13px;
  color: #909399;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 500;
}

.news-card-small, .quick-actions-card, .occupancy-card, .chart-card {
  margin-bottom: 20px;
}

.charts-row {
  margin-bottom: 20px;
}

.chart-container {
  width: 100%;
  position: relative;
}

.news-card-small {
  min-height: auto;
}

.news-list-small {
  max-height: 200px;
  overflow-y: auto;
}

.dashboard-bottom {
  margin-top: 20px;
}

.news-card-bottom {
  min-height: auto;
}

.news-list-bottom {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  padding: 10px 0;
}

.news-item-bottom {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background-color: #fafafa;
  flex: 1;
  min-width: 300px;
  transition: all 0.3s ease;
}

.news-item-bottom:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
  border-color: #dcdfe6;
}

.news-icon-bottom {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  flex-shrink: 0;
}

.news-icon-bottom.news-success {
  background-color: rgba(103, 194, 58, 0.1);
  color: #67C23A;
}

.news-icon-bottom.news-warning {
  background-color: rgba(230, 162, 60, 0.1);
  color: #E6A23C;
}

.news-icon-bottom.news-info {
  background-color: rgba(144, 147, 153, 0.1);
  color: #909399;
}

.news-icon-bottom.news-primary {
  background-color: rgba(64, 158, 255, 0.1);
  color: #409EFF;
}

.news-icon-bottom.news-danger {
  background-color: rgba(245, 108, 108, 0.1);
  color: #F56C6C;
}

.news-content-bottom {
  flex: 1;
  min-width: 0;
}

.news-title {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
  font-size: 14px;
}

.news-desc {
  color: #909399;
  font-size: 12px;
  line-height: 1.4;
}

.news-time {
  font-size: 12px;
  color: #909399;
  flex-shrink: 0;
  margin-left: 10px;
}

.news-item-small {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.news-item-small:last-child {
  border-bottom: none;
}

.news-icon-small {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  flex-shrink: 0;
}

.news-icon-small.news-success {
  background-color: rgba(103, 194, 58, 0.1);
  color: #67C23A;
}

.news-icon-small.news-warning {
  background-color: rgba(230, 162, 60, 0.1);
  color: #E6A23C;
}

.news-icon-small.news-info {
  background-color: rgba(144, 147, 153, 0.1);
  color: #909399;
}

.news-icon-small.news-primary {
  background-color: rgba(64, 158, 255, 0.1);
  color: #409EFF;
}

.news-icon-small.news-danger {
  background-color: rgba(245, 108, 108, 0.1);
  color: #F56C6C;
}

.news-content-small {
  flex: 1;
  min-width: 0;
}

.news-title {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
  font-size: 14px;
}

.news-desc {
  color: #909399;
  font-size: 12px;
  line-height: 1.4;
}

.news-time {
  font-size: 12px;
  color: #909399;
  flex-shrink: 0;
  margin-left: 10px;
}

.health-list {
  min-height: 200px;
}

.elder-info {
  display: flex;
  align-items: center;
}

.elder-info span {
  margin-left: 8px;
}

.reminder-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.reminder-time {
  color: #909399;
  font-size: 0.9rem;
}

.quick-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  padding: 10px 0;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 10px;
  border-radius: 12px;
  background: #f8f9fa;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
  border: 1px solid #ebeef5;
}

.action-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
  background: #ffffff;
  border-color: #dcdfe6;
}

.action-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  transition: all 0.3s ease;
}

.action-icon.action-primary {
  background-color: rgba(64, 158, 255, 0.1);
}

.action-icon.action-success {
  background-color: rgba(103, 194, 58, 0.1);
}

.action-icon.action-warning {
  background-color: rgba(230, 162, 60, 0.1);
}

.action-icon.action-info {
  background-color: rgba(144, 147, 153, 0.1);
}

.action-item:hover .action-icon {
  transform: scale(1.1);
}

.action-text {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.todo-list {
  min-height: 200px;
}

.todo-item {
  margin-bottom: 12px;
  padding: 8px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.todo-item:hover {
  background-color: #f5f7fa;
}

.todo-item.completed .todo-content {
  opacity: 0.6;
}

.todo-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  width: 100%;
}

.todo-text {
  flex: 1;
}

.todo-text .todo-desc {
  font-size: 0.85rem;
  color: #909399;
  margin-top: 4px;
}

.todo-time {
  color: #909399;
  font-size: 0.9rem;
  margin-left: 10px;
  flex-shrink: 0;
}

.occupancy-chart {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.occupancy-text {
  text-align: center;
  margin-top: 10px;
}

.occupancy-text p {
  margin: 5px 0;
  color: #909399;
}

.occupancy-text h3 {
  margin: 0;
  font-size: 1.5rem;
  color: #303133;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .dashboard-stats {
    :deep(.el-col) {
      margin-bottom: 16px;
    }
  }
  
  .dashboard-content {
    flex-direction: column;
  }
  
  .dashboard-content :deep(.el-col) {
    margin-bottom: 20px;
  }
}
</style>