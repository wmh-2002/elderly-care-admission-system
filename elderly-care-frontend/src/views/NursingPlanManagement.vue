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
          <el-form-item label="护理等级">
            <el-select v-model="searchForm.careLevelId" placeholder="请选择护理等级" :prefix-icon="CircleCheck" clearable @change="onSearch" style="width: 140px">
              <el-option
                v-for="category in categoryOptions"
                :key="category.levelCode"
                :label="category.levelName"
                :value="category.levelCode"
              >
                {{ category.levelName }}
              </el-option>
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
        <el-table-column prop="careLevelName" label="护理等级" min-width="120" />
        <el-table-column prop="planName" label="计划名称" min-width="150" />
        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" min-width="120" />
        <el-table-column prop="endDate" label="结束日期" min-width="120" />
        <el-table-column prop="assignedNurseName" label="分配护理员" min-width="120" />
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
            <el-form-item label="护理等级" prop="careLevelId">
              <el-select v-model="planForm.careLevelId" placeholder="请选择护理等级" style="width: 100%">
                <el-option
                  v-for="category in categoryOptions"
                  :key="category.levelCode"
                  :label="category.levelName"
                  :value="category.levelCode"
                >
                  {{ category.levelName }}
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计划状态" prop="status">
              <el-select v-model="planForm.status" placeholder="请选择计划状态" style="width: 100%">
                <el-option label="进行中" value="进行中" />
                <el-option label="已完成" value="已完成" />
                <el-option label="已取消" value="已取消" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分配护理员" prop="assignedNurseId">
              <el-select v-model="planForm.assignedNurseId" placeholder="请选择分配护理员" style="width: 100%">
                <el-option
                  v-for="nurse in nurseOptions"
                  :key="nurse.id"
                  :label="nurse.realName"
                  :value="nurse.id"
                >
                  {{ nurse.realName }}
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
                value-format="YYYY-MM-DD HH:mm:ss"
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
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="计划内容" prop="planContent" style="width: 100%">
          <el-input v-model="planForm.planContent" type="textarea" :rows="6" placeholder="请输入护理计划的详细内容" />
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
  User,
  CircleCheck,
  Download,
  Menu
} from '@element-plus/icons-vue'

