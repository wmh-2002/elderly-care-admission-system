<template>
  <div class="fee-settlement-container">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>费用结算</el-breadcrumb-item>
    </el-breadcrumb>
    
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">费用结算</span>
        </div>
      </template>
      
      <!-- 搜索和筛选区域 -->
      <div class="search-section">
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
          <el-form-item label="老人姓名">
            <el-input v-model="searchForm.elderName" placeholder="请输入老人姓名" :prefix-icon="User" />
          </el-form-item>
          <el-form-item label="结算状态">
            <el-select v-model="searchForm.status" placeholder="请选择结算状态" :prefix-icon="CircleCheck">
              <el-option label="未结算" value="未结算" />
              <el-option label="已结算" value="已结算" />
            </el-select>
          </el-form-item>
          <el-form-item label="结算日期">
            <el-date-picker
              v-model="searchForm.settlementDate"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
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
          <el-button type="primary" @click="openSettlementDialog" :icon="Plus">新增结算</el-button>
          <el-button type="danger" :disabled="!multipleSelection.length" :icon="Delete">批量删除</el-button>
        </div>
        <div class="button-group-right">
          <el-button @click="exportTable" :icon="Download">导出</el-button>
          <el-button @click="toggleView" :icon="Menu">视图</el-button>
        </div>
      </div>
      
      <!-- 数据表格 -->
      <el-table
        :data="settlementList"
        style="width: 100%;"
        @selection-change="handleSelectionChange"
        stripe
        border
      >
        <el-table-column type="selection" width="55" fixed="left" />
        <el-table-column prop="id" label="ID" width="80" fixed="left" />
        <el-table-column prop="elderName" label="老人姓名" min-width="120" fixed="left" />
        <el-table-column prop="roomNo" label="房间号" min-width="100" />
        <el-table-column prop="bedNo" label="床位号" min-width="100" />
        <el-table-column prop="settlementType" label="结算类型" min-width="120">
          <template #default="{ row }">
            <el-tag :type="getSettlementTypeTag(row.settlementType)">
              {{ row.settlementType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="settlementAmount" label="结算金额" min-width="120">
          <template #default="{ row }">
            <span>¥{{ row.settlementAmount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="settlementDate" label="结算日期" min-width="120" />
        <el-table-column prop="status" label="结算状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operator" label="操作员" min-width="120" />
        <el-table-column label="操作" min-width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewDetails(row)">详情</el-button>
            <el-button size="small" type="primary" @click="editSettlement(row)" :disabled="row.status === '已结算'">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteSettlement(row.id)" :disabled="row.status === '已结算'">删除</el-button>
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
    
    <!-- 新增/编辑结算弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="settlementForm" :rules="settlementRules" ref="settlementFormRef" label-width="120px">
        <el-form-item label="选择老人" prop="elderId">
          <el-select 
            v-model="settlementForm.elderId" 
            placeholder="请选择老人" 
            style="width: 100%" 
            filterable
            @change="handleElderChange"
          >
            <el-option
              v-for="elder in elderOptions"
              :key="elder.id"
              :label="elder.name"
              :value="elder.id"
            >
              {{ elder.name }} ({{ elder.elderNo }}, {{ elder.roomNo }}-{{ elder.bedNo }})
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="结算类型" prop="settlementType">
              <el-select v-model="settlementForm.settlementType" placeholder="请选择结算类型" style="width: 100%">
                <el-option label="月度结算" value="月度结算" />
                <el-option label="季度结算" value="季度结算" />
                <el-option label="年度结算" value="年度结算" />
                <el-option label="临时结算" value="临时结算" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结算金额" prop="settlementAmount">
              <el-input-number 
                v-model="settlementForm.settlementAmount" 
                :min="0" 
                :precision="2" 
                :step="100"
                placeholder="请输入结算金额"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="结算日期" prop="settlementDate">
              <el-date-picker
                v-model="settlementForm.settlementDate"
                type="date"
                placeholder="选择结算日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结算状态" prop="status">
              <el-select v-model="settlementForm.status" placeholder="请选择结算状态" style="width: 100%">
                <el-option label="未结算" value="未结算" />
                <el-option label="已结算" value="已结算" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="结算说明" prop="description">
          <el-input 
            v-model="settlementForm.description" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入结算说明"
          />
        </el-form-item>
        
        <el-form-item label="费用明细">
          <el-table
            :data="feeDetails"
            style="width: 100%; margin-top: 10px;"
            border
          >
            <el-table-column prop="itemName" label="费用项目" min-width="150" />
            <el-table-column prop="amount" label="金额" min-width="100">
              <template #default="{ row }">
                <span>¥{{ row.amount.toFixed(2) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" min-width="150" />
          </el-table>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
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
  status: '',
  settlementDate: []
})

// 结算列表数据
const settlementList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 选中的行
const multipleSelection = ref([])

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const settlementForm = ref({})
const settlementFormRef = ref()

// 选项数据
const elderOptions = ref([])

// 费用明细
const feeDetails = ref([])

// 表单验证规则
const settlementRules = {
  elderId: [
    { required: true, message: '请选择老人', trigger: 'change' }
  ],
  settlementType: [
    { required: true, message: '请选择结算类型', trigger: 'change' }
  ],
  settlementAmount: [
    { required: true, message: '请输入结算金额', trigger: 'blur' },
    { type: 'number', message: '金额必须为数字值', trigger: 'blur' }
  ],
  settlementDate: [
    { required: true, message: '请选择结算日期', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择结算状态', trigger: 'change' }
  ]
}

// 模拟数据加载
const loadSettlements = () => {
  // 模拟从 API 获取数据
  const mockData = []
  for (let i = 1; i <= 30; i++) {
    const statusOptions = ['未结算', '已结算']
    const status = statusOptions[Math.floor(Math.random() * statusOptions.length)]
    
    mockData.push({
      id: i,
      elderId: Math.floor(Math.random() * 50) + 1,
      elderName: `老人${Math.floor(Math.random() * 50) + 1}`,
      roomNo: `${Math.floor(Math.random() * 10) + 1}${String(Math.floor(Math.random() * 100)).padStart(2, '0')}`,
      bedNo: String(Math.floor(Math.random() * 10) + 1),
      settlementType: ['月度结算', '季度结算', '年度结算', '临时结算'][Math.floor(Math.random() * 4)],
      settlementAmount: Math.floor(Math.random() * 10000) + 1000,
      settlementDate: `${2023 + Math.floor(Math.random() * 2)}-${String(Math.floor(Math.random() * 12) + 1).padStart(2, '0')}-${String(Math.floor(Math.random() * 28) + 1).padStart(2, '0')}`,
      status: status,
      operator: `操作员${Math.floor(Math.random() * 10) + 1}`,
      description: `本次结算为${Math.floor(Math.random() * 4) + 1}月费用`
    })
  }
  settlementList.value = mockData
  total.value = mockData.length
}

// 加载老人选项数据
const loadElders = () => {
  const mockElders = []
  for (let i = 1; i <= 50; i++) {
    mockElders.push({
      id: i,
      name: `老人${i}`,
      elderNo: `ELDER${String(i).padStart(4, '0')}`,
      roomNo: `${Math.floor(i/5) + 1}${String(i%5 + 1).padStart(2, '0')}`,
      bedNo: String(i%8 + 1)
    })
  }
  elderOptions.value = mockElders
}

// 获取结算类型标签
const getSettlementTypeTag = (type) => {
  switch(type) {
    case '月度结算': return 'primary'
    case '季度结算': return 'success'
    case '年度结算': return 'warning'
    case '临时结算': return 'info'
    default: return 'info'
  }
}

// 获取状态类型
const getStatusType = (status) => {
  switch(status) {
    case '已结算': return 'success'
    case '未结算': return 'warning'
    default: return 'info'
  }
}

// 搜索
const onSearch = () => {
  console.log('Search:', searchForm)
  loadSettlements()
}

// 重置
const onReset = () => {
  searchForm.elderName = ''
  searchForm.status = ''
  searchForm.settlementDate = []
  loadSettlements()
}

// 打开结算对话框
const openSettlementDialog = () => {
  dialogTitle.value = '新增费用结算'
  settlementForm.value = {
    settlementType: '月度结算',
    settlementAmount: 0,
    settlementDate: new Date().toISOString().split('T')[0],
    status: '未结算'
  }
  feeDetails.value = [
    { itemName: '床位费', amount: 2000, remark: '标准床位' },
    { itemName: '护理费', amount: 1500, remark: '二级护理' },
    { itemName: '餐饮费', amount: 800, remark: '月餐费' },
    { itemName: '其他费用', amount: 200, remark: '杂费' }
  ]
  dialogVisible.value = true
}

// 编辑结算
const editSettlement = (row) => {
  if (row.status === '已结算') return
  
  dialogTitle.value = '编辑费用结算'
  settlementForm.value = { ...row }
  feeDetails.value = [
    { itemName: '床位费', amount: row.settlementAmount * 0.4, remark: '标准床位' },
    { itemName: '护理费', amount: row.settlementAmount * 0.35, remark: '按护理级别' },
    { itemName: '餐饮费', amount: row.settlementAmount * 0.2, remark: '月餐费' },
    { itemName: '其他费用', amount: row.settlementAmount * 0.05, remark: '杂费' }
  ]
  dialogVisible.value = true
}

// 查看详情
const viewDetails = (row) => {
  console.log('View details for:', row)
  dialogTitle.value = '费用结算详情'
  settlementForm.value = { ...row }
  feeDetails.value = [
    { itemName: '床位费', amount: row.settlementAmount * 0.4, remark: '标准床位' },
    { itemName: '护理费', amount: row.settlementAmount * 0.35, remark: '按护理级别' },
    { itemName: '餐饮费', amount: row.settlementAmount * 0.2, remark: '月餐费' },
    { itemName: '其他费用', amount: row.settlementAmount * 0.05, remark: '杂费' }
  ]
  // 禁用编辑
}

// 删除结算
const deleteSettlement = (id) => {
  if (confirm('确认删除此项费用结算记录吗？')) {
    console.log('Delete settlement:', id)
    // 实际应用中会调用API删除
    loadSettlements()
  }
}

// 批量删除
const handleSelectionChange = (val) => {
  multipleSelection.value = val
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  loadSettlements()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadSettlements()
}

// 处理老人选择变化
const handleElderChange = (elderId) => {
  console.log('Selected elder:', elderId)
  const selectedElder = elderOptions.value.find(elder => elder.id === elderId)
  if (selectedElder) {
    // 可以根据老人信息自动填充一些字段
    console.log('Selected elder details:', selectedElder)
  }
}

// 提交表单
const submitForm = () => {
  settlementFormRef.value.validate((valid) => {
    if (valid) {
      console.log('Submit form:', settlementForm.value)
      // 实际应用中会调用API提交数据
      dialogVisible.value = false
      loadSettlements()
      ElMessage.success('费用结算记录保存成功')
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
  loadSettlements()
  loadElders()
})
</script>

<style scoped>
.fee-settlement-container {
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