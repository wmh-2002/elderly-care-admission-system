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
          <el-form-item label="老人">
            <el-select v-model="searchForm.elderId" placeholder="请选择老人" filterable clearable>
              <el-option
                v-for="elder in elderOptions"
                :key="elder.id"
                :label="elder.name"
                :value="elder.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="账单月份">
            <el-date-picker
              v-model="searchForm.billMonth"
              type="month"
              placeholder="请选择月份"
              format="YYYY-MM"
              value-format="YYYY-MM"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="未缴清" :value="0" />
              <el-option label="已缴清" :value="1" />
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
        <el-table-column prop="id" label="账单ID" width="80" fixed="left" />
        <el-table-column prop="elderName" label="老人姓名" min-width="120" fixed="left" />
        <el-table-column prop="elderNo" label="老人编号" min-width="100" />
        <el-table-column prop="billMonth" label="账单月份" min-width="100">
          <template #default="{ row }">
            <span>{{ row.billMonth }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="应付金额" min-width="120">
          <template #default="{ row }">
            <span class="amount-text">¥{{ row.totalAmount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="paidAmount" label="已付金额" min-width="120">
          <template #default="{ row }">
            <span class="paid-amount-text">¥{{ row.paidAmount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" min-width="100" />
        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="160">
          <template #default="{ row }">
            <span>{{ formatDateTime(row.createdAt) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewDetails(row)">详情</el-button>
            <el-button size="small" type="primary" @click="payBill(row)" :disabled="row.status === 1">支付</el-button>
            <el-button size="small" type="danger" @click="deleteSettlement(row.id)" :disabled="row.status === 1">删除</el-button>
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
    
    <!-- 账单结算弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px">
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
            <el-form-item label="账单月份" prop="billMonth">
              <el-date-picker
                v-model="settlementForm.billMonth"
                type="month"
                placeholder="选择账单月份"
                format="YYYY-MM"
                value-format="YYYY-MM"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="支付方式">
              <el-select v-model="settlementForm.paymentMethod" placeholder="请选择支付方式" style="width: 100%">
                <el-option label="现金" value="现金" />
                <el-option label="微信" value="微信" />
                <el-option label="支付宝" value="支付宝" />
                <el-option label="银行卡" value="银行卡" />
                <el-option label="其它" value="其它" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 老人信息展示 -->
        <div v-if="selectedElder" class="elder-info-section">
          <el-divider>老人信息</el-divider>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="老人姓名">{{ selectedElder.name }}</el-descriptions-item>
            <el-descriptions-item label="老人编号">{{ selectedElder.elderNo }}</el-descriptions-item>
            <el-descriptions-item label="房间号">{{ selectedElder.roomNo }}-{{ selectedElder.bedNo }}</el-descriptions-item>
            <el-descriptions-item label="护理等级">{{ selectedElder.careLevelName || '暂无' }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 费用明细 -->
        <el-divider>费用明细</el-divider>
        <div class="fee-preview">
          <p>系统将自动计算以下费用：</p>
          <ul>
            <li>🧓 <strong>护理费用</strong>：根据护理等级自动计算当月天数 × 日单价</li>
            <li>➕ <strong>附加费用</strong>：可添加餐饮、医疗等额外费用</li>
          </ul>
        </div>
        
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
import { ElMessage, ElMessageBox } from 'element-plus'
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
import api from '@/api'

// 搜索表单
const searchForm = reactive({
  elderId: null,
  billMonth: '',
  status: null
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
  billMonth: [
    { required: true, message: '请选择账单月份', trigger: 'change' }
  ]
}

// 选中的老人信息
const selectedElder = ref(null)

// 加载账单数据
const loadSettlements = async () => {
  console.log('加载账单列表数据...')
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      elderId: searchForm.elderId,
      billMonth: searchForm.billMonth,
      status: searchForm.status
    }

    console.log('API请求参数:', params)
    const response = await api.bill.getBillList(params)
    console.log('API响应:', response)

    if (response.data && response.data.code === 200) {
      const pageData = response.data.data
      console.log('账单列表数据:', pageData)

      settlementList.value = pageData.content.map(bill => ({
        id: bill.id,
        elderId: bill.elderId,
        elderName: bill.elderName,
        elderNo: bill.elderNo,
        billMonth: bill.billMonth,
        totalAmount: bill.totalAmount,
        paidAmount: bill.paidAmount,
        paymentMethod: bill.paymentMethod,
        status: bill.status,
        statusText: bill.statusText,
        createdAt: bill.createdAt,
        updatedAt: bill.updatedAt,
        details: bill.details || []
      }))

      total.value = pageData.totalElements
      console.log('数据加载完成，共', settlementList.value.length, '条记录')
    } else {
      console.warn('API响应格式不正确:', response.data)
      settlementList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('加载账单列表失败:', error)
    ElMessage.error('加载账单列表失败')
    settlementList.value = []
    total.value = 0
  }
}

// 加载老人选项数据
const loadElders = async () => {
  console.log('加载老人列表数据...')
  try {
    const response = await api.elder.getElderList({
      page: 1,
      size: 1000 // 获取所有老人用于下拉选择
    })

    if (response.data && response.data.code === 200) {
      const pageData = response.data.data
      elderOptions.value = pageData.content.map(elder => ({
        id: elder.id,
        name: elder.name,
        elderNo: elder.elderNo,
        roomNo: elder.roomNo,
        bedNo: elder.bedNo
      }))
      console.log('老人选项加载完成，共', elderOptions.value.length, '个老人')
    } else {
      console.warn('老人API响应格式不正确:', response.data)
      elderOptions.value = []
    }
  } catch (error) {
    console.error('加载老人列表失败:', error)
    ElMessage.error('加载老人列表失败')
    elderOptions.value = []
  }
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
    case 1: return 'success'  // 已缴清
    case 0: return 'warning'  // 未缴清
    default: return 'info'
  }
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN')
}

// 支付账单
const payBill = async (bill) => {
  console.log('支付账单:', bill)

  try {
    const remainingAmount = bill.totalAmount - bill.paidAmount

    // 这里可以弹出一个支付确认对话框
    const confirmed = await ElMessageBox.confirm(
      `确认支付 ¥${remainingAmount.toFixed(2)} 给账单 ${bill.id} 吗？`,
      '确认支付',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    if (confirmed) {
      const response = await api.bill.payBill(bill.id, { paidAmount: remainingAmount })
      console.log('支付API响应:', response)

      if (response.data && response.data.code === 200) {
        ElMessage.success('支付成功')
        loadSettlements()
      } else {
        ElMessage.error('支付失败: ' + (response.data?.message || '未知错误'))
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('支付失败:', error)
      ElMessage.error('支付失败: ' + (error.response?.data?.message || error.message))
    }
  }
}

// 搜索
const onSearch = () => {
  console.log('Search:', searchForm)
  loadSettlements()
}

// 重置
const onReset = () => {
  searchForm.elderId = null
  searchForm.billMonth = ''
  searchForm.status = null
  currentPage.value = 1
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
const handleElderChange = async (elderId) => {
  console.log('Selected elder:', elderId)
  const elder = elderOptions.value.find(elder => elder.id === elderId)
  if (elder) {
    selectedElder.value = elder
    console.log('Selected elder details:', elder)

    // 可以在这里获取老人的更多详细信息，如果需要的话
    try {
      const response = await api.elder.getElderById(elderId)
      if (response.data && response.data.code === 200) {
        const elderDetail = response.data.data
        selectedElder.value = {
          ...elder,
          careLevelName: elderDetail.careLevelName || '暂无'
        }
        console.log('Elder detail loaded:', selectedElder.value)
      }
    } catch (error) {
      console.warn('Failed to load elder details:', error)
    }
  } else {
    selectedElder.value = null
  }
}

// 重置表单
const resetForm = () => {
  settlementForm.value = {
    elderId: null,
    billMonth: '',
    paymentMethod: '现金',
    additionalFees: []
  }
  selectedElder.value = null
}

// 提交表单 - 调用账单结算API
const submitForm = async () => {
  settlementFormRef.value.validate(async (valid) => {
    if (valid) {
      console.log('提交账单结算:', settlementForm.value)

      try {
        // 构建账单结算请求数据
        const settleData = {
          elderId: settlementForm.value.elderId,
          billMonth: settlementForm.value.billMonth,
          paymentMethod: settlementForm.value.paymentMethod || '现金',
          additionalFees: [] // 如果需要添加额外费用，可以在这里扩展
        }

        console.log('API请求数据:', settleData)

        // 调用账单结算API
        const response = await api.bill.settleBill(settleData)
        console.log('账单结算API响应:', response)

        if (response.data && response.data.code === 200) {
          ElMessage.success('账单结算成功')

          // 关闭弹窗并刷新列表
          dialogVisible.value = false
          loadSettlements()

          // 重置表单
          resetForm()

          console.log('账单结算完成，账单ID:', response.data.data.id)
        } else {
          ElMessage.error('账单结算失败: ' + (response.data?.message || '未知错误'))
        }
      } catch (error) {
        console.error('账单结算失败:', error)
        ElMessage.error('账单结算失败: ' + (error.response?.data?.message || error.message))
      }
    } else {
      console.log('表单验证失败!')
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

/* 金额显示样式 */
.amount-text {
  font-weight: bold;
  color: #e6a23c;
}

.paid-amount-text {
  font-weight: bold;
  color: #67c23a;
}

/* 老人信息展示样式 */
.elder-info-section {
  margin: 20px 0;
}

.elder-info-section .el-descriptions {
  margin-top: 10px;
}

.fee-preview {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 6px;
  margin: 15px 0;
}

.fee-preview ul {
  margin: 10px 0 0 0;
  padding-left: 20px;
}

.fee-preview li {
  margin-bottom: 8px;
  color: #606266;
}
</style>