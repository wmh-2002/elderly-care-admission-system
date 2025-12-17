<template>
  <div class="dashboard">
    <h1>系统概览</h1>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6" v-for="(stat, index) in stats" :key="index">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: stat.color }">
              <el-icon :size="30" color="#fff">
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
    
    <!-- 统计图表组件 -->
    <StatisticsCharts />
    
    <!-- 最近活动 -->
    <el-row :gutter="20" class="activity-row">
      <el-col :span="24">
        <el-card class="activity-card">
          <template #header>
            <div class="card-header">
              <span>最近活动</span>
            </div>
          </template>
          <el-table :data="recentActivities" style="width: 100%">
            <el-table-column prop="time" label="时间" width="180" />
            <el-table-column prop="action" label="操作" />
            <el-table-column prop="user" label="操作人" width="150" />
            <el-table-column prop="description" label="描述" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref } from 'vue'
import { 
  User, 
  House, 
  Bed, 
  Money, 
  UserFilled, 
  ChatLineRound, 
  Edit, 
  InfoFilled 
} from '@element-plus/icons-vue'
import StatisticsCharts from '@/components/StatisticsCharts.vue'

export default {
  name: 'Dashboard',
  components: {
    StatisticsCharts
  },
  setup() {
    // 统计数据
    const stats = ref([
      {
        label: '在院老人数',
        value: 156,
        icon: 'User',
        color: '#409eff'
      },
      {
        label: '房间总数',
        value: 45,
        icon: 'House',
        color: '#67c23a'
      },
      {
        label: '床位总数',
        value: 120,
        icon: 'Bed',
        color: '#e6a23c'
      },
      {
        label: '待缴费金额',
        value: '¥23,456',
        icon: 'Money',
        color: '#f56c6c'
      }
    ])
    
    // 最近活动数据
    const recentActivities = ref([
      { time: '2025-01-15 14:30', action: '新增老人', user: '管理员', description: '张三老人入住登记' },
      { time: '2025-01-15 10:15', action: '护理记录', user: '李护士', description: '为老人王五记录日常护理' },
      { time: '2025-01-14 16:45', action: '费用缴纳', user: '财务', description: '李四老人缴纳本月费用' },
      { time: '2025-01-14 09:20', action: '房间分配', user: '前台', description: '为老人赵六分配房间101' },
      { time: '2025-01-13 15:10', action: '健康评估', user: '医生', description: '为老人孙七进行健康评估' }
    ])
    
    return {
      stats,
      recentActivities
    }
  }
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.dashboard h1 {
  margin-bottom: 20px;
  color: #333;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  height: 100px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.charts-row {
  margin-bottom: 20px;
}

.chart-card {
  height: 400px;
}

.chart-container {
  height: 320px;
  width: 100%;
}

.card-header {
  font-weight: bold;
}

.activity-card {
  min-height: 300px;
}

.activity-row {
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-row .el-col {
    margin-bottom: 15px;
  }
  
  .stats-row .el-col:last-child {
    margin-bottom: 0;
  }
  
  .charts-row {
    flex-direction: column;
  }
  
  .charts-row .el-col {
    margin-bottom: 20px;
  }
  
  .charts-row .el-col:last-child {
    margin-bottom: 0;
  }
}
</style>