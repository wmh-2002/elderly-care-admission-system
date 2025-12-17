<template>
  <div class="bill-manage">
    <div class="page-header">
      <h2>费用管理</h2>
      <el-button type="primary" @click="handleAdd" :icon="Plus">新增账单</el-button>
    </div>
    
    <!-- 搜索和筛选区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="老人姓名">
          <el-select v-model="searchForm.elderId" placeholder="请选择老人" clearable style="width: 150px;">
            <el-option
              v-for="elder in elderList"
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
            placeholder="选择月份"
            format="YYYY-MM"
            value-format="YYYY-MM"
            style="width: 150px;"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="未缴清" :value="0" />
            <el-option label="已缴清" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="table-header">
          <span>账单列表 ({{ total }})</span>
        </div>
      </template>
      
      <el-table 
        :data="billList" 
        v-loading="loading"
        style="width: 100%"
        row-key="id"
      >
        <el-table-column type="index" width="60" />
        <el-table-column prop="elderName" label="老人姓名" width="120" />
        <el-table-column prop="elderNo" label="老人编号" width="120" />
        <el-table-column prop="billMonth" label="账单月份" width="120" />
        <el-table-column prop="totalAmount" label="应付金额" width="120">
          <template #default="{ row }">
            ¥{{ row.totalAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="paidAmount" label="已付金额" width="120">
          <template #default="{ row }">
            ¥{{ row.paidAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="unpaidAmount" label="未付金额" width="120">
          <template #default="{ row }">
            ¥{{ row.totalAmount - row.paidAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已缴清' : '未缴清' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="150" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button 
              size="small" 
              @click="handleView(row)"
            >
              查看
            </el-button>
            <el-button 
              size="small" 
              type="primary" 
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button 
              size="small" 
              type="success" 
              @click="handlePay(row)"
            >
              缴费
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 编辑/新增弹窗 -->
    <el-dialog 
      :title="dialogTitle" 
      v-model="dialogVisible" 
      width="70%" 
      :before-close="handleDialogClose"
    >
      <el-form 
        :model="billForm" 
        :rules="billRules" 
        ref="billFormRef"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="老人姓名" prop="elderId">
              <el-select 
                v-model="billForm.elderId" 
                placeholder="请选择老人"
                @change="onElderChange"
                style="width: 100%"
              >
                <el-option
                  v-for="elder in elderList"
                  :key="elder.id"
                  :label="elder.name"
                  :value="elder.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账单月份" prop="billMonth">
              <el-date-picker
                v-model="billForm.billMonth"
                type="month"
                placeholder="选择月份"
                format="YYYY-MM"
                value-format="YYYY-MM"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <h3>费用明细</h3>
        <el-table 
          :data="billForm.details" 
          style="width: 100%; margin-bottom: 20px;"
          :span-method="objectSpanMethod"
        >
          <el-table-column prop="itemCode" label="费用项目" width="150">
            <template #default="{ row, $index }">
              <el-select 
                v-if="isEdit || $index === billForm.details.length - 1" 
                v-model="row.itemCode" 
                placeholder="请选择费用项目"
                @change="onItemChange($index)"
                style="width: 100%"
              >
                <el-option
                  v-for="item in feeItemList"
                  :key="item.itemCode"
                  :label="item.itemName"
                  :value="item.itemCode"
                />
              </el-select>
              <span v-else>{{ getFeeItemName(row.itemCode) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="100">
            <template #default="{ row, $index }">
              <el-input-number 
                v-if="isEdit || $index === billForm.details.length - 1" 
                v-model="row.quantity" 
                :min="1" 
                style="width: 100%"
              />
              <span v-else>{{ row.quantity }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="unitPrice" label="单价" width="100">
            <template #default="{ row, $index }">
              <span>{{ row.unitPrice }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="小计" width="100">
            <template #default="{ row }">
              <span>¥{{ row.quantity * row.unitPrice }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" v-if="isEdit || billForm.details.length > 1">
            <template #default="{ $index }">
              <el-button 
                v-if="$index !== billForm.details.length - 1" 
                type="danger" 
                size="small"
                @click="removeDetail($index)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <el-button 
          v-if="isEdit" 
          type="primary" 
          @click="addDetail" 
          :icon="Plus"
          style="margin-bottom: 20px;"
        >
          添加费用项目
        </el-button>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="应付金额">
                ¥{{ billForm.totalAmount }}
              </el-descriptions-item>
            </el-descriptions>
          </el-col>
          <el-col :span="8">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="已付金额">
                ¥{{ billForm.paidAmount }}
              </el-descriptions-item>
            </el-descriptions>
          </el-col>
          <el-col :span="8">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="未付金额">
                ¥{{ billForm.totalAmount - billForm.paidAmount }}
              </el-descriptions-item>
            </el-descriptions>
          </el-col>
        </el-row>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button 
            type="primary" 
            @click="handleSubmit" 
            :loading="submitting"
            v-if="isEdit"
          >
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 缴费弹窗 -->
    <el-dialog 
      title="缴费" 
      v-model="payDialogVisible" 
      width="40%" 
      :before-close="handlePayDialogClose"
    >
      <el-form :model="paymentForm" label-width="120px">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="老人姓名">{{ currentBill?.elderName }}</el-descriptions-item>
          <el-descriptions-item label="账单月份">{{ currentBill?.billMonth }}</el-descriptions-item>
          <el-descriptions-item label="应付金额">¥{{ currentBill?.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="已付金额">¥{{ currentBill?.paidAmount }}</el-descriptions-item>
          <el-descriptions-item label="未付金额">¥{{ currentBill?.totalAmount - currentBill?.paidAmount }}</el-descriptions-item>
        </el-descriptions>
        
        <el-form-item label="缴费金额" prop="payAmount">
          <el-input-number 
            v-model="paymentForm.payAmount" 
            :min="0.01" 
            :max="currentBill?.totalAmount - currentBill?.paidAmount"
            :precision="2"
            :step="1"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="缴费方式" prop="payMethod">
          <el-select v-model="paymentForm.payMethod" placeholder="请选择缴费方式" style="width: 100%">
            <el-option label="现金" value="cash" />
            <el-option label="微信" value="wx" />
            <el-option label="支付宝" value="ali" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input 
            v-model="paymentForm.remark" 
            type="textarea" 
            :rows="3"
            placeholder="请输入缴费备注"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handlePayDialogClose">取消</el-button>
          <el-button type="primary" @click="handlePayment" :loading="paymentSubmitting">确认缴费</el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 账单详情弹窗 -->
    <el-drawer v-model="detailVisible" title="账单详情" size="50%">
      <div v-if="currentBill" class="bill-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="老人姓名">{{ currentBill.elderName }}</el-descriptions-item>
          <el-descriptions-item label="老人编号">{{ currentBill.elderNo }}</el-descriptions-item>
          <el-descriptions-item label="账单月份">{{ currentBill.billMonth }}</el-descriptions-item>
          <el-descriptions-item label="应付金额">¥{{ currentBill.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="已付金额">¥{{ currentBill.paidAmount }}</el-descriptions-item>
          <el-descriptions-item label="未付金额">¥{{ currentBill.totalAmount - currentBill.paidAmount }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentBill.status === 1 ? 'success' : 'warning'">
              {{ currentBill.status === 1 ? '已缴清' : '未缴清' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentBill.createTime }}</el-descriptions-item>
        </el-descriptions>
        
        <h3 style="margin-top: 20px;">费用明细</h3>
        <el-table :data="currentBill.details" style="margin-top: 10px;">
          <el-table-column prop="itemName" label="费用项目" />
          <el-table-column prop="quantity" label="数量" />
          <el-table-column prop="unitPrice" label="单价" />
          <el-table-column prop="amount" label="小计" />
        </el-table>
        
        <h3 style="margin-top: 20px;">缴费记录</h3>
        <el-table :data="currentBill.payments" style="margin-top: 10px;">
          <el-table-column prop="payAmount" label="缴费金额" />
          <el-table-column prop="payMethod" label="缴费方式" />
          <el-table-column prop="payTime" label="缴费时间" />
          <el-table-column prop="remark" label="备注" />
        </el-table>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { 
  Plus, 
  Search, 
  Edit, 
  Delete, 
  View,
  Money,
  CirclePlus
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'BillManage',
  setup() {
    // 搜索表单
    const searchForm = reactive({
      elderId: '',
      billMonth: '',
      status: ''
    })
    
    // 分页信息
    const pagination = reactive({
      page: 1,
      size: 10,
      total: 0
    })
    
    // 表格数据
    const loading = ref(false)
    const billList = ref([])
    const total = ref(0)
    
    // 弹窗相关
    const dialogVisible = ref(false)
    const dialogTitle = ref('')
    const detailVisible = ref(false)
    const payDialogVisible = ref(false)
    const submitting = ref(false)
    const paymentSubmitting = ref(false)
    const isEdit = ref(false)
    
    // 表单数据
    const billForm = reactive({
      id: null,
      elderId: '',
      billMonth: new Date().toISOString().slice(0, 7), // 当前年月
      details: [
        { itemCode: '', quantity: 1, unitPrice: 0, amount: 0 }
      ],
      totalAmount: 0,
      paidAmount: 0
    })
    
    // 缴费表单数据
    const paymentForm = reactive({
      payAmount: 0,
      payMethod: 'cash',
      remark: ''
    })
    
    // 表单验证规则
    const billRules = {
      elderId: [
        { required: true, message: '请选择老人', trigger: 'change' }
      ],
      billMonth: [
        { required: true, message: '请选择账单月份', trigger: 'change' }
      ]
    }
    
    const billFormRef = ref(null)
    
    // 当前查看的账单
    const currentBill = ref(null)
    
    // 老人列表（模拟数据）
    const elderList = ref([
      { id: 1, name: '张三', elderNo: 'E1001' },
      { id: 2, name: '李四', elderNo: 'E1002' },
      { id: 3, name: '王五', elderNo: 'E1003' },
      { id: 4, name: '赵六', elderNo: 'E1004' },
      { id: 5, name: '孙七', elderNo: 'E1005' }
    ])
    
    // 费用项目列表（模拟数据）
    const feeItemList = ref([
      { itemCode: 'ACC', itemName: '住宿费', unitPrice: 1500, itemType: '住宿' },
      { itemCode: 'CARE', itemName: '护理费', unitPrice: 2000, itemType: '护理' },
      { itemCode: 'MEAL', itemName: '餐饮费', unitPrice: 600, itemType: '餐饮' },
      { itemCode: 'MED', itemName: '医疗费', unitPrice: 300, itemType: '医疗' },
      { itemCode: 'OTHER', itemName: '其他费用', unitPrice: 100, itemType: '其他' }
    ])
    
    // 获取费用项目名称
    const getFeeItemName = (itemCode) => {
      const item = feeItemList.value.find(i => i.itemCode === itemCode)
      return item ? item.itemName : itemCode
    }
    
    // 当费用项目改变时更新单价
    const onItemChange = (index) => {
      const detail = billForm.details[index]
      const item = feeItemList.value.find(i => i.itemCode === detail.itemCode)
      if (item) {
        detail.unitPrice = item.unitPrice
        detail.amount = detail.quantity * detail.unitPrice
        calculateTotalAmount()
      }
    }
    
    // 计算总金额
    const calculateTotalAmount = () => {
      let total = 0
      billForm.details.forEach(detail => {
        detail.amount = detail.quantity * detail.unitPrice
        total += detail.amount
      })
      billForm.totalAmount = total
    }
    
    // 添加费用明细
    const addDetail = () => {
      billForm.details.push({
        itemCode: '',
        quantity: 1,
        unitPrice: 0,
        amount: 0
      })
    }
    
    // 删除费用明细
    const removeDetail = (index) => {
      if (billForm.details.length > 1) {
        billForm.details.splice(index, 1)
        calculateTotalAmount()
      }
    }
    
    // 老人选择变化时的处理
    const onElderChange = (elderId) => {
      console.log('选择老人:', elderId)
    }
    
    // 合并单元格方法
    const objectSpanMethod = ({ row, column, rowIndex, columnIndex }) => {
      // 这里可以实现合并单元格的逻辑，当前不合并
      return {}
    }
    
    // 模拟获取账单列表
    const getBillList = () => {
      loading.value = true
      
      // 模拟API请求延迟
      setTimeout(() => {
        // 生成模拟数据
        const mockData = []
        for (let i = 1; i <= 20; i++) {
          const elder = elderList.value[Math.floor(Math.random() * elderList.value.length)]
          const status = Math.random() > 0.3 ? 1 : 0 // 70%已缴清，30%未缴清
          
          // 生成费用明细
          const details = []
          const detailCount = Math.floor(Math.random() * 3) + 2 // 2-4项费用
          let totalAmount = 0
          
          for (let j = 0; j < detailCount; j++) {
            const item = feeItemList.value[Math.floor(Math.random() * feeItemList.value.length)]
            const quantity = Math.floor(Math.random() * 3) + 1
            const amount = quantity * item.unitPrice
            
            details.push({
              itemCode: item.itemCode,
              itemName: item.itemName,
              quantity: quantity,
              unitPrice: item.unitPrice,
              amount: amount
            })
            
            totalAmount += amount
          }
          
          // 生成缴费记录
          const payments = []
          let paidAmount = 0
          
          if (status === 0) {
            // 未缴清的账单生成部分缴费记录
            const paymentCount = Math.floor(Math.random() * 2) + 1
            for (let k = 0; k < paymentCount; k++) {
              const payAmount = Math.floor(Math.random() * (totalAmount - paidAmount)) || 100
              paidAmount += payAmount
              
              payments.push({
                payAmount: payAmount,
                payMethod: ['cash', 'wx', 'ali'][Math.floor(Math.random() * 3)],
                payTime: `2024-${String(Math.floor(i / 2) + 1).padStart(2, '0')}-${String(i % 28 + 1).padStart(2, '0')} 10:30:00`,
                remark: k === 0 ? '首次缴费' : '追缴'
              })
            }
          } else {
            paidAmount = totalAmount
            payments.push({
              payAmount: totalAmount,
              payMethod: ['cash', 'wx', 'ali'][Math.floor(Math.random() * 3)],
              payTime: `2024-${String(Math.floor(i / 2) + 1).padStart(2, '0')}-${String(i % 28 + 1).padStart(2, '0')} 10:30:00`,
              remark: '全额缴费'
            })
          }
          
          mockData.push({
            id: i,
            elderId: elder.id,
            elderName: elder.name,
            elderNo: elder.elderNo,
            billMonth: `2024-${String(Math.floor(i / 2) + 1).padStart(2, '0')}`,
            totalAmount: totalAmount,
            paidAmount: paidAmount,
            status: status,
            createTime: `2024-${String(Math.floor(i / 2) + 1).padStart(2, '0')}-${String(i % 28 + 1).padStart(2, '0')} 09:00:00`,
            details: details,
            payments: payments
          })
        }
        
        billList.value = mockData
        total.value = mockData.length
        loading.value = false
      }, 500)
    }
    
    // 搜索处理
    const handleSearch = () => {
      console.log('搜索条件:', searchForm)
      getBillList()
    }
    
    // 重置搜索
    const resetSearch = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = ''
      })
      getBillList()
    }
    
    // 分页处理
    const handleSizeChange = (size) => {
      pagination.size = size
      getBillList()
    }
    
    const handleCurrentChange = (page) => {
      pagination.page = page
      getBillList()
    }
    
    // 新增账单
    const handleAdd = () => {
      isEdit.value = true
      dialogTitle.value = '新增账单'
      // 重置表单
      Object.keys(billForm).forEach(key => {
        if (key === 'details') {
          billForm[key] = [{ itemCode: '', quantity: 1, unitPrice: 0, amount: 0 }]
        } else if (key === 'billMonth') {
          billForm[key] = new Date().toISOString().slice(0, 7)
        } else {
          billForm[key] = ''
        }
      })
      billForm.totalAmount = 0
      billForm.paidAmount = 0
      dialogVisible.value = true
    }
    
    // 编辑账单
    const handleEdit = (row) => {
      isEdit.value = true
      dialogTitle.value = '编辑账单'
      // 填充表单数据
      billForm.id = row.id
      billForm.elderId = row.elderId
      billForm.billMonth = row.billMonth
      billForm.totalAmount = row.totalAmount
      billForm.paidAmount = row.paidAmount
      billForm.details = row.details.map(detail => ({
        itemCode: detail.itemCode,
        quantity: detail.quantity,
        unitPrice: detail.unitPrice,
        amount: detail.amount
      }))
      dialogVisible.value = true
    }
    
    // 查看账单详情
    const handleView = (row) => {
      currentBill.value = row
      detailVisible.value = true
    }
    
    // 缴费
    const handlePay = (row) => {
      currentBill.value = row
      paymentForm.payAmount = row.totalAmount - row.paidAmount
      paymentForm.payMethod = 'cash'
      paymentForm.remark = ''
      payDialogVisible.value = true
    }
    
    // 删除账单
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除 ${row.elderName} 的账单吗？`,
          '警告',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        // 模拟删除操作
        console.log('删除账单:', row)
        ElMessage.success('删除成功')
        getBillList()
      } catch (error) {
        // 用户取消删除
      }
    }
    
    // 提交表单
    const handleSubmit = async () => {
      if (!billFormRef.value) return
      
      try {
        await billFormRef.value.validate()
        submitting.value = true
        
        // 模拟提交操作
        console.log('提交账单信息:', billForm)
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
        dialogVisible.value = false
        getBillList()
      } catch (error) {
        console.error('表单验证失败:', error)
      } finally {
        submitting.value = false
      }
    }
    
    // 处理缴费
    const handlePayment = async () => {
      paymentSubmitting.value = true
      
      try {
        // 模拟缴费操作
        console.log('缴费信息:', paymentForm)
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        ElMessage.success('缴费成功')
        payDialogVisible.value = false
        getBillList()
      } catch (error) {
        console.error('缴费失败:', error)
      } finally {
        paymentSubmitting.value = false
      }
    }
    
    // 关闭弹窗
    const handleDialogClose = () => {
      dialogVisible.value = false
      if (billFormRef.value) {
        billFormRef.value.clearValidate()
      }
    }
    
    const handlePayDialogClose = () => {
      payDialogVisible.value = false
    }
    
    // 组件挂载时获取数据
    onMounted(() => {
      getBillList()
    })
    
    return {
      // 搜索表单
      searchForm,
      
      // 分页
      pagination,
      total,
      
      // 表格
      loading,
      billList,
      
      // 弹窗
      dialogVisible,
      dialogTitle,
      detailVisible,
      payDialogVisible,
      submitting,
      paymentSubmitting,
      isEdit,
      
      // 表单
      billForm,
      billRules,
      billFormRef,
      paymentForm,
      
      // 当前账单
      currentBill,
      
      // 数据列表
      elderList,
      feeItemList,
      
      // 方法
      handleSearch,
      resetSearch,
      handleSizeChange,
      handleCurrentChange,
      handleAdd,
      handleEdit,
      handleView,
      handlePay,
      handleDelete,
      handleSubmit,
      handlePayment,
      handleDialogClose,
      handlePayDialogClose,
      onElderChange,
      onItemChange,
      addDetail,
      removeDetail,
      calculateTotalAmount,
      getFeeItemName,
      objectSpanMethod,
      
      // 图标
      Plus,
      Search,
      Edit,
      Delete,
      View
    }
  }
}
</script>

<style scoped>
.bill-manage {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #333;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  min-height: 400px;
}

.table-header {
  font-weight: bold;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  text-align: right;
}

.bill-detail {
  padding: 20px 0;
}

.detail-section {
  margin-bottom: 15px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .el-form-item {
    width: 100%;
  }
  
  .el-col {
    width: 100%;
    margin-bottom: 10px;
  }
  
  .el-col:last-child {
    margin-bottom: 0;
  }
}
</style>