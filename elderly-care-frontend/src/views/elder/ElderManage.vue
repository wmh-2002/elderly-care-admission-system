<template>
  <div class="elder-manage">
    <div class="page-header">
      <h2>老人管理</h2>
      <el-button type="primary" @click="handleAdd" :icon="Plus">新增老人</el-button>
    </div>
    
    <!-- 搜索和筛选区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="在院" :value="1" />
            <el-option label="退住" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="护理等级">
          <el-select v-model="searchForm.careLevel" placeholder="请选择护理等级" clearable>
            <el-option label="三级护理" value="L3" />
            <el-option label="二级护理" value="L2" />
            <el-option label="一级护理" value="L1" />
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
          <span>老人列表 ({{ total }})</span>
        </div>
      </template>
      
      <el-table 
        :data="elderList" 
        v-loading="loading"
        style="width: 100%"
        row-key="id"
      >
        <el-table-column type="index" width="60" />
        <el-table-column prop="elderNo" label="老人编号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            {{ row.gender === 'M' ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="80" />
        <el-table-column prop="idCard" label="身份证号" width="180" />
        <el-table-column prop="roomNo" label="房间号" width="100" />
        <el-table-column prop="bedNo" label="床号" width="100" />
        <el-table-column prop="careLevel" label="护理等级" width="120">
          <template #default="{ row }">
            {{ getCareLevelName(row.careLevel) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '在院' : '退住' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkinDate" label="入住日期" width="120" />
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
      width="60%" 
      :before-close="handleDialogClose"
    >
      <el-form 
        :model="elderForm" 
        :rules="elderRules" 
        ref="elderFormRef"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="老人编号" prop="elderNo">
              <el-input 
                v-model="elderForm.elderNo" 
                :disabled="!!elderForm.id"
                placeholder="系统自动生成"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="elderForm.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="elderForm.gender">
                <el-radio label="M">男</el-radio>
                <el-radio label="F">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker
                v-model="elderForm.birthDate"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="elderForm.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="elderForm.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房间号" prop="roomNo">
              <el-select 
                v-model="elderForm.roomId" 
                placeholder="请选择房间"
                @change="onRoomChange"
                style="width: 100%"
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
            <el-form-item label="床号" prop="bedNo">
              <el-select 
                v-model="elderForm.bedId" 
                placeholder="请选择床位"
                :disabled="!elderForm.roomId"
                style="width: 100%"
              >
                <el-option
                  v-for="bed in bedListByRoom"
                  :key="bed.id"
                  :label="bed.bedNo"
                  :value="bed.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="护理等级" prop="careLevel">
              <el-select 
                v-model="elderForm.careLevel" 
                placeholder="请选择护理等级"
                style="width: 100%"
              >
                <el-option label="一级护理" value="L1" />
                <el-option label="二级护理" value="L2" />
                <el-option label="三级护理" value="L3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="elderForm.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="在院" :value="1" />
                <el-option label="退住" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="家庭地址" prop="address">
          <el-input 
            v-model="elderForm.address" 
            type="textarea" 
            placeholder="请输入家庭地址"
          />
        </el-form-item>
        
        <el-form-item label="健康状况" prop="healthStatus">
          <el-input 
            v-model="elderForm.healthStatus" 
            type="textarea" 
            placeholder="请输入当前健康状况"
          />
        </el-form-item>
        
        <el-form-item label="紧急联系人" prop="contactName">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-input 
                v-model="elderForm.contactName" 
                placeholder="联系人姓名" 
              />
            </el-col>
            <el-col :span="8">
              <el-input 
                v-model="elderForm.contactPhone" 
                placeholder="联系人电话" 
              />
            </el-col>
            <el-col :span="8">
              <el-input 
                v-model="elderForm.contactRelation" 
                placeholder="与老人关系" 
              />
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
        </div>
      </template>
    </el-dialog>
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
  User
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'ElderManage',
  setup() {
    // 搜索表单
    const searchForm = reactive({
      name: '',
      status: '',
      careLevel: ''
    })
    
    // 分页信息
    const pagination = reactive({
      page: 1,
      size: 10,
      total: 0
    })
    
    // 表格数据
    const loading = ref(false)
    const elderList = ref([])
    const total = ref(0)
    
    // 弹窗相关
    const dialogVisible = ref(false)
    const dialogTitle = ref('')
    const submitting = ref(false)
    const isEdit = ref(false)
    
    // 表单数据
    const elderForm = reactive({
      id: null,
      elderNo: '',
      name: '',
      gender: 'M',
      birthDate: '',
      idCard: '',
      phone: '',
      roomId: '',
      bedId: '',
      careLevel: 'L2',
      status: 1,
      address: '',
      healthStatus: '',
      contactName: '',
      contactPhone: '',
      contactRelation: ''
    })
    
    // 表单验证规则
    const elderRules = {
      name: [
        { required: true, message: '请输入姓名', trigger: 'blur' },
        { min: 2, max: 10, message: '姓名长度应在2-10个字符之间', trigger: 'blur' }
      ],
      gender: [
        { required: true, message: '请选择性别', trigger: 'change' }
      ],
      idCard: [
        { required: true, message: '请输入身份证号', trigger: 'blur' },
        { pattern: /^\d{17}[\dXx]$/, message: '身份证号格式不正确', trigger: 'blur' }
      ],
      phone: [
        { required: true, message: '请输入联系电话', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
      ],
      roomId: [
        { required: true, message: '请选择房间', trigger: 'change' }
      ],
      bedId: [
        { required: true, message: '请选择床位', trigger: 'change' }
      ],
      careLevel: [
        { required: true, message: '请选择护理等级', trigger: 'change' }
      ],
      status: [
        { required: true, message: '请选择状态', trigger: 'change' }
      ]
    }
    
    const elderFormRef = ref(null)
    
    // 房间和床位数据（模拟）
    const roomList = ref([
      { id: 1, roomNo: '101', maxBed: 1 },
      { id: 2, roomNo: '102', maxBed: 2 },
      { id: 3, roomNo: '201', maxBed: 3 },
      { id: 4, roomNo: '202', maxBed: 2 }
    ])
    
    const bedList = ref([
      { id: 1, roomId: 1, bedNo: '101-1', status: 0 },
      { id: 2, roomId: 2, bedNo: '102-1', status: 0 },
      { id: 3, roomId: 2, bedNo: '102-2', status: 1 },
      { id: 4, roomId: 3, bedNo: '201-1', status: 0 },
      { id: 5, roomId: 3, bedNo: '201-2', status: 0 },
      { id: 6, roomId: 3, bedNo: '201-3', status: 1 },
      { id: 7, roomId: 4, bedNo: '202-1', status: 0 },
      { id: 8, roomId: 4, bedNo: '202-2', status: 0 }
    ])
    
    // 根据房间ID获取床位列表
    const bedListByRoom = computed(() => {
      if (!elderForm.roomId) return []
      return bedList.value.filter(bed => bed.roomId === elderForm.roomId && bed.status === 0)
    })
    
    // 计算年龄
    const calculateAge = (birthDate) => {
      if (!birthDate) return ''
      const birth = new Date(birthDate)
      const today = new Date()
      const age = today.getFullYear() - birth.getFullYear()
      return age
    }
    
    // 获取护理等级名称
    const getCareLevelName = (levelCode) => {
      const levels = {
        'L1': '一级护理',
        'L2': '二级护理',
        'L3': '三级护理'
      }
      return levels[levelCode] || levelCode
    }
    
    // 模拟获取老人列表
    const getElderList = () => {
      loading.value = true
      
      // 模拟API请求延迟
      setTimeout(() => {
        // 生成模拟数据
        const mockData = []
        for (let i = 1; i <= 50; i++) {
          const gender = i % 2 === 0 ? 'M' : 'F'
          const status = i % 3 === 0 ? 0 : 1 // 1/3 的老人是退住状态
          const careLevel = ['L1', 'L2', 'L3'][Math.floor(Math.random() * 3)]
          const roomId = Math.floor(Math.random() * 4) + 1
          const bed = bedList.value.find(b => b.roomId === roomId && b.status === (status === 1 ? 1 : 0))
          
          mockData.push({
            id: i,
            elderNo: `E${1000 + i}`,
            name: `老人${i}`,
            gender: gender,
            age: calculateAge(`19${40 + i % 40}-01-01`),
            birthDate: `19${40 + i % 40}-01-01`,
            idCard: `11010119${40 + i % 40}0101${String(i).padStart(4, '0')}`,
            phone: `138${String(i).padStart(8, '0')}`,
            roomId: roomId,
            bedId: bed ? bed.id : null,
            roomNo: roomList.value.find(r => r.id === roomId)?.roomNo || '',
            bedNo: bed?.bedNo || '',
            careLevel: careLevel,
            status: status,
            checkinDate: `2024-${String(Math.floor(i / 5) + 1).padStart(2, '0')}-${String(i % 28 + 1).padStart(2, '0')}`,
            address: `北京市朝阳区养老院${roomId}号`,
            healthStatus: i % 2 === 0 ? '健康' : '慢性病',
            contactName: `家属${i}`,
            contactPhone: `139${String(i).padStart(8, '0')}`,
            contactRelation: '子女'
          })
        }
        
        elderList.value = mockData
        total.value = mockData.length
        loading.value = false
      }, 500)
    }
    
    // 搜索处理
    const handleSearch = () => {
      console.log('搜索条件:', searchForm)
      getElderList()
    }
    
    // 重置搜索
    const resetSearch = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = ''
      })
      getElderList()
    }
    
    // 分页处理
    const handleSizeChange = (size) => {
      pagination.size = size
      getElderList()
    }
    
    const handleCurrentChange = (page) => {
      pagination.page = page
      getElderList()
    }
    
    // 新增老人
    const handleAdd = () => {
      isEdit.value = false
      dialogTitle.value = '新增老人'
      // 重置表单
      Object.keys(elderForm).forEach(key => {
        elderForm[key] = key === 'gender' ? 'M' : key === 'careLevel' ? 'L2' : key === 'status' ? 1 : ''
      })
      dialogVisible.value = true
    }
    
    // 编辑老人
    const handleEdit = (row) => {
      isEdit.value = true
      dialogTitle.value = '编辑老人'
      // 填充表单数据
      Object.keys(elderForm).forEach(key => {
        elderForm[key] = row[key] || ''
      })
      // 设置房间床位信息
      elderForm.roomId = row.roomId
      elderForm.bedId = row.bedId
      dialogVisible.value = true
    }
    
    // 查看老人
    const handleView = (row) => {
      isEdit.value = false
      dialogTitle.value = '查看老人信息'
      // 填充表单数据
      Object.keys(elderForm).forEach(key => {
        elderForm[key] = row[key] || ''
      })
      // 设置房间床位信息
      elderForm.roomId = row.roomId
      elderForm.bedId = row.bedId
      // 禁用表单编辑
      dialogVisible.value = true
    }
    
    // 删除老人
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除老人 ${row.name} 的信息吗？`,
          '警告',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        // 模拟删除操作
        console.log('删除老人:', row)
        ElMessage.success('删除成功')
        getElderList()
      } catch (error) {
        // 用户取消删除
      }
    }
    
    // 提交表单
    const handleSubmit = async () => {
      if (!elderFormRef.value) return
      
      try {
        await elderFormRef.value.validate()
        submitting.value = true
        
        // 模拟提交操作
        console.log('提交老人信息:', elderForm)
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
        dialogVisible.value = false
        getElderList()
      } catch (error) {
        console.error('表单验证失败:', error)
      } finally {
        submitting.value = false
      }
    }
    
    // 关闭弹窗
    const handleDialogClose = () => {
      dialogVisible.value = false
      if (elderFormRef.value) {
        elderFormRef.value.clearValidate()
      }
    }
    
    // 房间选择变化时重置床位选择
    const onRoomChange = () => {
      elderForm.bedId = ''
    }
    
    // 组件挂载时获取数据
    onMounted(() => {
      getElderList()
    })
    
    return {
      // 搜索表单
      searchForm,
      
      // 分页
      pagination,
      total,
      
      // 表格
      loading,
      elderList,
      
      // 弹窗
      dialogVisible,
      dialogTitle,
      submitting,
      isEdit,
      
      // 表单
      elderForm,
      elderRules,
      elderFormRef,
      
      // 房间床位
      roomList,
      bedListByRoom,
      
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
      getCareLevelName,
      
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
.elder-manage {
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