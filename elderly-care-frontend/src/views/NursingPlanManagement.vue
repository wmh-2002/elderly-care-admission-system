<template>
  <div class="nursing-plan-container">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>护理管理</el-breadcrumb-item>
      <el-breadcrumb-item>护理计划管理</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">护理计划管理</span>
        </div>
      </template>
      
      <!-- 搜索和筛选区域 -->
      <div class="search-section">
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
          <el-form-item label="老人姓名">
            <el-input v-model="searchForm.elderName" placeholder="请输入老人姓名" :prefix-icon="User" />
          </el-form-item>
          <el-form-item label="计划状态">
            <el-select v-model="searchForm.status" placeholder="请选择计划状态" :prefix-icon="CircleCheck">
              <el-option label="进行中" value="进行中" />
              <el-option label="已完成" value="已完成" />
              <el-option label="已取消" value="已取消" />
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
          <el-button type="primary" size="default" @click="addPlan" :icon="Plus">新增计划</el-button>
          <el-button type="danger" size="default" :disabled="!multipleSelection.length" :icon="Delete">批量删除</el-button>
        </div>
        <div class="button-group-right">
          <el-button size="default" @click="exportTable" :icon="Download">导出</el-button>
          <el-button size="default" @click="toggleView" :icon="Menu">视图</el-button>
        </div>
      </div>
      
      <!-- 数据表格 -->
      <el-table
        :data="planList"
        style="width: 100%;"
        @selection-change="handleSelectionChange"
        stripe
        border
      >
        <el-table-column type="selection" width="55" fixed="left" />
        <el-table-column prop="id" label="ID" width="80" fixed="left" />
        <el-table-column prop="elderName" label="老人姓名" min-width="120" fixed="left" />
        <el-table-column prop="categoryName" label="护理分类" min-width="120" />
        <el-table-column prop="planName" label="计划名称" min-width="150" />
        <el-table-column prop="startDate" label="开始日期" min-width="120" />
        <el-table-column prop="endDate" label="结束日期" min-width="120" />
        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assignedNurse" label="分配护理员" min-width="120" />
        <el-table-column label="操作" min-width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="editPlan(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deletePlan(row.id)">删除</el-button>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="planForm" :rules="planRules" ref="planFormRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="老人姓名" prop="elderId">
              <el-select 
                v-model="planForm.elderId" 
                placeholder="请选择老人" 
                style="width: 100%" 
                @change="handleElderChange"
              >
                <el-option
                  v-for="elder in elderOptions"
                  :key="elder.id"
                  :label="elder.name"
                  :value="elder.id"
                >
                  {{ elder.name }} ({{ elder.elderNo }})
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="护理分类" prop="categoryId">
              <el-select v-model="planForm.categoryId" placeholder="请选择护理分类" style="width: 100%">
                <el-option
                  v-for="category in categoryOptions"
                  :key="category.id"
                  :label="category.name"
                  :value="category.id"
                >
                  {{ category.name }} ({{ category.level }})
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计划名称" prop="planName">
              <el-input v-model="planForm.planName" placeholder="请输入计划名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分配护理员" prop="nurseId">
              <el-select v-model="planForm.nurseId" placeholder="请选择护理员" style="width: 100%">
                <el-option
                  v-for="nurse in nurseOptions"
                  :key="nurse.id"
                  :label="nurse.realName"
                  :value="nurse.id"
                >
                  {{ nurse.realName }} ({{ nurse.username }})
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                v-model="planForm.startDate"
                type="date"
                placeholder="选择开始日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker
                v-model="planForm.endDate"
                type="date"
                placeholder="选择结束日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="计划描述" prop="description">
          <el-input v-model="planForm.description" type="textarea" :rows="4" placeholder="请输入护理计划的详细描述" />
        </el-form-item>
        
        <el-form-item label="计划状态" prop="status">
          <el-select v-model="planForm.status" placeholder="请选择计划状态" style="width: 100%">
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已取消" value="已取消" />
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
  User, 
  CircleCheck,
  Download,
  Menu
} from '@element-plus/icons-vue'

// 搜索表单
const searchForm = reactive({
  elderName: '',
  status: ''
})

// 计划列表数据
const planList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 选中的行
const multipleSelection = ref([])

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const planForm = ref({})
const planFormRef = ref()

// 选项数据
const elderOptions = ref([])
const categoryOptions = ref([])
const nurseOptions = ref([])

