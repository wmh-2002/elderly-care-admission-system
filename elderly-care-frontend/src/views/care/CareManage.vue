<template>
  <div class="care-manage">
    <div class="page-header">
      <h2>护理管理</h2>
      <el-button type="primary" @click="handleAdd" :icon="Plus">新增护理记录</el-button>
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
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 240px;"
          />
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
          <span>护理记录列表 ({{ total }})</span>
        </div>
      </template>
      
      <el-table 
        :data="careRecordList" 
        v-loading="loading"
        style="width: 100%"
        row-key="id"
      >
        <el-table-column type="index" width="60" />
        <el-table-column prop="elderName" label="老人姓名" width="120" />
        <el-table-column prop="elderNo" label="老人编号" width="120" />
        <el-table-column prop="recordDate" label="记录日期" width="120" />
        <el-table-column prop="temperature" label="体温(℃)" width="100">
          <template #default="{ row }">
            {{ row.temperature || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="pulse" label="脉搏(次/分)" width="120">
          <template #default="{ row }">
            {{ row.pulse || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="breath" label="呼吸(次/分)" width="120">
          <template #default="{ row }">
            {{ row.breath || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="bloodPressure" label="血压" width="120">
          <template #default="{ row }">
            {{ row.bloodPressure || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="nurseName" label="护理员" width="120" />
        <el-table-column label="操作" width="200" fixed="right">
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
        :model="careForm" 
        :rules="careRules" 
        ref="careFormRef"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="老人姓名" prop="elderId">
              <el-select 
                v-model="careForm.elderId" 
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
            <el-form-item label="记录日期" prop="recordDate">
              <el-date-picker
                v-model="careForm.recordDate"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="体温(℃)" prop="temperature">
              <el-input v-model="careForm.temperature" placeholder="如：36.5">
                <template #append>℃</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="脉搏(次/分)" prop="pulse">
              <el-input v-model="careForm.pulse" placeholder="如：72">
                <template #append>次/分</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="呼吸(次/分)" prop="breath">
              <el-input v-model="careForm.breath" placeholder="如：20">
                <template #append>次/分</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="血压" prop="bloodPressure">
              <el-input v-model="careForm.bloodPressure" placeholder="如：120/80" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="饮食情况" prop="diet">
          <el-input 
            v-model="careForm.diet" 
            type="textarea" 
            :rows="2"
            placeholder="记录老人饮食情况，如：正常进食、偏食、食欲不佳等"
          />
        </el-form-item>
        
        <el-form-item label="排泄情况" prop="excrete">
          <el-input 
            v-model="careForm.excrete" 
            type="textarea" 
            :rows="2"
            placeholder="记录老人排泄情况，如：正常、便秘、失禁等"
          />
        </el-form-item>
        
        <el-form-item label="睡眠情况" prop="sleep">
          <el-input 
            v-model="careForm.sleep" 
            type="textarea" 
            :rows="2"
            placeholder="记录老人睡眠情况，如：睡眠质量、睡眠时间等"
          />
        </el-form-item>
        
        <el-form-item label="用药记录" prop="medicine">
          <el-input 
            v-model="careForm.medicine" 
            type="textarea" 
            :rows="2"
            placeholder="记录老人用药情况，如：药物名称、剂量、时间等"
          />
        </el-form-item>
        
        <el-form-item label="特殊情况" prop="special">
          <el-input 
            v-model="careForm.special" 
            type="textarea" 
            :rows="3"
            placeholder="记录老人特殊情况，如：异常症状、突发事件、情绪变化等"
          />
        </el-form-item>
        
        <el-form-item label="护理员" prop="nurseId">
          <el-select 
            v-model="careForm.nurseId" 
            placeholder="请选择护理员"
            style="width: 100%"
          >
            <el-option
              v-for="nurse in nurseList"
              :key="nurse.id"
              :label="nurse.name"
              :value="nurse.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 护理记录详情弹窗 -->
    <el-drawer v-model="detailVisible" title="护理记录详情" size="50%">
      <div v-if="currentCareRecord" class="care-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="老人姓名">{{ currentCareRecord.elderName }}</el-descriptions-item>
          <el-descriptions-item label="老人编号">{{ currentCareRecord.elderNo }}</el-descriptions-item>
          <el-descriptions-item label="记录日期">{{ currentCareRecord.recordDate }}</el-descriptions-item>
          <el-descriptions-item label="体温">{{ currentCareRecord.temperature || '-' }}℃</el-descriptions-item>
          <el-descriptions-item label="脉搏">{{ currentCareRecord.pulse || '-' }}次/分</el-descriptions-item>
          <el-descriptions-item label="呼吸">{{ currentCareRecord.breath || '-' }}次/分</el-descriptions-item>
          <el-descriptions-item label="血压">{{ currentCareRecord.bloodPressure || '-' }}</el-descriptions-item>
          <el-descriptions-item label="护理员">{{ currentCareRecord.nurseName }}</el-descriptions-item>
        </el-descriptions>
        
        <h3 style="margin-top: 20px;">详细记录</h3>
        <el-card style="margin-top: 10px;">
          <div class="detail-section">
            <h4>饮食情况</h4>
            <p>{{ currentCareRecord.diet || '无记录' }}</p>
          </div>
          <div class="detail-section">
            <h4>排泄情况</h4>
            <p>{{ currentCareRecord.excrete || '无记录' }}</p>
          </div>
          <div class="detail-section">
            <h4>睡眠情况</h4>
            <p>{{ currentCareRecord.sleep || '无记录' }}</p>
          </div>
          <div class="detail-section">
            <h4>用药记录</h4>
            <p>{{ currentCareRecord.medicine || '无记录' }}</p>
          </div>
          <div class="detail-section">
            <h4>特殊情况</h4>
            <p>{{ currentCareRecord.special || '无记录' }}</p>
          </div>
        </el-card>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { 
  Plus, 
  Search, 
  Edit, 
  Delete, 
  View,
  Health
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'CareManage',
  setup() {
    // 搜索表单
    const searchForm = reactive({
      elderId: '',
      dateRange: []
    })
    
    // 分页信息
    const pagination = reactive({
      page: 1,
      size: 10,
      total: 0
    })
    
    // 表格数据
    const loading = ref(false)
    const careRecordList = ref([])
    const total = ref(0)
    
    // 弹窗相关
    const dialogVisible = ref(false)
    const dialogTitle = ref('')
    const detailVisible = ref(false)
    const submitting = ref(false)
    const isEdit = ref(false)
    
    // 表单数据
    const careForm = reactive({
      id: null,
      elderId: '',
      recordDate: new Date().toISOString().split('T')[0],
      temperature: '',
      pulse: '',
      breath: '',
      bloodPressure: '',
      diet: '',
      excrete: '',
      sleep: '',
      medicine: '',
      special: '',
      nurseId: 1
    })
    
    // 表单验证规则
    const careRules = {
      elderId: [
        { required: true, message: '请选择老人', trigger: 'change' }
      ],
      recordDate: [
        { required: true, message: '请选择记录日期', trigger: 'change' }
      ],
      nurseId: [
        { required: true, message: '请选择护理员', trigger: 'change' }
      ]
    }
    
    const careFormRef = ref(null)
    
    // 当前查看的护理记录
    const currentCareRecord = ref(null)
    
    // 老人列表（模拟数据）
    const elderList = ref([
      { id: 1, name: '张三', elderNo: 'E1001' },
      { id: 2, name: '李四', elderNo: 'E1002' },
      { id: 3, name: '王五', elderNo: 'E1003' },
      { id: 4, name: '赵六', elderNo: 'E1004' },
      { id: 5, name: '孙七', elderNo: 'E1005' }
    ])
    
    // 护理员列表（模拟数据）
    const nurseList = ref([
      { id: 1, name: '李护士', role: '护理员' },
      { id: 2, name: '王护士', role: '护理员' },
      { id: 3, name: '张护士', role: '护理员' }
    ])
    
    // 模拟获取护理记录列表
    const getCareRecordList = () => {
      loading.value = true
      
      // 模拟API请求延迟
      setTimeout(() => {
        // 生成模拟数据
        const mockData = []
        for (let i = 1; i <= 30; i++) {
          const elder = elderList.value[Math.floor(Math.random() * elderList.value.length)]
          const nurse = nurseList.value[Math.floor(Math.random() * nurseList.value.length)]
          
          mockData.push({
            id: i,
            elderId: elder.id,
            elderName: elder.name,
            elderNo: elder.elderNo,
            recordDate: `2024-${String(Math.floor(i / 3) + 1).padStart(2, '0')}-${String(i % 28 + 1).padStart(2, '0')}`,
            temperature: Math.random() * 2 + 36,
            pulse: Math.floor(Math.random() * 20) + 60,
            breath: Math.floor(Math.random() * 10) + 15,
            bloodPressure: `${Math.floor(Math.random() * 40) + 100}/${Math.floor(Math.random() * 20) + 60}`,
            diet: i % 3 === 0 ? '正常进食' : i % 3 === 1 ? '食欲不佳' : '偏食',
            excrete: i % 2 === 0 ? '正常' : '便秘',
            sleep: i % 4 === 0 ? '睡眠良好' : i % 4 === 1 ? '睡眠一般' : '入睡困难',
            medicine: i % 5 === 0 ? '按时服药' : i % 5 === 1 ? '拒绝服药' : '需提醒服药',
            special: i % 6 === 0 ? '情绪稳定' : i % 6 === 1 ? '情绪波动' : '无特殊情况',
            nurseId: nurse.id,
            nurseName: nurse.name
          })
        }
        
        careRecordList.value = mockData
        total.value = mockData.length
        loading.value = false
      }, 500)
    }
    
    // 搜索处理
    const handleSearch = () => {
      console.log('搜索条件:', searchForm)
      getCareRecordList()
    }
    
    // 重置搜索
    const resetSearch = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = key === 'dateRange' ? [] : ''
      })
      getCareRecordList()
    }
    
    // 分页处理
    const handleSizeChange = (size) => {
      pagination.size = size
      getCareRecordList()
    }
    
    const handleCurrentChange = (page) => {
      pagination.page = page
      getCareRecordList()
    }
    
    // 新增护理记录
    const handleAdd = () => {
      isEdit.value = false
      dialogTitle.value = '新增护理记录'
      // 重置表单
      Object.keys(careForm).forEach(key => {
        careForm[key] = key === 'recordDate' ? new Date().toISOString().split('T')[0] : key === 'nurseId' ? 1 : ''
      })
      dialogVisible.value = true
    }
    
    // 编辑护理记录
    const handleEdit = (row) => {
      isEdit.value = true
      dialogTitle.value = '编辑护理记录'
      // 填充表单数据
      Object.keys(careForm).forEach(key => {
        careForm[key] = row[key] || ''
      })
      dialogVisible.value = true
    }
    
    // 查看护理记录详情
    const handleView = (row) => {
      currentCareRecord.value = row
      detailVisible.value = true
    }
    
    // 删除护理记录
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除 ${row.elderName} 的护理记录吗？`,
          '警告',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        // 模拟删除操作
        console.log('删除护理记录:', row)
        ElMessage.success('删除成功')
        getCareRecordList()
      } catch (error) {
        // 用户取消删除
      }
    }
    
    // 提交表单
    const handleSubmit = async () => {
      if (!careFormRef.value) return
      
      try {
        await careFormRef.value.validate()
        submitting.value = true
        
        // 模拟提交操作
        console.log('提交护理记录:', careForm)
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
        dialogVisible.value = false
        getCareRecordList()
      } catch (error) {
        console.error('表单验证失败:', error)
      } finally {
        submitting.value = false
      }
    }
    
    // 关闭弹窗
    const handleDialogClose = () => {
      dialogVisible.value = false
      if (careFormRef.value) {
        careFormRef.value.clearValidate()
      }
    }
    
    // 老人选择变化时的处理
    const onElderChange = (elderId) => {
      console.log('选择老人:', elderId)
    }
    
    // 组件挂载时获取数据
    onMounted(() => {
      getCareRecordList()
    })
    
    return {
      // 搜索表单
      searchForm,
      
      // 分页
      pagination,
      total,
      
      // 表格
      loading,
      careRecordList,
      
      // 弹窗
      dialogVisible,
      dialogTitle,
      detailVisible,
      submitting,
      isEdit,
      
      // 表单
      careForm,
      careRules,
      careFormRef,
      
      // 当前护理记录
      currentCareRecord,
      
      // 数据列表
      elderList,
      nurseList,
      
      // 方法
      handleSearch,
      resetSearch,
      handleSizeChange,
      handleCurrentChange,
      handleAdd,
      handleEdit,
      handleView,
      handleDelete,
      handleSubmit,
      handleDialogClose,
      onElderChange,
      
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
.care-manage {
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

.care-detail {
  padding: 20px 0;
}

.detail-section {
  margin-bottom: 15px;
}

.detail-section h4 {
  margin: 0 0 5px 0;
  color: #666;
  font-size: 14px;
}

.detail-section p {
  margin: 0;
  color: #333;
  line-height: 1.5;
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