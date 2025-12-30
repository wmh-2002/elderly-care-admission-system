<template>
  <div class="login-container">
    <!-- 左侧温馨图片区域 -->
    <div class="left-section">
      <div class="image-placeholder">
        <div class="elderly-care-image">
          <!-- 使用象征性的养老院图标或图片 -->
          <div class="icon-container">
            <svg class="care-icon" viewBox="0 0 120 120" xmlns="http://www.w3.org/2000/svg">
              <circle cx="60" cy="45" r="20" fill="#409EFF"/>
              <path d="M60 65 C40 65, 40 95, 60 95 C80 95, 80 65, 60 65 Z" fill="#409EFF"/>
              <path d="M30 70 L35 65 L40 70 Z" fill="#FFB74D"/>
              <path d="M80 70 L85 65 L90 70 Z" fill="#FFB74D"/>
              <circle cx="50" cy="40" r="3" fill="white"/>
              <circle cx="70" cy="40" r="3" fill="white"/>
              <path d="M55 50 Q60 55, 65 50" stroke="white" stroke-width="2" fill="none"/>
            </svg>
          </div>
          <h3>温馨家园，关爱无限</h3>
          <p>让每一位长者都能享受到最贴心的照料</p>
        </div>
      </div>
    </div>

    <!-- 右侧登录表单区域 -->
    <div class="right-section">
      <div class="login-wrapper">
        <div class="login-card">
          <div class="card-header">
            <div class="logo-section">
              <div class="logo-icon">
                <svg class="logo-svg" viewBox="0 0 24 24">
                  <path fill="#409EFF" d="M10,20V14H14V20H19V12H22L12,3L2,12H5V20H10Z" />
                </svg>
              </div>
              <div class="logo-text">
                <h2>养老院管理系统</h2>
                <p>安心 · 专业 · 温馨</p>
              </div>
            </div>
            
            <h3>员工登录</h3>
            <p>请使用您的工号和密码登录系统</p>
          </div>
          
          <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" class="login-form">
            <el-form-item prop="username">
              <el-input 
                v-model="loginForm.username" 
                placeholder="请输入工号或用户名" 
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
                show-password
                size="large"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button 
                type="primary" 
                @click="handleLogin" 
                class="login-button"
                size="large"
                :loading="loading"
              >
                <template #icon>
                  <el-icon><User /></el-icon>
                </template>
                立即登录
              </el-button>
            </el-form-item>
            
            <div class="remember-forgot">
              <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
              <a href="#" class="forgot-password">忘记密码?</a>
            </div>
          </el-form>
          
          <div class="support-info">
            <p><i class="el-icon-phone-outline"></i> 技术支持热线：400-XXX-XXXX</p>
            <p><i class="el-icon-time"></i> 服务时间：24小时全天候服务</p>
          </div>
        </div>
        
        <div class="footer-info">
          <p>© 2023 养老院管理系统 版权所有</p>
          <p>用心关爱每一位长者</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User } from '@element-plus/icons-vue'
import api from '@/api'

const router = useRouter()

// 登录表单
const loginForm = reactive({
  username: 'admin',
  password: '123456',
  rememberMe: false
})

// 加载状态
const loading = ref(false)

// 登录规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
  ]
}

const loginFormRef = ref()

// 处理登录
const handleLogin = async () => {
  loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 调用后端登录API
        const response = await api.auth.login({
          username: loginForm.username,
          password: loginForm.password
        })

        // 检查响应状态
        if (response.data.code === 200) {
          const loginData = response.data.data // This is the LoginData object from backend
          
          // 存储用户信息和JWT token
          localStorage.setItem('isLogin', 'true')
          localStorage.setItem('token', loginData.token) // 存储JWT token
          
          // 存储用户信息
          localStorage.setItem('userInfo', JSON.stringify({
            username: loginData.username || loginForm.username,
            realName: loginData.realName || '管理员',
            roleName: loginData.roleName || '系统管理员',
            roleId: loginData.roleId,
            userId: loginData.id
          }))
          
          ElMessage.success('登录成功')
          
          // 跳转到首页
          router.push('/dashboard')
        } else {
          ElMessage.error(response.data.message || '登录失败')
        }
      } catch (error) {
        console.error('登录失败:', error)
        let errorMessage = '登录失败'
        if (error.response) {
          // 服务器返回了错误响应
          if (error.response.data && error.response.data.message) {
            errorMessage = error.response.data.message
          } else {
            errorMessage = `登录失败: ${error.response.status}`
          }
        } else if (error.request) {
          // 请求已发出但没有收到响应
          errorMessage = '网络错误，请检查网络连接'
        } else {
          // 其他错误
          errorMessage = error.message || '登录失败'
        }
        ElMessage.error(errorMessage)
      } finally {
        loading.value = false
      }
    } else {
      console.log('Validation failed!')
      ElMessage.error('请填写正确的登录信息')
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  height: 100vh;
  width: 100vw;
  background: linear-gradient(135deg, #ace5e2ff, #e8d8ddff, #b899afff);
}

.left-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  padding: 40px;
  position: relative;
  overflow: hidden;
}