// 表单验证规则
const planRules = {
  elderId: [
    { required: true, message: '请选择老人', trigger: 'change' }
  ],
  categoryId: [
    { required: true, message: '请选择护理分类', trigger: 'change' }
  ],
  planName: [
    { required: true, message: '请输入计划名称', trigger: 'blur' }
  ],
  startDate: [
    { required: true, message: '请选择开始日期', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: '请选择结束日期', trigger: 'change' },
    { 
      validator: (rule, value, callback) => {
        if (planForm.value.startDate && value < planForm.value.startDate) {
          callback(new Error('结束日期不能早于开始日期'))
        } else {
          callback()
        }
      }, 
      trigger: 'change' 
    }
  ],
  status: [
    { required: true, message: '请选择计划状态', trigger: 'change' }
  ]
}

// 模拟数据加载
const loadPlans = () => {
  // 模拟从 API 获取数据
  const mockData = []
  for (let i = 1; i <= 30; i++) {
    const statusOptions = ['进行中', '已完成', '已取消']
    const status = statusOptions[Math.floor(Math.random() * statusOptions.length)]
    
    mockData.push({
      id: i,
      elderId: Math.floor(Math.random() * 50) + 1,
      elderName: `老人${Math.floor(Math.random() * 50) + 1}`,
      categoryName: `护理分类${Math.floor(Math.random() * 5) + 1}`,
      planName: `护理计划-${i}`,
      description: `为老人${Math.floor(Math.random() * 50) + 1}制定的详细护理计划`,
      startDate: `${2023 + Math.floor(Math.random() * 2)}-${String(Math.floor(Math.random() * 12) + 1).padStart(2, '0')}-${String(Math.floor(Math.random() * 28) + 1).padStart(2, '0')}`,
      endDate: `${2023 + Math.floor(Math.random() * 2)}-${String(Math.floor(Math.random() * 12) + 1).padStart(2, '0')}-${String(Math.floor(Math.random() * 28) + 1).padStart(2, '0')}`,
      status: status,
      assignedNurse: `护理员${Math.floor(Math.random() * 10) + 1}`,
      nurseId: Math.floor(Math.random() * 10) + 1
    })
  }
  planList.value = mockData
  total.value = mockData.length
}

// 加载老人选项数据
const loadElders = () => {
  const mockElders = []
  for (let i = 1; i <= 50; i++) {
    mockElders.push({
      id: i,
      name: `老人${i}`,
      elderNo: `ELDER${String(i).padStart(4, '0')}`
    })
  }
  elderOptions.value = mockElders
}

// 加载护理分类选项数据
const loadCategories = () => {
  const mockCategories = []
  for (let i = 1; i <= 10; i++) {
    const levels = ['1级护理', '2级护理', '3级护理', '特级护理']
    const level = levels[Math.floor(Math.random() * levels.length)]
    
    mockCategories.push({
      id: i,
      name: `护理分类${i}`,
      level: level
    })
  }
  categoryOptions.value = mockCategories
}

// 加载护理员选项数据
const loadNurses = () => {
  const mockNurses = []
  for (let i = 1; i <= 20; i++) {
    mockNurses.push({
      id: i,
      realName: `护理员${i}`,
      username: `nurse${i}`
    })
  }
  nurseOptions.value = mockNurses
}

// 获取状态类型
const getStatusType = (status) => {
  switch(status) {
    case '进行中': return 'primary'
    case '已完成': return 'success'
    case '已取消': return 'danger'
    default: return 'info'
  }
}

// 搜索
const onSearch = () => {
  console.log('Search:', searchForm)
  loadPlans()
}

// 重置
const onReset = () => {
  searchForm.elderName = ''
  searchForm.status = ''
  loadPlans()
}

// 新增计划
const addPlan = () => {
  dialogTitle.value = '新增护理计划'
  planForm.value = {
    status: '进行中'
  }
  dialogVisible.value = true
}

// 编辑计划
const editPlan = (row) => {
  dialogTitle.value = '编辑护理计划'
  planForm.value = { ...row }
  dialogVisible.value = true
}

// 查看详情
const viewDetails = (row) => {
  console.log('View details for:', row)
  dialogTitle.value = '护理计划详情'
  planForm.value = { ...row }
  // 禁用表单编辑
}

// 删除计划
const deletePlan = (id) => {
  console.log('Delete plan:', id)
  // 实际应用中会调用API删除
  loadPlans()
}

// 批量删除
const handleSelectionChange = (val) => {
  multipleSelection.value = val
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  loadPlans()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadPlans()
}

// 处理老人选择变化
const handleElderChange = (elderId) => {
  console.log('Selected elder:', elderId)
}

// 提交表单
const submitForm = () => {
  planFormRef.value.validate((valid) => {
    if (valid) {
      console.log('Submit form:', planForm.value)
      // 实际应用中会调用API提交数据
      dialogVisible.value = false
      loadPlans()
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
  loadPlans()
  loadElders()
  loadCategories()
  loadNurses()
})
</script>

<style scoped>
.nursing-plan-container {
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