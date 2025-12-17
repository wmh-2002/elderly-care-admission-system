<template>
  <div class="user-manage">
    <div class="page-header">
      <h2>用户管理</h2>
      <el-button type="primary" @click="handleAdd" :icon="Plus">新增用户</el-button>
    </div>
    
    <!-- 搜索和筛选区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="searchForm.realName" placeholder="请输入真实姓名" clearable />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="searchForm.roleId" placeholder="请选择角色" clearable>
            <el-option
              v-for="role in roleList"
              :key="role.id"
              :label="role.roleName"
              :value="role.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
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
          <span>用户列表 ({{ total }})</span>
        </div>
      </template>
      
      <el-table 
        :data="userList" 
        v-loading="loading"
        style="width: 100%"
        row-key="id"
      >
        <el-table-column type="index" width="60" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="roleName" label="角色" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
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
              @click="handleResetPassword(row)"
            >
              重置密码
            </el-button>
            <el-button 
              size="small" 
              :type="row.status === 1 ? 'warning' : 'info'" 
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
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
        :model="userForm" 
        :rules="userRules" 
        ref="userFormRef"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input 
                v-model="userForm.username" 
                :disabled="!!userForm.id"
                placeholder="请输入用户名"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="userForm.realName" placeholder="请输入真实姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="角色" prop="roleId">
              <el-select v-model="userForm.roleId" placeholder="请选择角色" style="width: 100%">
                <el-option
                  v-for="role in roleList"
                  :key="role.id"
                  :label="role.roleName"
                  :value="role.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="userForm.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="正常" :value="1" />
                <el-option label="禁用" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item v-if="!userForm.id" label="密码" prop="password">
          <el-input 
            v-model="userForm.password" 
            type="password" 
            show-password
            placeholder="请输入密码，至少6位"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 用户详情弹窗 -->
    <el-drawer v-model="detailVisible" title="用户详情" size="40%">
      <div v-if="currentUser" class="user-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
          <el-descriptions-item label="真实姓名">{{ currentUser.realName }}</el-descriptions-item>
          <el-descriptions-item label="角色">{{ currentUser.roleName }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'">
              {{ currentUser.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentUser.createTime }}</el-descriptions-item>
        </el-descriptions>
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
  User,
  Key
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'UserManage',
  setup() {
    // 搜索表单
    const searchForm = reactive({
      username: '',
      realName: '',
      roleId: '',
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
    const userList = ref([])
    const total = ref(0)
    
    // 弹窗相关
    const dialogVisible = ref(false)
    const dialogTitle = ref('')
    const detailVisible = ref(false)
    const submitting = ref(false)
    const isEdit = ref(false)
    
    // 表单数据
    const userForm = reactive({
      id: null,
      username: '',
      realName: '',
      roleId: 1,
      status: 1,
      password: ''
    })
    
    // 表单验证规则
    const userRules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度应在3-20个字符之间', trigger: 'blur' }
      ],
      realName: [
        { required: true, message: '请输入真实姓名', trigger: 'blur' },
        { min: 2, max: 10, message: '真实姓名长度应在2-10个字符之间', trigger: 'blur' }
      ],
      roleId: [
        { required: true, message: '请选择角色', trigger: 'change' }
      ],
      status: [
        { required: true, message: '请选择状态', trigger: 'change' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
      ]
    }
    
    const userFormRef = ref(null)
    
    // 当前查看的用户
    const currentUser = ref(null)
    
    // 角色列表（模拟数据）
    const roleList = ref([
      { id: 1, roleName: '系统管理员', remark: '拥有所有权限' },
      { id: 2, roleName: '院长', remark: '查看各类统计报表' },
      { id: 3, roleName: '护理主管', remark: '管理护理计划与排班' },
      { id: 4, roleName: '护理员', remark: '填写护理记录' },
      { id: 5, roleName: '财务人员', remark: '费用计算与收款' },
      { id: 6, roleName: '前台接待', remark: '老人入住登记' }
    ])
    
    // 模拟获取用户列表
    const getUserList = () => {
      loading.value = true
      
      // 模拟API请求延迟
      setTimeout(() => {
        // 生成模拟数据
        const mockData = []
        for (let i = 1; i <= 15; i++) {
          const roleId = Math.floor(Math.random() * 6) + 1
          const role = roleList.value.find(r => r.id === roleId)
          const status = i % 6 === 0 ? 0 : 1 // 每6个用户有一个被禁用
          
          mockData.push({
            id: i,
            username: `user${i}`,
            realName: ['张三', '李四', '王五', '赵六', '孙七', '周八'][i % 6],
            roleId: roleId,
            roleName: role.roleName,
            status: status,
            createTime: `2024-${String(Math.floor(i / 3) + 1).padStart(2, '0')}-${String(i % 28 + 1).padStart(2, '0')} 09:00:00`
          })
        }
        
        userList.value = mockData
        total.value = mockData.length
        loading.value = false
      }, 500)
    }
    
    // 搜索处理
    const handleSearch = () => {
      console.log('搜索条件:', searchForm)
      getUserList()
    }
    
    // 重置搜索
    const resetSearch = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = ''
      })
      getUserList()
    }
    
    // 分页处理
    const handleSizeChange = (size) => {
      pagination.size = size
      getUserList()
    }
    
    const handleCurrentChange = (page) => {
      pagination.page = page
      getUserList()
    }
    
    // 新增用户
    const handleAdd = () => {
      isEdit.value = false
      dialogTitle.value = '新增用户'
      // 重置表单
      Object.keys(userForm).forEach(key => {
        userForm[key] = key === 'roleId' ? 1 : key === 'status' ? 1 : ''
      })
      dialogVisible.value = true
    }
    
    // 编辑用户
    const handleEdit = (row) => {
      isEdit.value = true
      dialogTitle.value = '编辑用户'
      // 填充表单数据
      Object.keys(userForm).forEach(key => {
        userForm[key] = row[key] || ''
      })
      dialogVisible.value = true
    }
    
    // 查看用户详情
    const handleView = (row) => {
      currentUser.value = row
      detailVisible.value = true
    }
    
    // 重置密码
    const handleResetPassword = async (row) => {
      try {
        await ElMessageBox.confirm(
          `确定要重置用户 ${row.realName} 的密码吗？`,
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        // 模拟重置密码操作
        console.log('重置密码:', row)
        ElMessage.success('密码已重置为初始密码')
      } catch (error) {
        // 用户取消重置
      }
    }
    
    // 切换用户状态
    const handleToggleStatus = async (row) => {
      try {
        await ElMessageBox.confirm(
          `确定要${row.status === 1 ? '禁用' : '启用'}用户 ${row.realName} 吗？`,
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        // 模拟切换状态操作
        console.log('切换用户状态:', row)
        ElMessage.success(`用户已${row.status === 1 ? '禁用' : '启用'}`)
        getUserList()
      } catch (error) {
        // 用户取消操作
      }
    }
    
    // 删除用户
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm(
          `确定要删除用户 ${row.realName} 吗？`,
          '警告',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        // 模拟删除操作
        console.log('删除用户:', row)
        ElMessage.success('删除成功')
        getUserList()
      } catch (error) {
        // 用户取消删除
      }
    }
    
    // 提交表单
    const handleSubmit = async () => {
      if (!userFormRef.value) return
      
      try {
        await userFormRef.value.validate()
        submitting.value = true
        
        // 模拟提交操作
        console.log('提交用户信息:', userForm)
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
        dialogVisible.value = false
        getUserList()
      } catch (error) {
        console.error('表单验证失败:', error)
      } finally {
        submitting.value = false
      }
    }
    
    // 关闭弹窗
    const handleDialogClose = () => {
      dialogVisible.value = false
      if (userFormRef.value) {
        userFormRef.value.clearValidate()
      }
    }
    
    // 组件挂载时获取数据
    onMounted(() => {
      getUserList()
    })
    
    return {
      // 搜索表单
      searchForm,
      
      // 分页
      pagination,
      total,
      
      // 表格
      loading,
      userList,
      
      // 弹窗
      dialogVisible,
      dialogTitle,
      detailVisible,
      submitting,
      isEdit,
      
      // 表单
      userForm,
      userRules,
      userFormRef,
      
      // 当前用户
      currentUser,
      
      // 数据列表
      roleList,
      
      // 方法
      handleSearch,
      resetSearch,
      handleSizeChange,
      handleCurrentChange,
      handleAdd,
      handleEdit,
      handleView,
      handleResetPassword,
      handleToggleStatus,
      handleDelete,
      handleSubmit,
      handleDialogClose,
      
      // 图标
      Plus,
      Search,
      Edit,
      Delete,
      View,
      Key
    }
  }
}
</script>

<style scoped>
.user-manage {
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

.user-detail {
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