<template>
  <div class="nursing-category-container">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>护理管理</el-breadcrumb-item>
      <el-breadcrumb-item>护理分类管理</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">护理分类管理</span>
        </div>
      </template>
      
      <!-- 搜索和筛选区域 -->
      <div class="search-section">
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
          <el-form-item label="分类名称">
            <el-input v-model="searchForm.name" placeholder="请输入分类名称" :prefix-icon="Document" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" :prefix-icon="CircleCheck">
              <el-option label="启用" value="1" />
              <el-option label="禁用" value="0" />
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
          <el-button type="primary" size="default" @click="addCategory" :icon="Plus">新增分类</el-button>
          <el-button type="danger" size="default" :disabled="!multipleSelection.length" :icon="Delete">批量删除</el-button>
        </div>
        <div class="button-group-right">
          <el-button size="default" @click="exportTable" :icon="Download">导出</el-button>
          <el-button size="default" @click="toggleView" :icon="Menu">视图</el-button>
        </div>
      </div>
      
      <!-- 数据表格 -->
      <el-table
        :data="categoryList"
        style="width: 100%;"
        @selection-change="handleSelectionChange"
        stripe
        border
      >
        <el-table-column type="selection" width="55" fixed="left" />
        <el-table-column prop="id" label="ID" width="80" fixed="left" />
        <el-table-column prop="name" label="分类名称" min-width="120" />
        <el-table-column prop="description" label="分类描述" min-width="200" />
        <el-table-column prop="level" label="护理等级" min-width="120">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)">
              {{ getLevelText(row.level) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="150" />
        <el-table-column label="操作" min-width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="editCategory(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteCategory(row.id)">删除</el-button>
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
      <el-form :model="categoryForm" :rules="categoryRules" ref="categoryFormRef" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" :disabled="!!categoryForm.id" />
        </el-form-item>
        
        <el-form-item label="护理等级" prop="level">
          <el-select v-model="categoryForm.level" placeholder="请选择护理等级" style="width: 100%">
            <el-option label="一级护理" value="1级护理" />
            <el-option label="二级护理" value="2级护理" />
            <el-option label="三级护理" value="3级护理" />
            <el-option label="特级护理" value="特级护理" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="分类描述" prop="description">
          <el-input v-model="categoryForm.description" type="textarea" :rows="3" placeholder="请输入分类描述" />
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-select v-model="categoryForm.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
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
  Document, 
  CircleCheck,
  Download,
  Menu
} from '@element-plus/icons-vue'

// 搜索表单
const searchForm = reactive({
  name: '',
  status: ''
})

// 分类列表数据
const categoryList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 选中的行
const multipleSelection = ref([])

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const categoryForm = ref({})
const categoryFormRef = ref()

// 表单验证规则
const categoryRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 20, message: '分类名称长度应在2到20个字符之间', trigger: 'blur' }
  ],
  level: [
    { required: true, message: '请选择护理等级', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 模拟数据加载
const loadCategories = () => {
  // 模拟从 API 获取数据
  const mockData = []
  for (let i = 1; i <= 20; i++) {
    const levels = ['1级护理', '2级护理', '3级护理', '特级护理']
    const level = levels[Math.floor(Math.random() * levels.length)]
    
    mockData.push({
      id: i,
      name: `护理分类${i}`,
      description: `这是第${i}类护理服务的描述，包含相关的护理项目和要求`,
      level: level,
      status: Math.random() > 0.2 ? 1 : 0, // 80% 概率启用
      createTime: `${2022 + Math.floor(Math.random() * 3)}-${String(Math.floor(Math.random() * 12) + 1).padStart(2, '0')}-${String(Math.floor(Math.random() * 28) + 1).padStart(2, '0')}`
    })
  }
  categoryList.value = mockData
  total.value = mockData.length
}

// 获取等级类型
const getLevelType = (level) => {
  switch(level) {
    case '1级护理': return 'primary'
    case '2级护理': return 'success'
    case '3级护理': return 'warning'
    case '特级护理': return 'danger'
    default: return 'info'
  }
}

// 获取等级文本
const getLevelText = (level) => {
  switch(level) {
    case '1级护理': return '一级护理'
    case '2级护理': return '二级护理'
    case '3级护理': return '三级护理'
    case '特级护理': return '特级护理'
    default: return level
  }
}

// 搜索
const onSearch = () => {
  console.log('Search:', searchForm)
  loadCategories()
}

// 重置
const onReset = () => {
  searchForm.name = ''
  searchForm.status = ''
  loadCategories()
}

// 新增分类
const addCategory = () => {
  dialogTitle.value = '新增护理分类'
  categoryForm.value = {
    level: '1级护理',
    status: 1
  }
  dialogVisible.value = true
}

// 编辑分类
const editCategory = (row) => {
  dialogTitle.value = '编辑护理分类'
  categoryForm.value = { ...row }
  dialogVisible.value = true
}

// 查看详情
const viewDetails = (row) => {
  console.log('View details for:', row)
  dialogTitle.value = '护理分类详情'
  categoryForm.value = { ...row }
  // 禁用表单编辑
}

// 删除分类
const deleteCategory = (id) => {
  console.log('Delete category:', id)
  // 实际应用中会调用API删除
  loadCategories()
}

// 批量删除
const handleSelectionChange = (val) => {
  multipleSelection.value = val
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  loadCategories()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadCategories()
}

// 提交表单
const submitForm = () => {
  categoryFormRef.value.validate((valid) => {
    if (valid) {
      console.log('Submit form:', categoryForm.value)
      // 实际应用中会调用API提交数据
      dialogVisible.value = false
      loadCategories()
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
  loadCategories()
})
</script>

<style scoped>
.nursing-category-container {
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