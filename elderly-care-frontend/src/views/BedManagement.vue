<template>
  <div class="bed-management-container">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>床位管理</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">床位管理</span>
        </div>
      </template>
      
      <!-- 搜索和筛选区域 -->
      <div class="search-section">
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
          <el-form-item label="房间号">
            <el-input v-model="searchForm.roomNo" placeholder="请输入房间号" :prefix-icon="House" />
          </el-form-item>
          <el-form-item label="床位号">
            <el-input v-model="searchForm.bedNo" placeholder="请输入床位号" :prefix-icon="List" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" :prefix-icon="CircleCheck">
              <el-option label="空闲" value="0" />
              <el-option label="已入住" value="1" />
              <el-option label="维修" value="2" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onSearch" :icon="Search">查询</el-button>
            <el-button @click="onReset" :icon="Refresh">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 操作按钮区域 -->
      <div class="operation-section">
        <div class="button-group-left">
          <el-button type="primary" size="default" @click="addBed" :icon="Plus">新增床位</el-button>
          <el-button type="danger" size="default" :disabled="!multipleSelection.length" :icon="Delete">批量删除</el-button>
        </div>
        <div class="button-group-right">
          <el-button size="default" @click="exportTable" :icon="Download">导出</el-button>
          <el-button size="default" @click="toggleView" :icon="Menu">视图</el-button>
        </div>
      </div>
      
      <!-- 数据表格 -->
      <el-table
        :data="bedList"
        style="width: 100%;"
        @selection-change="handleSelectionChange"
        stripe
        border
      >
        <el-table-column type="selection" width="55" fixed="left" />
        <el-table-column prop="roomNo" label="房间号" min-width="100" fixed="left" />
        <el-table-column prop="bedNo" label="床位号" min-width="100" fixed="left" />
        <el-table-column prop="elderName" label="入住老人" min-width="120" />
        <el-table-column prop="elderNo" label="老人编号" min-width="120" />
        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkinDate" label="入住日期" min-width="120" />
        <el-table-column label="操作" min-width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="editBed(row)" :disabled="row.status === 1">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteBed(row.id)" :disabled="row.status === 1">删除</el-button>
            <el-button size="small" type="primary" @click="viewDetails(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :background="true"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="bedForm" :rules="bedRules" ref="bedFormRef" label-width="100px">
        <el-form-item label="房间号" prop="roomId">
          <el-select v-model="bedForm.roomId" placeholder="请选择房间" style="width: 100%" :disabled="!!bedForm.id">
            <el-option
              v-for="room in roomOptions"
              :key="room.id"
              :label="room.roomNo"
              :value="room.id"
            >
              {{ room.roomNo }} ({{ room.roomType }}, 楼层: {{ room.floor }})
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="床位号" prop="bedNo">
          <el-input v-model="bedForm.bedNo" placeholder="如：101-1" :disabled="!!bedForm.id" />
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-select v-model="bedForm.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="空闲" :value="0" />
            <el-option label="已入住" :value="1" />
            <el-option label="维修" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button size="default" @click="dialogVisible = false">取消</el-button>
          <el-button size="default" type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Search, 
  Refresh, 
  Plus, 
  Delete, 
  House,
  List,
  CircleCheck,
  Download,
  Menu
} from '@element-plus/icons-vue'

// 搜索表单
const searchForm = reactive({
  roomNo: '',
  bedNo: '',
  status: ''
})

// 床位列表数据
const bedList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 选中的行
const multipleSelection = ref([])

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const bedForm = ref({})
const bedFormRef = ref()

// 房间选项
const roomOptions = ref([])

