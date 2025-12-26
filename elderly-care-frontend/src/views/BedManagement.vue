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
          <el-form-item label="房间">
            <el-select v-model="searchForm.roomId" placeholder="请选择房间" clearable filterable @change="onSearch" style="width: 140px">
              <el-option
                v-for="room in roomOptions"
                :key="room.id"
                :label="`${room.roomNo}(${room.roomType})`"
                :value="room.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="床位号">
            <el-input v-model="searchForm.bedNo" placeholder="请输入床位号" :prefix-icon="List" @keyup.enter="onSearch" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model.number="searchForm.status" placeholder="请选择状态" :prefix-icon="CircleCheck" clearable @change="onSearch" style="width: 120px">
              <el-option label="空闲" :value="0" />
              <el-option label="已入住" :value="1" />
              <el-option label="维修" :value="2" />
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
          <el-button type="danger" size="default" :disabled="!multipleSelection.length" @click="batchDeleteBeds" :icon="Delete">批量删除</el-button>
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
        <el-table-column label="操作" min-width="320" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="editBed(row)" :disabled="row.status === 1">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteBed(row.id)" :disabled="row.status === 1">删除</el-button>
            <el-button size="small" type="primary" @click="viewDetails(row)">详情</el-button>
            <el-dropdown @command="(command) => updateBedStatus(row.id, command)" style="margin-left: 8px;">
              <el-button size="small">
                更新状态
                <el-icon class="el-icon--right">
                  <ArrowDown />
                </el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :disabled="row.status === 0" command="0">设为空闲</el-dropdown-item>
                  <el-dropdown-item :disabled="row.status === 1" command="1">设为已入住</el-dropdown-item>
                  <el-dropdown-item :disabled="row.status === 2" command="2">设为维修</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Plus,
  Delete,
  House,
  List,
  CircleCheck,
  Download,
  Menu,
  ArrowDown
} from '@element-plus/icons-vue'
import api from '@/api'

// 搜索表单
const searchForm = reactive({
  roomId: null,
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

// 加载床位列表
const loadBeds = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      roomId: searchForm.roomId ? parseInt(searchForm.roomId) : undefined,
      bedNo: searchForm.bedNo || undefined,
      status: searchForm.status !== '' ? parseInt(searchForm.status) : undefined
    }

    const response = await api.bed.getBedList(params)
    if (response.data.code === 200) {
      bedList.value = response.data.data.content || []
      total.value = response.data.data.totalElements || 0
    } else {
      ElMessage.error(response.data.message || '获取床位列表失败')
    }
  } catch (error) {
    console.error('获取床位列表失败:', error)
    ElMessage.error('获取床位列表失败')
    // 出错时清空数据
    bedList.value = []
    total.value = 0
  }
}

// 加载房间选项
const loadRooms = async () => {
  try {
    const response = await api.room.getRoomList({ page: 1, size: 1000 }) // 获取所有房间作为选项
    if (response.data.code === 200) {
      roomOptions.value = response.data.data.content || []
    } else {
      ElMessage.error(response.data.message || '获取房间列表失败')
      roomOptions.value = []
    }
  } catch (error) {
    console.error('获取房间列表失败:', error)
    ElMessage.error('获取房间列表失败')
    roomOptions.value = []
  }
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
  currentPage.value = 1 // 搜索时重置到第一页
  loadBeds()
}

// 重置
const onReset = () => {
  searchForm.roomId = null
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
const deleteBed = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该床位吗？此操作不可恢复。', '删除确认', {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await api.bed.deleteBed(id)
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      loadBeds()
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除床位失败:', error)
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
  loadBeds()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadBeds()
}

// 提交表单
const submitForm = async () => {
  bedFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let response
        if (bedForm.value.id) {
          // 更新床位
          response = await api.bed.updateBed(bedForm.value.id, bedForm.value)
        } else {
          // 创建床位
          response = await api.bed.createBed(bedForm.value)
        }

        if (response.data.code === 200) {
          ElMessage.success(response.data.message || (bedForm.value.id ? '更新成功' : '创建成功'))
          dialogVisible.value = false
          loadBeds()
        } else {
          ElMessage.error(response.data.message || (bedForm.value.id ? '更新失败' : '创建失败'))
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

// 更新床位状态
const updateBedStatus = async (bedId, status) => {
  try {
    let response
    const statusNum = parseInt(status)

    switch (statusNum) {
      case 0:
        response = await api.bed.setBedAvailable(bedId)
        break
      case 1:
        response = await api.bed.setBedOccupied(bedId)
        break
      case 2:
        response = await api.bed.setBedMaintenance(bedId)
        break
      default:
        ElMessage.error('无效的状态值')
        return
    }

    if (response.data.code === 200) {
      const statusTexts = ['空闲', '已入住', '维修']
      ElMessage.success(`床位已设为${statusTexts[statusNum]}`)
      loadBeds()
    } else {
      ElMessage.error(response.data.message || '状态更新失败')
    }
  } catch (error) {
    console.error('更新床位状态失败:', error)
    ElMessage.error('状态更新失败')
  }
}

// 批量删除床位
const batchDeleteBeds = async () => {
  if (!multipleSelection.value.length) {
    ElMessage.warning('请先选择要删除的床位')
    return
  }

  try {
    await ElMessageBox.confirm(`确认删除选中的 ${multipleSelection.value.length} 个床位吗？此操作不可恢复。`, '批量删除确认', {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 逐个删除选中的床位
    const deletePromises = multipleSelection.value.map(bed => api.bed.deleteBed(bed.id))
    const results = await Promise.allSettled(deletePromises)

    const successCount = results.filter(result => result.status === 'fulfilled' && result.value.data.code === 200).length
    const failCount = results.length - successCount

    if (successCount > 0) {
      ElMessage.success(`成功删除 ${successCount} 个床位${failCount > 0 ? `，失败 ${failCount} 个` : ''}`)
      loadBeds()
    } else {
      ElMessage.error('批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除床位失败:', error)
      ElMessage.error('批量删除失败')
    }
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