// 搜索表单
const searchForm = reactive({
  elderName: '',
  careLevelId: null
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
  status: [
    { required: true, message: '请选择计划状态', trigger: 'change' }
  ],
  startDate: [
    { required: true, message: '请选择开始日期', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: '请选择结束日期', trigger: 'change' },
    {
      validator: (rule, value, callback) => {
        if (planForm.value.startDate && value && new Date(value) < new Date(planForm.value.startDate)) {
          callback(new Error('结束日期不能早于开始日期'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ],
  planContent: [
    { required: true, message: '请输入计划内容', trigger: 'blur' }
  ]
}

// 加载护理计划列表
const loadPlans = async () => {
  try {
    const params = {
      page: currentPage.value, // 后端分页从1开始
      size: pageSize.value,
      planContent: searchForm.elderName || undefined, // 使用planContent字段搜索内容
      careLevelId: searchForm.careLevelId || undefined // 添加护理等级筛选参数
    }

    const response = await api.nursingPlan.getNursingPlanList(params)
    if (response.data.code === 200) {
      planList.value = response.data.data.content.map(plan => ({
        ...plan,
        // 将后端返回的字段映射到前端表格所需的字段
        id: plan.id,
        elderId: plan.elderId,
        elderName: plan.elderName,
        careLevelId: plan.careLevelId,
        careLevelName: plan.careLevelName,
        status: plan.status || '进行中',
        startDate: plan.startDate ? new Date(plan.startDate).toISOString().split('T')[0] : '',
        endDate: plan.endDate ? new Date(plan.endDate).toISOString().split('T')[0] : '',
        assignedNurseName: plan.assignedNurseName || '暂无分配',
        assignedNurseId: plan.assignedNurseId,
        planContent: plan.planContent,
        planName: plan.planContent ? plan.planContent.substring(0, 20) + '...' : '护理计划',
        createTime: plan.createTime,
        updateTime: plan.updateTime
      }))
      total.value = response.data.data.totalElements
    } else {
      ElMessage.error(response.data.message || '获取护理计划列表失败')
    }
  } catch (error) {
    console.error('获取护理计划列表失败:', error)
    ElMessage.error('获取护理计划列表失败')
  }
}

// 加载老人选项数据
const loadElders = async () => {
  try {
    // 从老人API获取老人列表
    const response = await api.elder.getElderList({ page: 1, size: 100 }) // 获取所有老人
    if (response.data.code === 200) {
      elderOptions.value = response.data.data.content || []
    } else {
      console.error('获取老人列表失败:', response.data.message)
    }
  } catch (error) {
    console.error('获取老人列表失败:', error)
  }
}

// 加载护理分类选项数据
const loadCategories = async () => {
  try {
    // 从护理分类API获取护理等级列表
    const response = await api.nursingCategory.getNursingCategoryList({ page: 1, size: 100 }) // 获取所有护理分类
    if (response.data.code === 200) {
      // Map the response to ensure correct field names for the dropdown
      categoryOptions.value = response.data.data.content || []
    } else {
      console.error('获取护理分类列表失败:', response.data.message)
    }
  } catch (error) {
    console.error('获取护理分类列表失败:', error)
  }
}

// 加载护理员选项数据（从用户API获取角色为护理员的用户）
const loadNurses = async () => {
  try {
    // 从用户API获取用户列表，这里假设需要获取所有用户然后筛选护理员
    const response = await api.user.getUserList({ page: 1, size: 100 })
    if (response.data.code === 200) {
      // 过滤出护理员角色的用户（这里可能需要根据实际角色ID调整）
      nurseOptions.value = response.data.data.content || []
    } else {
      console.error('获取用户列表失败:', response.data.message)
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
  }
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
  currentPage.value
  loadPlans()
}

// 重置
const onReset = () => {
  searchForm.elderName = ''
  searchForm.careLevelId = null
  currentPage.value = 1
  loadPlans()
}

// 新增计划
const addPlan = () => {
  dialogTitle.value = '新增护理计划'
  planForm.value = {
    elderId: null,
    careLevelId: null,
    status: '进行中',
    startDate: null,
    endDate: null,
    assignedNurseId: null,
    planContent: ''
  }
  dialogVisible.value = true
}

// 编辑计划
const editPlan = async (row) => {
  try {
    dialogTitle.value = '编辑护理计划'
    // 获取完整的护理计划信息
    const response = await api.nursingPlan.getNursingPlanById(row.id)
    if (response.data.code === 200) {
      planForm.value = { ...response.data.data }
      dialogVisible.value = true
    } else {
      ElMessage.error(response.data.message || '获取护理计划详情失败')
    }
  } catch (error) {
    console.error('获取护理计划详情失败:', error)
    ElMessage.error('获取护理计划详情失败')
  }
}

// 查看详情
const viewDetails = async (row) => {
  try {
    dialogTitle.value = '护理计划详情'
    // 获取完整的护理计划信息
    const response = await api.nursingPlan.getNursingPlanById(row.id)
    if (response.data.code === 200) {
      planForm.value = { ...response.data.data }
      dialogVisible.value = true
    } else {
      ElMessage.error(response.data.message || '获取护理计划详情失败')
    }
  } catch (error) {
    console.error('获取护理计划详情失败:', error)
    ElMessage.error('获取护理计划详情失败')
  }
}

// 删除计划
const deletePlan = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个护理计划吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await api.nursingPlan.deleteNursingPlan(id)
    if (response.data.code === 200) {
      ElMessage.success(response.data.message || '删除成功')
      loadPlans()
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') { // 用户取消删除
      console.error('删除护理计划失败:', error)
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
  currentPage.value = 1
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
const submitForm = async () => {
  const valid = await planFormRef.value.validate().catch(() => false)
  if (valid) {
    try {
      let response
      if (planForm.value.id) {
        // 更新护理计划
        response = await api.nursingPlan.updateNursingPlan(planForm.value.id, {
          elderId: planForm.value.elderId,
          careLevelId: planForm.value.careLevelId,
          status: planForm.value.status,
          startDate: planForm.value.startDate,
          endDate: planForm.value.endDate,
          assignedNurseId: planForm.value.assignedNurseId,
          planContent: planForm.value.planContent
        })
      } else {
        // 创建护理计划
        response = await api.nursingPlan.createNursingPlan({
          elderId: planForm.value.elderId,
          careLevelId: planForm.value.careLevelId,
          status: planForm.value.status,
          startDate: planForm.value.startDate,
          endDate: planForm.value.endDate,
          assignedNurseId: planForm.value.assignedNurseId,
          planContent: planForm.value.planContent
        })
      }

      if (response.data.code === 200) {
        ElMessage.success(response.data.message || (planForm.value.id ? '更新成功' : '创建成功'))
        dialogVisible.value = false
        loadPlans()
      } else {
        ElMessage.error(response.data.message || (planForm.value.id ? '更新失败' : '创建失败'))
      }
    } catch (error) {
      console.error(planForm.value.id ? '更新护理计划失败:' : '创建护理计划失败:', error)
      ElMessage.error(planForm.value.id ? '更新失败' : '创建失败')
    }
  } else {
    console.log('Validation failed!')
  }
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