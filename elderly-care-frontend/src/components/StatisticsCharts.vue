<template>
  <div class="statistics-charts">
    <!-- 入住趋势图 -->
    <el-card class="chart-card">
      <template #header>
        <div class="card-header">
          <span>入住趋势图</span>
        </div>
      </template>
      <div ref="trendChartRef" class="chart-container" />
    </el-card>
    
    <!-- 护理等级分布图 -->
    <el-card class="chart-card">
      <template #header>
        <div class="card-header">
          <span>护理等级分布</span>
        </div>
      </template>
      <div ref="pieChartRef" class="chart-container" />
    </el-card>
    
    <!-- 费用统计图 -->
    <el-card class="chart-card">
      <template #header>
        <div class="card-header">
          <span>月度费用统计</span>
        </div>
      </template>
      <div ref="barChartRef" class="chart-container" />
    </el-card>
    
    <!-- 房间使用率图 -->
    <el-card class="chart-card">
      <template #header>
        <div class="card-header">
          <span>房间使用率</span>
        </div>
      </template>
      <div ref="gaugeChartRef" class="chart-container" />
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'

export default {
  name: 'StatisticsCharts',
  setup() {
    // 图表引用
    const trendChartRef = ref(null)
    const pieChartRef = ref(null)
    const barChartRef = ref(null)
    const gaugeChartRef = ref(null)
    
    let trendChart = null
    let pieChart = null
    let barChart = null
    let gaugeChart = null
    
    // 初始化图表
    const initCharts = () => {
      // 入住趋势图
      trendChart = echarts.init(trendChartRef.value)
      const trendOption = {
        title: {
          text: '近6个月入住趋势',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['新增入住', '退住'],
          top: 30
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: ['7月', '8月', '9月', '10月', '11月', '12月']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '新增入住',
            type: 'line',
            stack: '总量',
            data: [12, 18, 15, 20, 22, 19]
          },
          {
            name: '退住',
            type: 'line',
            stack: '总量',
            data: [3, 2, 4, 3, 5, 2]
          }
        ]
      }
      trendChart.setOption(trendOption)
      
      // 护理等级分布饼图
      pieChart = echarts.init(pieChartRef.value)
      const pieOption = {
        title: {
          text: '护理等级分布',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '护理等级',
            type: 'pie',
            radius: '50%',
            data: [
              { value: 35, name: '三级护理' },
              { value: 45, name: '二级护理' },
              { value: 30, name: '一级护理' },
              { value: 46, name: '特级护理' }
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
      pieChart.setOption(pieOption)
      
      // 月度费用统计柱状图
      barChart = echarts.init(barChartRef.value)
      const barOption = {
        title: {
          text: '月度费用统计',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: ['住宿费', '护理费', '餐饮费', '其他费用'],
          top: 30
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'value'
        },
        yAxis: {
          type: 'category',
          data: ['7月', '8月', '9月', '10月', '11月', '12月']
        },
        series: [
          {
            name: '住宿费',
            type: 'bar',
            stack: 'total',
            label: {
              show: true
            },
            emphasis: {
              focus: 'series'
            },
            data: [320, 302, 301, 334, 390, 330]
          },
          {
            name: '护理费',
            type: 'bar',
            stack: 'total',
            label: {
              show: true
            },
            emphasis: {
              focus: 'series'
            },
            data: [120, 132, 101, 134, 90, 230]
          },
          {
            name: '餐饮费',
            type: 'bar',
            stack: 'total',
            label: {
              show: true
            },
            emphasis: {
              focus: 'series'
            },
            data: [220, 182, 191, 234, 290, 330]
          },
          {
            name: '其他费用',
            type: 'bar',
            stack: 'total',
            label: {
              show: true
            },
            emphasis: {
              focus: 'series'
            },
            data: [80, 92, 71, 84, 90, 130]
          }
        ]
      }
      barChart.setOption(barOption)
      
      // 房间使用率仪表图
      gaugeChart = echarts.init(gaugeChartRef.value)
      const gaugeOption = {
        title: {
          text: '房间使用率',
          left: 'center'
        },
        series: [
          {
            name: '使用率',
            type: 'gauge',
            progress: {
              show: true
            },
            axisLine: {
              lineStyle: {
                width: 30
              }
            },
            axisTick: {
              show: false
            },
            splitLine: {
              length: 15,
              lineStyle: {
                width: 2,
                color: '#999'
              }
            },
            axisLabel: {
              distance: 25,
              color: '#999',
              fontSize: 15
            },
            anchor: {
              show: true,
              showAbove: true,
              size: 20,
              itemStyle: {
                borderWidth: 10
              }
            },
            pointer: {
              itemStyle: {
                color: '#C0C4CC'
              }
            },
            title: {
              show: false
            },
            detail: {
              valueAnimation: true,
              fontSize: 30,
              offsetCenter: [0, '80%'],
              formatter: '{value}%',
              color: 'inherit'
            },
            data: [
              {
                value: 70,
                name: '使用率',
                title: {
                  offsetCenter: ['0%', '-40%']
                },
                detail: {
                  valueAnimation: true,
                  offsetCenter: ['0%', '-20%']
                }
              }
            ]
          }
        ]
      }
      gaugeChart.setOption(gaugeOption)
    }
    
    // 响应式处理
    const resizeCharts = () => {
      if (trendChart) trendChart.resize()
      if (pieChart) pieChart.resize()
      if (barChart) barChart.resize()
      if (gaugeChart) gaugeChart.resize()
    }
    
    // 组件挂载后初始化
    onMounted(() => {
      // 确保DOM元素已经渲染完成
      setTimeout(() => {
        initCharts()
        window.addEventListener('resize', resizeCharts)
      }, 100)
    })
    
    // 组件卸载前清理
    onUnmounted(() => {
      if (trendChart) trendChart.dispose()
      if (pieChart) pieChart.dispose()
      if (barChart) barChart.dispose()
      if (gaugeChart) gaugeChart.dispose()
      window.removeEventListener('resize', resizeCharts)
    })
    
    return {
      trendChartRef,
      pieChartRef,
      barChartRef,
      gaugeChartRef
    }
  }
}
</script>

<style scoped>
.statistics-charts {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  padding: 20px 0;
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

@media (max-width: 768px) {
  .statistics-charts {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .chart-card {
    height: 350px;
  }
  
  .chart-container {
    height: 270px;
  }
}
</style>