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
        <el-table-column prop="levelCode" label="护理等级" width="120" fixed="left" />
        <el-table-column prop="levelName" label="护理名称" min-width="120" />
        <el-table-column prop="description" label="护理描述" min-width="200" />
        <el-table-column prop="dailyPrice" label="标准价钱" min-width="120">
          <template #default="{ row }">
            <el-tag>
              {{row.dailyPrice}} ￥
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="150" />
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
        <el-form-item label="护理名称" prop="levelName">
          <el-input v-model="categoryForm.levelName" placeholder="请输入分类名称" :disabled="!!categoryForm.id" />
        </el-form-item>
        
        <el-form-item label="护理等级" prop="levelCode">
          <el-select v-model="categoryForm.levelCode" placeholder="请选择护理等级" style="width: 100%">
            <el-option label="一级护理" value="L1" />
            <el-option label="二级护理" value="L2" />
            <el-option label="三级护理" value="L3" />
            <el-option label="特级护理" value="L0" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="标准价钱" prop="dailyPrice">
          <el-input v-model="categoryForm.dailyPrice" placeholder="请输入标准价钱" />
        </el-form-item>
        <el-form-item label="分类描述" prop="description">
          <el-input v-model="categoryForm.description" type="textarea" :rows="3" placeholder="请输入分类描述" />
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
import api from '@/api'
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

// 加载状态
const loading = ref(false)

// 选中的行
const multipleSelection = ref([])

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const categoryForm = ref({})
const categoryFormRef = ref()

// 表单验证规则
const categoryRules = {
  levelCode: [
    { required: true, message: '请选择护理等级', trigger: 'blur' }
  ],
  levelName: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ]
}

// 加载护理分类数据
const loadCategories = async () => {
  try {
    loading.value = true
    
    // 构建查询参数
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    
    // 添加筛选条件
    if (searchForm.name) params.levelName = searchForm.name  // 后端使用 levelCode 字段
    if (searchForm.status) params.status = searchForm.status

    const response = await api.nursingCategory.getNursingCategoryList(params)
    
    if (response.data.code === 200) {
      const data = response.data.data
      categoryList.value = data.content || []
      total.value = data.total || 0
    } else {
      ElMessage.error(response.data.message || '获取护理分类数据失败')
    }
  } catch (error) {
    console.error('获取护理分类数据失败:', error)
    let errorMessage = '获取护理分类数据失败'
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

// 获取等级类型
const getLevelType = (level) => {
  switch(level) {
    case 'L1': return 'primary'
    case 'L2': return 'success'
    case 'L3': return 'warning'
    case 'L0': return 'danger'  // 特级护理
    default: return 'info'
  }
}

// 获取等级文本
const getLevelText = (level) => {
  switch(level) {
    case 'L1': return '一级护理'
    case 'L2': return '二级护理'
    case 'L3': return '三级护理'
    case 'L0': return '特级护理'
    default: return level
  }
}

// 搜索
const onSearch = () => {
  currentPage.value = 1
  loadCategories()
}

// 重置
const onReset = () => {
  searchForm.name = ''
  searchForm.status = ''
  currentPage.value = 1
  loadCategories()
}

// 新增分类
const addCategory = () => {
  dialogTitle.value = '新增护理分类'
  categoryForm.value = {
    level: 'L1',
    status: 1
  }
  dialogVisible.value = true
}

// 编辑分类
const editCategory = async (row) => {
  try {
    // 获取完整的护理分类信息用于编辑
    const response = await api.nursingCategory.getNursingCategoryByCode(row.levelCode)
    if (response.data.code === 200) {
      dialogTitle.value = '编辑护理分类'
      categoryForm.value = { ...response.data.data }
      dialogVisible.value = true
    } else {
      ElMessage.error(response.data.message || '获取护理分类信息失败')
    }
  } catch (error) {
    console.error('获取护理分类详细信息失败:', error)
    ElMessage.error('获取护理分类详细信息失败')
  }
}

// 查看详情
const viewDetails = (row) => {
  console.log('View details for:', row)
  dialogTitle.value = '护理分类详情'
  categoryForm.value = { ...row }
  // 在实际应用中，可能会跳转到详情页面
}

// 删除分类
const deleteCategory = async (levelCode) => {
  try {
    await ElMessageBox.confirm(
      '此操作将永久删除该护理分类, 是否继续?',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    const response = await api.nursingCategory.deleteNursingCategory(levelCode)
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      // 重新加载数据
      loadCategories()
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') { // 用户取消操作
      console.error('删除护理分类失败:', error)
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
  loadCategories()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadCategories()
}

// 提交表单（新增或编辑）
const submitForm = async () => {
  categoryFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let response
        if (categoryForm.value.levelCode) {
          // 编辑现有护理分类
          response = await api.nursingCategory.updateNursingCategory(categoryForm.value.levelCode, categoryForm.value)
        } else {
          // 创建新护理分类
          response = await api.nursingCategory.createNursingCategory(categoryForm.value)
        }
        
        if (response.data.code === 200) {
          ElMessage.success(categoryForm.value.levelCode ? '更新成功' : '新增成功')
          dialogVisible.value = false
          // 重新加载数据
          loadCategories()
        } else {
          ElMessage.error(response.data.message || (categoryForm.value.levelCode ? '更新失败' : '新增失败'))
        }
      } catch (error) {
        console.error(categoryForm.value.levelCode ? '更新护理分类失败' : '新增护理分类失败', error)
        let errorMessage = categoryForm.value.levelCode ? '更新失败' : '新增失败'
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