// 表单验证规则
const bedRules = {
  roomId: [
    { required: true, message: '请选择房间', trigger: 'change' }
  ],
  bedNo: [
    { required: true, message: '请输入床位号', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 模拟数据加载
const loadBeds = () => {
  // 模拟从 API 获取数据
  const mockData = []
  for (let i = 1; i <= 200; i++) {
    const status = Math.floor(Math.random() * 3) // 0: 空闲, 1: 已入住, 2: 维修
    const elderName = status === 1 ? `老人${i}` : ''
    const elderNo = status === 1 ? `ELDER${String(i).padStart(4, '0')}` : ''
    const checkinDate = status === 1 ? `${2023 + Math.floor(Math.random() * 2)}-${String(Math.floor(Math.random() * 12) + 1).padStart(2, '0')}-${String(Math.floor(Math.random() * 28) + 1).padStart(2, '0')}` : ''
    
    mockData.push({
      id: i,
      roomId: Math.floor(i/4) + 1,
      roomNo: `${Math.floor(i/20) + 1}${String((i/4) % 10).padStart(2, '0')}`,
      bedNo: `${Math.floor(i/20) + 1}${String((i/4) % 10).padStart(2, '0')}-${i % 4 + 1}`,
      elderName: elderName,
      elderNo: elderNo,
      status: status,
      checkinDate: checkinDate
    })
  }
  bedList.value = mockData
  total.value = mockData.length
}

// 加载房间选项
const loadRooms = () => {
  // 模拟从 API 获取房间数据
  const mockRooms = []
  for (let i = 1; i <= 50; i++) {
    mockRooms.push({
      id: i,
      roomNo: `${Math.floor(i/10) + 1}${String(i%10).padStart(2, '0')}`,
      roomType: ['单人间', '双人间', '多人间'][Math.floor(Math.random() * 3)],
      floor: Math.floor(i/10) + 1
    })
  }
  roomOptions.value = mockRooms
}

// 获取状态文本
const getStatusText = (status) => {
  switch(status) {
    case 0: return '空闲'
    case 1: return '已入住'
    case 2: return '维修'
    default: return '未知'
  }
}

// 获取状态类型
const getStatusType = (status) => {
  switch(status) {
    case 0: return 'success'
    case 1: return 'primary'
    case 2: return 'warning'
    default: return 'info'
  }
}

// 搜索
const onSearch = () => {
  console.log('Search:', searchForm)
  loadBeds()
}

// 重置
const onReset = () => {
  searchForm.roomNo = ''
  searchForm.bedNo = ''
  searchForm.status = ''
  loadBeds()
}

// 新增床位
const addBed = () => {
  dialogTitle.value = '新增床位'
  bedForm.value = {
    status: 0
  }
  dialogVisible.value = true
}

// 编辑床位
const editBed = (row) => {
  if (row.status === 1) {
    // 已入住房间不可编辑
    return
  }
  
  dialogTitle.value = '编辑床位'
  bedForm.value = { ...row }
  dialogVisible.value = true
}

// 查看详情
const viewDetails = (row) => {
  console.log('View details for:', row)
  dialogTitle.value = '床位详情'
  bedForm.value = { ...row }
  // 禁用表单编辑
}

// 删除床位
const deleteBed = (id) => {
  console.log('Delete bed:', id)
  // 实际应用中会调用API删除
  loadBeds()
}

// 批量删除
const handleSelectionChange = (val) => {
  multipleSelection.value = val
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  loadBeds()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadBeds()
}

// 提交表单
const submitForm = () => {
  bedFormRef.value.validate((valid) => {
    if (valid) {
      console.log('Submit form:', bedForm.value)
      // 实际应用中会调用API提交数据
      dialogVisible.value = false
      loadBeds()
    } else {
      console.log('Validation failed!')
    }
  })
}

// 导出表格数据
const exportTable = () => {
  // 这里可以集成导出功能，如使用xlsx库导出Excel
  console.log('Exporting table data...')
  // 模拟导出功能
  ElMessage.success('表格数据导出功能已触发')
}

// 切换视图模式
const viewMode = ref('table') // 'table' or 'card'
const toggleView = () => {
  viewMode.value = viewMode.value === 'table' ? 'card' : 'table'
  if (viewMode.value === 'card') {
    ElMessage.info('已切换到卡片视图')
  } else {
    ElMessage.info('已切换到表格视图')
  }
}

onMounted(() => {
  loadRooms()
  loadBeds()
})
</script>

<style scoped>
.bed-management-container {
  padding: 0;
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.breadcrumb {
  margin-bottom: 10px;
  padding: 0 10px;
}

.page-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  padding: 2px 8px !important;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
  color: #303133;
}

.search-section {
  margin-bottom: 10px;
}

.operation-section {
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.button-group-left {
  display: flex;
  gap: 10px;
}

.button-group-right {
  display: flex;
  gap: 10px;
}

.pagination-section {
  margin-top: 10px;
  text-align: center;
}

/* 表格样式优化 */
:deep(.el-table) {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

:deep(.el-table th) {
  background-color: #fafafa;
  color: #606266;
  font-weight: 600;
  user-select: none;
}

:deep(.el-table .el-table__row:hover > td) {
  background-color: #f5f7fa;
}

:deep(.el-table th.is-sortable:hover > .cell) {
  background-color: #eeeff3;
}

:deep(.el-table td),
:deep(.el-table th) {
  padding: 8px 0;
}

:deep(.el-table tr) {
  height: 48px;
}

:deep(.el-table .cell) {
  padding: 0 12px;
  word-break: break-word;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

:deep(.el-table .el-table__fixed),
:deep(.el-table .el-table__fixed-right) {
}
</style>