.image-placeholder {
  max-width: 500px;
  text-align: center;
  z-index: 2;
}

.elderly-care-image {
  padding: 30px;
}

.icon-container {
  margin-bottom: 30px;
}

.care-icon {
  width: 120px;
  height: 120px;
  margin: 0 auto;
}

.care-icon circle, .care-icon path {
  transition: all 0.3s ease;
}

.elderly-care-image h3 {
  font-size: 1.8rem;
  margin: 20px 0;
  font-weight: 500;
}

.elderly-care-image p {
  font-size: 1.1rem;
  opacity: 0.9;
  line-height: 1.6;
}

.right-section {
  width: 500px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
  margin-right: 100px;
  background: transparent;
}

.login-wrapper {
  width: 100%;
}

.login-card {
  background: white;
  backdrop-filter: blur(10px);
  border-radius: 15px;
  padding: 60px 30px;
}

.card-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 25px;
}

.logo-icon {
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #a8edea, #fed6e3);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 15px;
}

.logo-svg {
  width: 40px;
  height: 40px;
  fill: white;
}

.logo-text h2 {
  margin: 0;
  color: #303133;
  font-size: 1.5rem;
  font-weight: 600;
}

.logo-text p {
  margin: 5px 0 0;
  color: #909399;
  font-size: 0.9rem;
}

.card-header h3 {
  margin: 25px 0 10px;
  color: #303133;
  font-size: 1.5rem;
  font-weight: 500;
}

.card-header p {
  margin: 0;
  color: #909399;
  font-size: 0.9rem;
}

.login-form {
  margin-top: 10px;
}

.login-button {
  width: 100%;
  height: 50px;
  font-size: 16px;
  letter-spacing: 1px;
  background: linear-gradient(135deg, #6a89cc, #4a69bd);
  border: none;
  transition: all 0.3s ease;
  color: white;
}

.login-button:hover {
  background: linear-gradient(135deg, #4a69bd, #6a89cc);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(106, 137, 204, 0.3);
}

.remember-forgot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.forgot-password {
  color: #409EFF;
  text-decoration: none;
  font-size: 0.9rem;
}

.forgot-password:hover {
  text-decoration: underline;
}

.support-info {
  margin-top: 25px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
  font-size: 0.85rem;
  color: #909399;
}

.support-info p {
  margin: 8px 0;
  display: flex;
  align-items: center;
}

.support-info i {
  margin-right: 8px;
}

.footer-info {
  margin-top: 30px;
  text-align: center;
  font-size: 0.8rem;
  color: #c0c4cc;
  line-height: 1.6;
}

/* 装饰元素 */
.left-section::before {
  content: '';
  position: absolute;
  width: 200%;
  height: 200%;
  top: -50%;
  left: -50%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0, transparent 70%);
  animation: rotate 25s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 响应式设计 */
@media (max-width: 992px) {
  .login-container {
    flex-direction: column;
  }
  
  .left-section {
    padding: 20px;
    min-height: 300px;
  }
  
  .right-section {
    width: 100%;
    padding: 30px;
  }
  
  .elderly-care-image h3 {
    font-size: 1.5rem;
  }
}

@media (max-width: 768px) {
  .right-section {
    padding: 20px;
  }
  
  .login-card {
    padding: 30px 20px;
  }
}
</style>