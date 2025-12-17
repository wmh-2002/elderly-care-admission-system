<template>
  <div class="login-container">
    <el-card class="login-card">
      <div class="login-header">
        <h2>养老院入住管理系统</h2>
        <p>欢迎使用智能养老管理系统</p>
      </div>
      <el-form 
        :model="loginForm" 
        :rules="loginRules" 
        ref="loginFormRef"
        class="login-form"
      >
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleLogin" 
            :loading="loading"
            size="large"
            style="width: 100%"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <p>演示账号: admin / 123456</p>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const store = useStore()
    
    // 登录表单数据
    const loginForm = reactive({
      username: 'admin',
      password: '123456'
    })
    
    // 登录表单验证规则
    const loginRules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
      ]
    }
    
    // 加载状态
    const loading = ref(false)
    const loginFormRef = ref(null)
    
    // 处理登录
    const handleLogin = async () => {
      // 验证表单
      if (!loginFormRef.value) return
      
      try {
        await loginFormRef.value.validate()
        
        // 模拟登录请求
        loading.value = true
        
        // 模拟API请求延迟
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        // 模拟成功登录 - 生成假token
        const fakeToken = 'fake-jwt-token-' + Date.now()
        const fakeUser = {
          id: 1,
          username: loginForm.username,
          realName: '管理员',
          roleId: 1,
          roleName: '系统管理员'
        }
        
        // 存储用户信息和token到store
        store.dispatch('login', { user: fakeUser, token: fakeToken })
        
        // 显示成功消息
        ElMessage.success('登录成功')
        
        // 跳转到首页
        router.push('/dashboard')
      } catch (error) {
        console.error('登录失败:', error)
        ElMessage.error('登录失败，请检查用户名和密码')
      } finally {
        loading.value = false
      }
    }
    
    return {
      loginForm,
      loginRules,
      loading,
      loginFormRef,
      handleLogin
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 24px;
}

.login-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.login-form {
  margin-top: 20px;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 12px;
  color: #999;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-card {
    width: 90%;
    margin: 0 20px;
  }
}
</style>