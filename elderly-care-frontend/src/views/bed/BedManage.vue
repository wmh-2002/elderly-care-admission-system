<template>
  <div class="bed-manage">
    <div class="page-header">
      <h2>床位管理</h2>
      <el-button type="primary" @click="handleAdd" :icon="Plus">新增床位</el-button>
    </div>
    
    <!-- 搜索和筛选区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="床位号">
          <el-input v-model="searchForm.bedNo" placeholder="请输入床位号" clearable />
        </el-form-item>
        <el-form-item label="房间号">
          <el-select v-model="searchForm.roomId" placeholder="请选择房间" clearable>
            <el-option
              v-for="room in roomList"
              :key="room.id"
              :label="room.roomNo"
              :value="room.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="空闲" :value="0" />
            <el-option label="已入住" :value="1" />
            <el-option label="维修" :value="2" />
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
          <span>床位列表 ({{ total }})</span>
        </div>
      </template>
      
      <el-table 
        :data="bedList" 
        v-loading="loading"
        style="width: 100%"
        row-key="id"
      >
        <el-table-column type="index" width="60" />
        <el-table-column prop="bedNo" label="床位号" width="120" />
        <el-table-column prop="roomNo" label="房间号" width="120" />
        <el-table-column prop="roomType" label="房间类型" width="120" />
        <el-table-column prop="elderName" label="入住老人" width="120">
          <template #default="{ row }">
            {{ row.elderName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
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
      width="50%" 
      :before-close="handleDialogClose"
    >
      <el-form 
        :model="bedForm" 
        :rules="bedRules" 
        ref="bedFormRef"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房间" prop="roomId">
              <el-select 
                v-model="bedForm.roomId" 
                placeholder="请选择房间"
                @change="onRoomChange"
                style="width: 100%"
                :disabled="!!bedForm.id"
              >
                <el-option
                  v-for="room in roomList"
                  :key="room.id"
                  :label="room.roomNo"
                  :value="room.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="床位号" prop="bedNo">
              <el-input v-model="bedForm.bedNo" placeholder="请输入床位号，如：101-1" :disabled="!!bedForm.id" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="bedForm.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="空闲" :value="0" />
                <el-option label="已入住" :value="1" />
                <el-option label="维修" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="bedForm.status === 1">
            <el-form-item label="入住老人">
              <el-select v-model="bedForm.elderId" placeholder="请选择老人" style="width: 100%">
                <el-option
                  v-for="elder in availableElders"
                  :key="elder.id"
                  :label="elder.name"
                  :value="elder.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 床位详情弹窗 -->
    <el-drawer v-model="detailVisible" title="床位详情" size="40%">
      <div v-if="currentBed" class="bed-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="床位号">{{ currentBed.bedNo }}</el-descriptions-item>
          <el-descriptions-item label="房间号">{{ currentBed.roomNo }}</el-descriptions-item>
          <el-descriptions-item label="房间类型">{{ currentBed.roomType }}</el-descriptions-item>
          <el-descriptions-item label="入住老人">{{ currentBed.elderName || '无' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentBed.status)">
              {{ getStatusText(currentBed.status) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
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
  Bed
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'BedManage',
  setup() {
    // 搜索表单
    const searchForm = reactive({
      bedNo: '',
      roomId: '',
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
    const bedList = ref([])
    const total = ref(0)
    
    // 弹窗相关
    const dialogVisible = ref(false)
    const dialogTitle = ref('')
    const detailVisible = ref(false)
    const submitting = ref(false)
    const isEdit = ref(false)
    
    // 表单数据
    const bedForm = reactive({
      id: null,
      roomId: '',
      bedNo: '',
      status: 0,
      elderId: null
    })
    
    // 表单验证规则
    const bedRules = {
      roomId: [
        { required: true, message: '请选择房间', trigger: 'change' }
      ],
      bedNo: [
        { required: true, message: '请输入床位号', trigger: 'blur' },
        { pattern: /^\d{3}-\d$/, message: '床位号格式为房间号-床位序号，如：101-1', trigger: 'blur' }
      ],
      status: [
        { required: true, message: '请选择状态', trigger: 'change' }
      ]
    }
    
    const bedFormRef = ref(null)
    
    // 当前查看的床位
    const currentBed = ref(null)
    
    // 房间列表（模拟数据）
    const roomList = ref([
      { id: 1, roomNo: '101', roomType: '单人间', maxBed: 1 },
      { id: 2, roomNo: '102', roomType: '双人间', maxBed: 2 },
      { id: 3, roomNo: '201', roomType: '多人间', maxBed: 3 },
      { id: 4, roomNo: '202', roomType: '双人间', maxBed: 2 }
    ])
    
    // 老人列表（模拟数据）
    const elderList = ref([
      { id: 1, name: '张三', elderNo: 'E1001', roomId: null, bedId: null },
      { id: 2, name: '李四', elderNo: 'E1002', roomId: 2, bedId: 2 },
      { id: 3, name: '王五', elderNo: 'E1003', roomId: 3, bedId: 4 },
      { id: 4, name: '赵六', elderNo: 'E1004', roomId: 3, bedId: 5 },
      { id: 5, name: '孙七', elderNo: 'E1005', roomId: 4, bedId: 8 }
    ])
    
    // 根据当前状态获取可用老人列表
    const availableElders = computed(() => {
      return elderList.value.filter(elder => 
        !elder.bedId || (bedForm.id && elder.bedId === bedForm.id)
      )
    })
    
    // 获取状态文本
    const getStatusText = (status) => {
      const statusMap = {
        0: '空闲',
        1: '已入住',
        2: '维修'
      }
      return statusMap[status] || '未知'
    }
    
    // 获取状态类型
    const getStatusType = (status) => {
      const typeMap = {
        0: 'success',
        1: 'primary',
        2: 'warning'
      }
      return typeMap[status] || 'info'
    }
    
    // 模拟获取床位列表
    const getBedList = () => {
      loading.value = true
      
      // 模拟API请求延迟
      setTimeout(() => {
        // 生成模拟数据
        const mockData = []
        let bedIdCounter = 1
        
        roomList.value.forEach(room => {
          for (let i = 1; i <= room.maxBed; i++) {
            const bedNo = `${room.roomNo}-${i}`
            const status = Math.floor(Math.random() * 3) // 0:空闲, 1:已入住, 2:维修
            const elder = elderList.value.find(e => e.bedId === bedIdCounter)
            
            mockData.push({
              id: bedIdCounter,
              bedNo: bedNo,
              roomId: room.id,
              roomNo: room.roomNo,
              roomType: room.roomType,
              elderId: elder ? elder.id : null,
              elderName: elder ? elder.name : null,
              status: status
            })
            
            bedIdCounter++
          }
        })
        
        bedList.value = mockData
        total.value = mockData.length
        loading.value = false
      }, 500)
    }
    
    // 搜索处理
    const handleSearch = () => {
      console.log('搜索条件:', searchForm)
      getBedList()
    }
    
    // 重置搜索
    const resetSearch = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = ''
      })
      getBedList()
    }
    
    // 分页处理
    const handleSizeChange = (size) => {
      pagination.size = size
      getBedList()
    }
    
    const handleCurrentChange = (page) => {
      pagination.page = page
      getBedList()
    }
    
    // 新增床位
    const handleAdd = () => {
      isEdit.value = false
      dialogTitle.value = '新增床位'
      // 重置表单
      Object.keys(bedForm).forEach(key => {
        bedForm[key] = key === 'status' ? 0 : null
      })
      dialogVisible.value = true
    }
    
    // 编辑床位
    const handleEdit = (row) => {
      isEdit.value = true
      dialogTitle.value = '编辑床位'
      // 填充表单数据
      Object.keys(bedForm).forEach(key => {
        bedForm[key] = row[key] || null
      })
      dialogVisible.value = true
    }
    
    // 查看床位详情
    const handleView = (row) => {
      currentBed.value = row
      detailVisible.value = true
    }
    
    // 删除床位
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除床位 ${row.bedNo} 吗？`,
          '警告',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        // 模拟删除操作
        console.log('删除床位:', row)
        ElMessage.success('删除成功')
        getBedList()
      } catch (error) {
        // 用户取消删除
      }
    }
    
    // 提交表单
    const handleSubmit = async () => {
      if (!bedFormRef.value) return
      
      try {
        await bedFormRef.value.validate()
        submitting.value = true
        
        // 模拟提交操作
        console.log('提交床位信息:', bedForm)
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
        dialogVisible.value = false
        getBedList()
      } catch (error) {
        console.error('表单验证失败:', error)
      } finally {
        submitting.value = false
      }
    }
    
    // 关闭弹窗
    const handleDialogClose = () => {
      dialogVisible.value = false
      if (bedFormRef.value) {
        bedFormRef.value.clearValidate()
      }
    }
    
    // 房间选择变化时更新床位号
    const onRoomChange = () => {
      if (!bedForm.roomId) return
      
      const room = roomList.value.find(r => r.id === bedForm.roomId)
      if (room) {
        // 获取该房间已有床位数，生成新的床位号
        const existingBeds = bedList.value.filter(b => b.roomId === room.id)
        const nextBedNo = `${room.roomNo}-${existingBeds.length + 1}`
        bedForm.bedNo = nextBedNo
      }
    }
    
    // 组件挂载时获取数据
    onMounted(() => {
      getBedList()
    })
    
    return {
      // 搜索表单
      searchForm,
      
      // 分页
      pagination,
      total,
      
      // 表格
      loading,
      bedList,
      
      // 弹窗
      dialogVisible,
      dialogTitle,
      detailVisible,
      submitting,
      isEdit,
      
      // 表单
      bedForm,
      bedRules,
      bedFormRef,
      
      // 当前床位
      currentBed,
      
      // 数据列表
      roomList,
      elderList,
      availableElders,
      
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
      onRoomChange,
      getStatusText,
      getStatusType,
      
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
.bed-manage {
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

.bed-detail {
  padding: 20px 0;
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