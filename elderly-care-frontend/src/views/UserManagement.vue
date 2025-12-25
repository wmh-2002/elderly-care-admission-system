<template>
  <div class="user-management-container">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>用户管理</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">用户管理</span>
        </div>
      </template>
      
      <!-- 搜索和筛选区域 -->
      <div class="search-section">
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
          <el-form-item label="用户名">
            <el-input v-model="searchForm.username" placeholder="请输入用户名" :prefix-icon="User" @keyup.enter="onSearch" />
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="searchForm.realName" placeholder="请输入姓名" @keyup.enter="onSearch" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="searchForm.email" placeholder="请输入邮箱" @keyup.enter="onSearch" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="searchForm.phone" placeholder="请输入手机号" @keyup.enter="onSearch" />
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
          <el-button type="primary" size="default" @click="addUser" :icon="Plus">新增用户</el-button>
          <el-button type="danger" size="default" :disabled="!multipleSelection.length" :icon="Delete">批量删除</el-button>
        </div>
        <div class="button-group-right">
          <el-button size="default" @click="exportTable" :icon="Download">导出</el-button>
          <el-button size="default" @click="toggleView" :icon="Menu">视图</el-button>
        </div>
      </div>
      
      <!-- 数据表格 -->
      <el-table
        :data="userList"
        style="width: 100%;"
        @selection-change="handleSelectionChange"
        stripe
        border
      >
        <el-table-column type="selection" width="55" fixed="left" />
        <el-table-column prop="username" label="用户名" min-width="120" fixed="left" />
        <el-table-column prop="realName" label="真实姓名" min-width="120" fixed="left" />
        <el-table-column prop="phone" label="联系电话" min-width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="roleName" label="角色" min-width="120">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.roleName)">
              {{ row.roleName }}
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
            <el-button size="small" @click="editUser(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteUser(row.id)">删除</el-button>
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
      <el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" :disabled="!!userForm.id" />
        </el-form-item>
        
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="userForm.realName" />
        </el-form-item>
        
        <el-form-item label="密码" prop="password" v-if="!userForm.id">
          <el-input v-model="userForm.password" type="password" show-password />
        </el-form-item>
        
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="userForm.phone" />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" />
        </el-form-item>
        
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理员" value="admin" />
            <el-option label="护理员" value="caregiver" />
            <el-option label="前台" value="receptionist" />
            <el-option label="医生" value="doctor" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-select v-model="userForm.status" placeholder="请选择状态" style="width: 100%">
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
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Plus,
  Delete,
  User,
  Avatar,
  Download,
  Menu
} from '@element-plus/icons-vue'
import userAPI from '@/api/user'

// 搜索表单
const searchForm = reactive({
  username: '',
  realName: '',
  email: '',
  phone: ''
})

// 用户列表数据
const userList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 选中的行
const multipleSelection = ref([])

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const userForm = ref({})
const userFormRef = ref()

// 表单验证规则
const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 加载用户列表
const loadUsers = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      username: searchForm.username || undefined,
      realName: searchForm.realName || undefined,
      email: searchForm.email || undefined,
      phone: searchForm.phone || undefined
    }

    const response = await userAPI.getUserList(params)
    if (response.data.code === 200) {
      userList.value = response.data.data.content || []
      total.value = response.data.data.totalElements || 0
    } else {
      ElMessage.error(response.data.message || '获取用户列表失败')
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
    // 出错时清空数据
    userList.value = []
    total.value = 0
  }
}

// 获取角色文本
const getRoleText = (role) => {
  switch(role) {
    case 'admin': return '管理员'
    case 'caregiver': return '护理员'
    case 'receptionist': return '前台'
    case 'doctor': return '医生'
    default: return role
  }
}

// 获取角色类型
const getRoleType = (roleName) => {
  if (roleName?.includes('管理员') || roleName?.includes('admin')) return 'danger'
  if (roleName?.includes('护理') || roleName?.includes('caregiver')) return 'warning'
  if (roleName?.includes('前台') || roleName?.includes('receptionist')) return 'primary'
  if (roleName?.includes('医生') || roleName?.includes('doctor')) return 'success'
  return 'info'
}

// 搜索
const onSearch = () => {
  currentPage.value = 1 // 搜索时重置到第一页
  loadUsers()
}

// 重置
const onReset = () => {
  searchForm.username = ''
  searchForm.realName = ''
  searchForm.email = ''
  searchForm.phone = ''
  loadUsers()
}

// 新增用户
const addUser = () => {
  dialogTitle.value = '新增用户'
  userForm.value = {
    role: 'caregiver',
    status: 1
  }
  dialogVisible.value = true
}

// 编辑用户
const editUser = (row) => {
  dialogTitle.value = '编辑用户'
  userForm.value = { ...row }
  dialogVisible.value = true
}

// 查看详情
const viewDetails = (row) => {
  console.log('View details for:', row)
  dialogTitle.value = '用户详情'
  userForm.value = { ...row }
  // 禁用表单编辑
}

// 删除用户
const deleteUser = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该用户吗？此操作不可恢复。', '删除确认', {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await userAPI.deleteUser(id)
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      loadUsers()
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
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
  loadUsers()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadUsers()
}

// 提交表单
const submitForm = async () => {
  userFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let response
        if (userForm.value.id) {
          // 更新用户
          response = await userAPI.updateUser(userForm.value.id, userForm.value)
        } else {
          // 创建用户
          response = await userAPI.createUser(userForm.value)
        }

        if (response.data.code === 200) {
          ElMessage.success(response.data.message || (userForm.value.id ? '更新成功' : '创建成功'))
          dialogVisible.value = false
          loadUsers()
        } else {
          ElMessage.error(response.data.message || (userForm.value.id ? '更新失败' : '创建失败'))
        }
      } catch (error) {
        console.error('提交表单失败:', error)
        ElMessage.error('操作失败')
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
  loadUsers()
})
</script>

<style scoped>
.user-management-container {
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