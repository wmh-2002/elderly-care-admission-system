<template>
  <div class="role-management-container">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>角色管理</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">角色管理</span>
        </div>
      </template>

      <!-- 搜索和筛选区域 -->
      <div class="search-section">
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
          <el-form-item label="角色名称">
            <el-input v-model="searchForm.roleName" placeholder="请输入角色名称" :prefix-icon="User" @keyup.enter="onSearch" />
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
          <el-button type="primary" size="default" @click="addRole" :icon="Plus">新增角色</el-button>
          <el-button type="danger" size="default" :disabled="!multipleSelection.length" :icon="Delete">批量删除</el-button>
        </div>
        <div class="button-group-right">
          <el-button size="default" @click="exportTable" :icon="Download">导出</el-button>
        </div>
      </div>

      <!-- 数据表格 -->
      <el-table
        :data="roleList"
        style="width: 100%;"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" min-width="150" />
        <el-table-column prop="remark" label="角色描述" min-width="200" />
        <el-table-column prop="createdAt" label="创建时间" min-width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" min-width="180">
          <template #default="{ row }">
            {{ formatDate(row.updatedAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="editRole(row)" :icon="Edit">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteRole(row.id)" :icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑角色对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="roleFormRef"
        :model="roleForm"
        :rules="roleRules"
        label-width="120px"
      >
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色描述" prop="remark">
          <el-input v-model="roleForm.remark" placeholder="请输入角色描述" type="textarea" :rows="3" />
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
  Edit,
  Download,
  User
} from '@element-plus/icons-vue'
import api from '@/api'

// 搜索表单
const searchForm = reactive({
  roleName: ''
})

// 角色列表数据
const roleList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 选中的行
const multipleSelection = ref([])

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const roleForm = ref({})
const roleFormRef = ref()

// 表单验证规则
const roleRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 50, message: '角色名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  remark: [
    { max: 200, message: '角色描述不能超过200个字符', trigger: 'blur' }
  ]
}

// 加载角色列表
const loadRoles = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      roleName: searchForm.roleName || undefined
    }

    const response = await api.role.getRoleList(params)
    if (response.data.code === 200) {
      roleList.value = response.data.data.content || []
      total.value = response.data.data.totalElements || 0
    } else {
      ElMessage.error(response.data.message || '获取角色列表失败')
    }
  } catch (error) {
    console.error('获取角色列表失败:', error)
    ElMessage.error('获取角色列表失败')
    // 出错时清空数据
    roleList.value = []
    total.value = 0
  }
}

// 搜索
const onSearch = () => {
  currentPage.value = 1 // 搜索时重置到第一页
  loadRoles()
}

// 重置
const onReset = () => {
  searchForm.roleName = ''
  loadRoles()
}

// 新增角色
const addRole = () => {
  dialogTitle.value = '新增角色'
  roleForm.value = {
    roleName: '',
    remark: ''
  }
  dialogVisible.value = true
}

// 编辑角色
const editRole = (row) => {
  dialogTitle.value = '编辑角色'
  roleForm.value = { ...row }
  dialogVisible.value = true
}

// 删除角色
const deleteRole = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该角色吗？此操作不可恢复。', '删除确认', {
      confirmButtonText: '确认删除',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await api.role.deleteRole(id)
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      loadRoles()
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除角色失败:', error)
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
  loadRoles()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadRoles()
}

// 提交表单
const submitForm = async () => {
  roleFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let response
        if (roleForm.value.id) {
          // 更新角色
          response = await api.role.updateRole(roleForm.value.id, roleForm.value)
        } else {
          // 创建角色
          response = await api.role.createRole(roleForm.value)
        }

        if (response.data.code === 200) {
          ElMessage.success(response.data.message || (roleForm.value.id ? '更新成功' : '创建成功'))
          dialogVisible.value = false
          loadRoles()
        } else {
          ElMessage.error(response.data.message || (roleForm.value.id ? '更新失败' : '创建失败'))
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

// 重置表单
const resetForm = () => {
  roleForm.value = {}
  if (roleFormRef.value) {
    roleFormRef.value.resetFields()
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

// 导出表格数据
const exportTable = () => {
  // 这里可以集成导出功能，如使用xlsx库导出Excel
  console.log('Exporting table data...')
  // 模拟导出功能
  ElMessage.success('表格数据导出功能已触发')
}

onMounted(() => {
  loadRoles()
})
</script>

<style scoped>
.role-management-container {
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
