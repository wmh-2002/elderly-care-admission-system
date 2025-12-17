<template>
  <div class="room-manage">
    <div class="page-header">
      <h2>房间管理</h2>
      <el-button type="primary" @click="handleAdd" :icon="Plus">新增房间</el-button>
    </div>
    
    <!-- 搜索和筛选区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="房间号">
          <el-input v-model="searchForm.roomNo" placeholder="请输入房间号" clearable />
        </el-form-item>
        <el-form-item label="房间类型">
          <el-select v-model="searchForm.roomType" placeholder="请选择房间类型" clearable>
            <el-option label="单人间" value="单人间" />
            <el-option label="双人间" value="双人间" />
            <el-option label="多人间" value="多人间" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼层">
          <el-input v-model="searchForm.floor" placeholder="请输入楼层" clearable />
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
          <span>房间列表 ({{ total }})</span>
        </div>
      </template>
      
      <el-table 
        :data="roomList" 
        v-loading="loading"
        style="width: 100%"
        row-key="id"
      >
        <el-table-column type="index" width="60" />
        <el-table-column prop="roomNo" label="房间号" width="120" />
        <el-table-column prop="roomType" label="房间类型" width="120" />
        <el-table-column prop="floor" label="楼层" width="100" />
        <el-table-column prop="maxBed" label="最大床位数" width="120" />
        <el-table-column prop="occupiedBed" label="已用床位" width="120">
          <template #default="{ row }">
            <span>{{ row.occupiedBed }} / {{ row.maxBed }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.occupiedBed === 0 ? 'success' : (row.occupiedBed < row.maxBed ? 'warning' : 'danger')">
              {{ row.occupiedBed === 0 ? '空闲' : (row.occupiedBed < row.maxBed ? '部分占用' : '已满') }}
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
        :model="roomForm" 
        :rules="roomRules" 
        ref="roomFormRef"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房间号" prop="roomNo">
              <el-input v-model="roomForm.roomNo" placeholder="请输入房间号，如：101" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间类型" prop="roomType">
              <el-select v-model="roomForm.roomType" placeholder="请选择房间类型" style="width: 100%">
                <el-option label="单人间" value="单人间" />
                <el-option label="双人间" value="双人间" />
                <el-option label="多人间" value="多人间" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="楼层" prop="floor">
              <el-input v-model="roomForm.floor" type="number" placeholder="请输入楼层" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大床位数" prop="maxBed">
              <el-input v-model="roomForm.maxBed" type="number" placeholder="请输入最大床位数" />
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
    
    <!-- 房间详情弹窗 -->
    <el-drawer v-model="detailVisible" title="房间详情" size="40%">
      <div v-if="currentRoom" class="room-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="房间号">{{ currentRoom.roomNo }}</el-descriptions-item>
          <el-descriptions-item label="房间类型">{{ currentRoom.roomType }}</el-descriptions-item>
          <el-descriptions-item label="楼层">{{ currentRoom.floor }}</el-descriptions-item>
          <el-descriptions-item label="最大床位数">{{ currentRoom.maxBed }}</el-descriptions-item>
          <el-descriptions-item label="已用床位">{{ currentRoom.occupiedBed }}</el-descriptions-item>
          <el-descriptions-item label="空闲床位">{{ currentRoom.maxBed - currentRoom.occupiedBed }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentRoom.occupiedBed === 0 ? 'success' : (currentRoom.occupiedBed < currentRoom.maxBed ? 'warning' : 'danger')">
              {{ currentRoom.occupiedBed === 0 ? '空闲' : (currentRoom.occupiedBed < currentRoom.maxBed ? '部分占用' : '已满') }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
        
        <h3 style="margin-top: 20px;">床位列表</h3>
        <el-table :data="currentRoom.beds" style="margin-top: 10px;">
          <el-table-column prop="bedNo" label="床位号" />
          <el-table-column prop="elderName" label="入住老人" />
          <el-table-column prop="status" label="状态">
            <template #default="{ row }">
              <el-tag :type="row.status === 0 ? 'success' : (row.status === 1 ? 'primary' : 'warning')">
                {{ row.status === 0 ? '空闲' : (row.status === 1 ? '已入住' : '维修') }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
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
  House
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'RoomManage',
  setup() {
    // 搜索表单
    const searchForm = reactive({
      roomNo: '',
      roomType: '',
      floor: ''
    })
    
    // 分页信息
    const pagination = reactive({
      page: 1,
      size: 10,
      total: 0
    })
    
    // 表格数据
    const loading = ref(false)
    const roomList = ref([])
    const total = ref(0)
    
    // 弹窗相关
    const dialogVisible = ref(false)
    const dialogTitle = ref('')
    const detailVisible = ref(false)
    const submitting = ref(false)
    const isEdit = ref(false)
    
    // 表单数据
    const roomForm = reactive({
      id: null,
      roomNo: '',
      roomType: '单人间',
      floor: 1,
      maxBed: 1
    })
    
    // 表单验证规则
    const roomRules = {
      roomNo: [
        { required: true, message: '请输入房间号', trigger: 'blur' },
        { pattern: /^\d{3}$/, message: '房间号格式为3位数字，如：101', trigger: 'blur' }
      ],
      roomType: [
        { required: true, message: '请选择房间类型', trigger: 'change' }
      ],
      floor: [
        { required: true, message: '请输入楼层', trigger: 'blur' },
        { type: 'number', min: 1, max: 20, message: '楼层应在1-20之间', trigger: 'blur' }
      ],
      maxBed: [
        { required: true, message: '请输入最大床位数', trigger: 'blur' },
        { type: 'number', min: 1, max: 10, message: '床位数应在1-10之间', trigger: 'blur' }
      ]
    }
    
    const roomFormRef = ref(null)
    
    // 当前查看的房间
    const currentRoom = ref(null)
    
    // 模拟床位数据
    const bedList = ref([
      { id: 1, roomId: 1, bedNo: '101-1', elderName: '张三', status: 1 },
      { id: 2, roomId: 2, bedNo: '102-1', elderName: '李四', status: 1 },
      { id: 3, roomId: 2, bedNo: '102-2', elderName: '', status: 0 },
      { id: 4, roomId: 3, bedNo: '201-1', elderName: '王五', status: 1 },
      { id: 5, roomId: 3, bedNo: '201-2', elderName: '赵六', status: 1 },
      { id: 6, roomId: 3, bedNo: '201-3', elderName: '', status: 0 },
      { id: 7, roomId: 4, bedNo: '202-1', elderName: '', status: 0 },
      { id: 8, roomId: 4, bedNo: '202-2', elderName: '孙七', status: 1 }
    ])
    
    // 模拟获取房间列表
    const getRoomList = () => {
      loading.value = true
      
      // 模拟API请求延迟
      setTimeout(() => {
        // 生成模拟数据
        const mockData = []
        for (let i = 1; i <= 20; i++) {
          const floor = Math.floor((i - 1) / 5) + 1
          const roomType = ['单人间', '双人间', '多人间'][Math.floor(Math.random() * 3)]
          const maxBed = roomType === '单人间' ? 1 : roomType === '双人间' ? 2 : Math.floor(Math.random() * 3) + 2
          
          // 计算已占用床位数
          const occupiedBed = Math.min(Math.floor(Math.random() * (maxBed + 1)), maxBed)
          
          // 获取该房间的床位列表
          const roomBeds = bedList.value.filter(bed => bed.roomId === i)
          
          mockData.push({
            id: i,
            roomNo: `${floor}${String(i % 5 + 1).padStart(2, '0')}`,
            roomType: roomType,
            floor: floor,
            maxBed: maxBed,
            occupiedBed: occupiedBed,
            beds: roomBeds
          })
        }
        
        roomList.value = mockData
        total.value = mockData.length
        loading.value = false
      }, 500)
    }
    
    // 搜索处理
    const handleSearch = () => {
      console.log('搜索条件:', searchForm)
      getRoomList()
    }
    
    // 重置搜索
    const resetSearch = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = ''
      })
      getRoomList()
    }
    
    // 分页处理
    const handleSizeChange = (size) => {
      pagination.size = size
      getRoomList()
    }
    
    const handleCurrentChange = (page) => {
      pagination.page = page
      getRoomList()
    }
    
    // 新增房间
    const handleAdd = () => {
      isEdit.value = false
      dialogTitle.value = '新增房间'
      // 重置表单
      Object.keys(roomForm).forEach(key => {
        roomForm[key] = key === 'roomType' ? '单人间' : key === 'floor' ? 1 : key === 'maxBed' ? 1 : ''
      })
      dialogVisible.value = true
    }
    
    // 编辑房间
    const handleEdit = (row) => {
      isEdit.value = true
      dialogTitle.value = '编辑房间'
      // 填充表单数据
      Object.keys(roomForm).forEach(key => {
        roomForm[key] = row[key] || ''
      })
      dialogVisible.value = true
    }
    
    // 查看房间详情
    const handleView = (row) => {
      currentRoom.value = row
      detailVisible.value = true
    }
    
    // 删除房间
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除房间 ${row.roomNo} 吗？`,
          '警告',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        // 模拟删除操作
        console.log('删除房间:', row)
        ElMessage.success('删除成功')
        getRoomList()
      } catch (error) {
        // 用户取消删除
      }
    }
    
    // 提交表单
    const handleSubmit = async () => {
      if (!roomFormRef.value) return
      
      try {
        await roomFormRef.value.validate()
        submitting.value = true
        
        // 模拟提交操作
        console.log('提交房间信息:', roomForm)
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
        dialogVisible.value = false
        getRoomList()
      } catch (error) {
        console.error('表单验证失败:', error)
      } finally {
        submitting.value = false
      }
    }
    
    // 关闭弹窗
    const handleDialogClose = () => {
      dialogVisible.value = false
      if (roomFormRef.value) {
        roomFormRef.value.clearValidate()
      }
    }
    
    // 组件挂载时获取数据
    onMounted(() => {
      getRoomList()
    })
    
    return {
      // 搜索表单
      searchForm,
      
      // 分页
      pagination,
      total,
      
      // 表格
      loading,
      roomList,
      
      // 弹窗
      dialogVisible,
      dialogTitle,
      detailVisible,
      submitting,
      isEdit,
      
      // 表单
      roomForm,
      roomRules,
      roomFormRef,
      
      // 当前房间
      currentRoom,
      
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
.room-manage {
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

.room-detail {
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