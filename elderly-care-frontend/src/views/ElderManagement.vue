<template>
  <div class="elder-management-container">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>老人档案管理</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">老人档案管理</span>
        </div>
      </template>
      
      <!-- 搜索和筛选区域 -->
      <div class="search-section">
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
          <el-form-item label="姓名">
            <el-input v-model="searchForm.name" placeholder="请输入姓名" :prefix-icon="User" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" :prefix-icon="CircleCheck">
              <el-option label="在院" value="1" />
              <el-option label="退住" value="0" />
            </el-select>
          </el-form-item>
          <el-form-item label="护理等级">
            <el-select v-model="searchForm.careLevel" placeholder="请选择护理等级" :prefix-icon="Management">
              <el-option label="一级护理" value="1" />
              <el-option label="二级护理" value="2" />
              <el-option label="三级护理" value="3" />
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
          <el-button type="primary" size="default" @click="addElder" :icon="Plus">新增老人</el-button>
          <el-button type="danger" size="default" :disabled="!multipleSelection.length" :icon="Delete">批量删除</el-button>
        </div>
        <div class="button-group-right">
          <el-button size="default" @click="exportTable" :icon="Download">导出</el-button>
          <el-button size="default" @click="toggleView" :icon="Menu">视图</el-button>
        </div>
      </div>
      
      <!-- 数据表格 -->
      <el-table
        :data="elderList"
        style="width: 100%;"
        @selection-change="handleSelectionChange"
        stripe
        border
      >
        <el-table-column type="selection" width="55" fixed="left" />
        <el-table-column prop="elderNo" label="老人编号" min-width="120" fixed="left" />
        <el-table-column prop="name" label="姓名" min-width="100" fixed="left" />
        <el-table-column prop="gender" label="性别" min-width="80">
          <template #default="{ row }">
            {{ row.gender === 'M' ? '男' : row.gender === 'F' ? '女' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" min-width="80">
          <template #default="{ row }">
            {{ calculateAge(row.birthDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" min-width="130" />
        <el-table-column prop="roomNo" label="房间号" min-width="100" />
        <el-table-column prop="bedNo" label="床号" min-width="100" />
        <el-table-column prop="careLevel" label="护理等级" min-width="120">
          <template #default="{ row }">
            {{ formatCareLevel(row.careLevel) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '在院' : '退住' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="editElder(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteElder(row.id)">删除</el-button>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="elderForm" :rules="elderRules" ref="elderFormRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="老人编号" prop="elderNo">
              <el-input v-model="elderForm.elderNo" :disabled="!!elderForm.id" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="elderForm.name" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="elderForm.gender">
                <el-radio label="M">男</el-radio>
                <el-radio label="F">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker
                v-model="elderForm.birthDate"
                type="date"
                placeholder="选择日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="elderForm.idCard" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="elderForm.phone" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="家庭地址" prop="address">
          <el-input v-model="elderForm.address" type="textarea" />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房间" prop="roomId">
              <el-select 
                v-model="elderForm.roomId" 
                placeholder="请选择房间" 
                style="width: 100%"
                @change="handleRoomChange"
                :loading="roomLoading"
              >
                <el-option
                  v-for="room in roomList"
                  :key="room.id"
                  :label="room.roomNo"
                  :value="room.id"
                >
                  <span>{{ room.roomNo }}</span>
                  <span style="float: right; color: #8492a6; font-size: 13px">{{ room.roomType }}</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="床位" prop="bedId">
              <el-select 
                v-model="elderForm.bedId" 
                placeholder="请选择床位" 
                style="width: 100%"
                :loading="bedLoading"
                :disabled="!elderForm.roomId"
              >
                <el-option
                  v-for="bed in availableBedList"
                  :key="bed.id"
                  :label="bed.bedNo"
                  :value="bed.id"
                >
                  <span>{{ bed.bedNo }}</span>
                  <span style="float: right; color: #8492a6; font-size: 13px">{{ bed.status === 0 ? '空闲' : '已占用' }}</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="护理等级" prop="careLevel">
              <el-select v-model="elderForm.careLevel" placeholder="请选择护理等级" style="width: 100%">
                <el-option label="一级护理" value="L1" />
                <el-option label="二级护理" value="L2" />
                <el-option label="三级护理" value="L3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="elderForm.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="在院" :value="1" />
                <el-option label="退住" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="备注">
          <el-input v-model="elderForm.remarks" type="textarea" />
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
import { useRouter } from 'vue-router'
import api from '@/api'
import { 
  Search, 
  Refresh, 
  Plus, 
  Delete, 
  User, 
  Management, 
  CircleCheck,
  Download,
  Menu
} from '@element-plus/icons-vue'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  name: '',
  status: '',
  careLevel: ''
})

// 老人列表数据
const elderList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 选中的行
const multipleSelection = ref([])

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const elderForm = ref({})
const elderFormRef = ref()

// 加载状态
const loading = ref(false)

// 房间和床位相关
const roomList = ref([])
const bedList = ref([])
const availableBedList = ref([])
const roomLoading = ref(false)
const bedLoading = ref(false)

// 表单验证规则
const elderRules = {
  elderNo: [
    { required: true, message: '请输入老人编号', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  birthDate: [
    { required: true, message: '请选择出生日期', trigger: 'change' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' }
  ]
}

// 加载老人数据
const loadElders = async () => {
  try {
    loading.value = true
    
    // 构建查询参数
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    
    // 添加筛选条件
    if (searchForm.name) params.name = searchForm.name
    if (searchForm.status) params.status = searchForm.status
    if (searchForm.careLevel) params.careLevel = searchForm.careLevel

    const response = await api.elder.getElderList(params)
    
    if (response.data.code === 200) {
      const data = response.data.data
      // 计算每位老人的年龄并添加到数据中
      elderList.value = (data.content || []).map(elder => ({
        ...elder,
        age: calculateAge(elder.birthDate)
      }))
      total.value = data.total || 0
    } else {
      ElMessage.error(response.data.message || '获取老人数据失败')
    }
  } catch (error) {
    console.error('获取老人数据失败:', error)
    let errorMessage = '获取老人数据失败'
    if (error.response) {
      errorMessage = error.response.data?.message || `错误: ${error.response.status}`
    } else if (error.request) {
      errorMessage = '网络错误，请检查网络连接'
    } else {
      errorMessage = error.message || '未知错误'
    }
    ElMessage.error(errorMessage)
  } finally {
    loading.value = false
  }
}

// 格式化护理等级显示
const formatCareLevel = (level) => {
  switch(level) {
    case 'L1': return '一级护理'
    case 'L2': return '二级护理'
    case 'L3': return '三级护理'
    default: return level
  }
}

// 根据出生日期计算年龄
const calculateAge = (birthDate) => {
  if (!birthDate) return '-'
  
  const birth = new Date(birthDate)
  const today = new Date()
  let age = today.getFullYear() - birth.getFullYear()
  const monthDiff = today.getMonth() - birth.getMonth()
  
  // 如果还没到生日，年龄减1
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
    age--
  }
  
  return age
}

// 搜索
const onSearch = () => {
  currentPage.value = 1
  loadElders()
}

// 重置
const onReset = () => {
  searchForm.name = ''
  searchForm.status = ''
  searchForm.careLevel = ''
  currentPage.value = 1
  loadElders()
}

// 新增老人
const addElder = () => {
  dialogTitle.value = '新增老人'
  elderForm.value = {
    status: 1,
    careLevel: 'L1' // 使用后端标准的护理等级格式
  }
  dialogVisible.value = true
}

// 编辑老人
const editElder = async (row) => {
  try {
    // 获取完整的老人信息用于编辑
    const response = await api.elder.getElderById(row.id)
    if (response.data.code === 200) {
      dialogTitle.value = '编辑老人'
      elderForm.value = { ...response.data.data }
      dialogVisible.value = true
    } else {
      ElMessage.error(response.data.message || '获取老人信息失败')
    }
  } catch (error) {
    console.error('获取老人详细信息失败:', error)
    ElMessage.error('获取老人详细信息失败')
  }
}

// 查看详情
const viewDetails = (row) => {
  // 跳转到老人详情页面
  router.push(`/elders/${row.id}`)
}

// 删除老人
const deleteElder = async (id) => {
  try {
    await ElMessageBox.confirm(
      '此操作将永久删除该老人档案, 是否继续?',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    const response = await api.elder.deleteElder(id)
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      // 重新加载数据
      loadElders()
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') { // 用户取消操作
      console.error('删除老人失败:', error)
      let errorMessage = '删除失败'
      if (error.response) {
        errorMessage = error.response.data?.message || `错误: ${error.response.status}`
      } else if (error.request) {
        errorMessage = '网络错误，请检查网络连接'
      } else {
        errorMessage = error.message || '未知错误'
      }
      ElMessage.error(errorMessage)
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
  currentPage.value = 1
  loadElders()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadElders()
}

// 提交表单（新增或编辑）
const submitForm = async () => {
  elderFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 创建提交数据副本，确保bedId字段正确映射
        const submitData = { ...elderForm.value }
        
        let response
        if (elderForm.value.id) {
          // 编辑现有老人
          response = await api.elder.updateElder(elderForm.value.id, submitData)
        } else {
          // 创建新老人
          response = await api.elder.createElder(submitData)
        }
        
        if (response.data.code === 200) {
          ElMessage.success(elderForm.value.id ? '更新成功' : '新增成功')
          dialogVisible.value = false
          // 重新加载数据
          loadElders()
        } else {
          ElMessage.error(response.data.message || (elderForm.value.id ? '更新失败' : '新增失败'))
        }
      } catch (error) {
        console.error(elderForm.value.id ? '更新老人失败' : '新增老人失败', error)
        let errorMessage = elderForm.value.id ? '更新失败' : '新增失败'
        if (error.response) {
          errorMessage = error.response.data?.message || `错误: ${error.response.status}`
        } else if (error.request) {
          errorMessage = '网络错误，请检查网络连接'
        } else {
          errorMessage = error.message || '未知错误'
        }
        ElMessage.error(errorMessage)
      }
    } else {
      console.log('Validation failed!')
    }
  })
}

// 加载房间列表
const loadRooms = async () => {
  try {
    roomLoading.value = true
    const response = await api.room.getRoomList({
      page: 1,
      size: 100 // 获取所有房间
    })
    
    if (response.data.code === 200) {
      roomList.value = response.data.data.content || []
    } else {
      ElMessage.error(response.data.message || '获取房间列表失败')
    }
  } catch (error) {
    console.error('获取房间列表失败:', error)
    let errorMessage = '获取房间列表失败'
    if (error.response) {
      errorMessage = error.response.data?.message || `错误: ${error.response.status}`
    } else if (error.request) {
      errorMessage = '网络错误，请检查网络连接'
    } else {
      errorMessage = error.message || '未知错误'
    }
    ElMessage.error(errorMessage)
  } finally {
    roomLoading.value = false
  }
}

// 根据房间ID加载可用床位
const loadAvailableBedsByRoom = async (roomId) => {
  if (!roomId) {
    availableBedList.value = []
    return
  }
  
  try {
    bedLoading.value = true
    const response = await api.room.getAvailableBedsByRoomId(roomId)
    
    if (response.data.code === 200) {
      availableBedList.value = response.data.data || [] // The response is a list directly, not paginated

    } else {
      ElMessage.error(response.data.message || '获取床位列表失败')
    }
  } catch (error) {
    console.error('获取床位列表失败:', error)
    let errorMessage = '获取床位列表失败'
    if (error.response) {
      errorMessage = error.response.data?.message || `错误: ${error.response.status}`
    } else if (error.request) {
      errorMessage = '网络错误，请检查网络连接'
    } else {
      errorMessage = error.message || '未知错误'
    }
    ElMessage.error(errorMessage)
  } finally {
    bedLoading.value = false
  }
}

// 处理房间选择变化
const handleRoomChange = (roomId) => {
  // 清空床位选择
  elderForm.value.bedId = undefined
  if (roomId) {
    loadAvailableBedsByRoom(roomId)
  } else {
    availableBedList.value = []
  }
}

// 切换视图模式
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

// 初始化房间列表
const initRoomList = async () => {
  await loadRooms()
}

onMounted(async () => {
  await loadElders()
  await initRoomList()
})
</script>

<style scoped>
.elder-management-container {
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