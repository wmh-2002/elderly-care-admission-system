<template>
  <div class="room-management-container">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>房间管理</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">房间管理</span>
        </div>
      </template>
      
      <!-- 搜索和筛选区域 -->
      <div class="search-section">
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
          <el-form-item label="房间号">
            <el-input v-model="searchForm.roomNo" placeholder="请输入房间号" :prefix-icon="House" @keyup.enter="onSearch" />
          </el-form-item>
          <el-form-item label="房间类型">
            <el-select v-model="searchForm.roomType" placeholder="请选择房间类型" :prefix-icon="OfficeBuilding" clearable filterable @change="onSearch" style="width: 120px">
              <el-option label="单人间" value="单人间" />
              <el-option label="双人间" value="双人间" />
              <el-option label="多人间" value="多人间" />
            </el-select>
          </el-form-item>
          <el-form-item label="楼层">
            <el-input v-model="searchForm.floor" placeholder="请输入楼层" :prefix-icon="Histogram" @keyup.enter="onSearch" />
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
          <el-button type="primary" size="default" @click="addRoom" :icon="Plus">新增房间</el-button>
          <el-button type="danger" size="default" :disabled="!multipleSelection.length" :icon="Delete">批量删除</el-button>
        </div>
        <div class="button-group-right">
          <el-button size="default" @click="exportTable" :icon="Download">导出</el-button>
          <el-button size="default" @click="toggleView" :icon="Menu">视图</el-button>
        </div>
      </div>
      
      <!-- 数据表格 -->
      <el-table
        :data="roomList"
        style="width: 100%;"
        @selection-change="handleSelectionChange"
        stripe
        border
      >
        <el-table-column type="selection" width="55" fixed="left" />
        <el-table-column prop="roomNo" label="房间号" min-width="100" fixed="left" />
        <el-table-column prop="roomType" label="房间类型" min-width="120" fixed="left" />
        <el-table-column prop="floor" label="楼层" min-width="80" />
        <el-table-column prop="maxBed" label="最大床位数" min-width="120" />
        <!-- <el-table-column prop="occupiedBed" label="已用床位数" min-width="150">
          <template #default="{ row }">
            <el-progress :percentage="Math.round((row.occupiedBed / row.maxBed) * 100)" :show-text="false" :color="getPercentageColor(Math.round((row.occupiedBed / row.maxBed) * 100))" />
            <span>{{ row.occupiedBed }} / {{ row.maxBed }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="120">
          <template #default="{ row }">
            <el-tag :type="row.occupiedBed === 0 ? 'success' : row.occupiedBed < row.maxBed ? 'warning' : 'danger'">
              {{ row.occupiedBed === 0 ? '空闲' : row.occupiedBed < row.maxBed ? '部分占用' : '已满' }}
            </el-tag>
          </template>
        </el-table-column> -->
        <el-table-column label="操作" min-width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="editRoom(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteRoom(row.id)">删除</el-button>
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
      <el-form :model="roomForm" :rules="roomRules" ref="roomFormRef" label-width="100px">
        <el-form-item label="房间号" prop="roomNo">
          <el-input v-model="roomForm.roomNo" :disabled="!!roomForm.id" placeholder="如：101、202" />
        </el-form-item>
        
        <el-form-item label="房间类型" prop="roomType">
          <el-select v-model="roomForm.roomType" placeholder="请选择房间类型" style="width: 100%">
            <el-option label="单人间" value="单人间" />
            <el-option label="双人间" value="双人间" />
            <el-option label="多人间" value="多人间" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="楼层" prop="floor">
          <el-input-number v-model="roomForm.floor" :min="1" :max="20" style="width: 100%" />
        </el-form-item>
        
        <el-form-item label="最大床位数" prop="maxBed">
          <el-input-number v-model="roomForm.maxBed" :min="1" :max="10" style="width: 100%" />
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
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Plus,
  Delete,
  House,
  OfficeBuilding,
  Histogram,
  Download,
  Menu
} from '@element-plus/icons-vue'
import roomAPI from '@/api/room'

// 搜索表单
const searchForm = reactive({
  roomNo: '',
  roomType: '',
  floor: ''
})

// 房间列表数据
const roomList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 选中的行
const multipleSelection = ref([])

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const roomForm = ref({})
const roomFormRef = ref()

// 表单验证规则
const roomRules = {
  roomNo: [
    { required: true, message: '请输入房间号', trigger: 'blur' }
  ],
  roomType: [
    { required: true, message: '请选择房间类型', trigger: 'change' }
  ],
  floor: [
    { required: true, message: '请输入楼层', trigger: 'blur' }
  ],
  maxBed: [
    { required: true, message: '请输入最大床位数', trigger: 'blur' }
  ]
}

// 加载房间列表
const loadRooms = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      roomNo: searchForm.roomNo || undefined,
      roomType: searchForm.roomType || undefined,
      floor: searchForm.floor ? parseInt(searchForm.floor) : undefined
    }

    const response = await roomAPI.getRoomList(params)
    if (response.data.code === 200) {
      roomList.value = response.data.data.content || []
      total.value = response.data.data.totalElements || 0
    } else {
      ElMessage.error(response.data.message || '获取房间列表失败')
    }
  } catch (error) {
    console.error('获取房间列表失败:', error)
    ElMessage.error('获取房间列表失败')
    // 出错时清空数据
    roomList.value = []
    total.value = 0
  }
}

// 获取进度条颜色
const getPercentageColor = (percentage) => {
  if (percentage < 30) {
    return '#67c23a' // 绿色
  } else if (percentage < 70) {
    return '#e6a23c' // 黄色
  } else {
    return '#f56c6c' // 红色
  }
}

// 搜索
const onSearch = () => {
  currentPage.value = 1 // 搜索时重置到第一页
  loadRooms()
}

// 重置
const onReset = () => {
  searchForm.roomNo = ''
  searchForm.roomType = ''
  searchForm.floor = ''
  loadRooms()
}

// 新增房间
const addRoom = () => {
  dialogTitle.value = '新增房间'
  roomForm.value = {
    roomType: '单人间',
    floor: 1,
    maxBed: 1
  }
  dialogVisible.value = true
}

// 编辑房间
const editRoom = (row) => {
  dialogTitle.value = '编辑房间'
  roomForm.value = { ...row }
  dialogVisible.value = true
}

// 查看详情
const viewDetails = (row) => {
  dialogTitle.value = '房间详情'
  roomForm.value = { ...row }
  // 这里可以设置只读模式或者打开详情弹窗
  dialogVisible.value = true
}

// 删除房间
const deleteRoom = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该房间吗？此操作不可恢复。', '删除确认', {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await roomAPI.deleteRoom(id)
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      loadRooms()
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除房间失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleSelectionChange = (val) => {
  multipleSelection.value = val
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  loadRooms()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadRooms()
}

// 提交表单
const submitForm = async () => {
  roomFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let response
        if (roomForm.value.id) {
          // 更新房间
          response = await roomAPI.updateRoom(roomForm.value.id, roomForm.value)
        } else {
          // 创建房间
          response = await roomAPI.createRoom(roomForm.value)
        }

        if (response.data.code === 200) {
          ElMessage.success(response.data.message || (roomForm.value.id ? '更新成功' : '创建成功'))
          dialogVisible.value = false
          loadRooms()
        } else {
          ElMessage.error(response.data.message || (roomForm.value.id ? '更新失败' : '创建失败'))
        }
      } catch (error) {
        console.error('提交表单失败:', error)
        ElMessage.error('操作失败')
      }
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
})
</script>

<style scoped>
.room-management-container {
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