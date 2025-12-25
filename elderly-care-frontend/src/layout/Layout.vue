<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '200px'" class="aside">
      <div class="logo">
        <h3 v-show="!isCollapse">养老院系统</h3>
        <h3 v-show="isCollapse">养</h3>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        :collapse="isCollapse"
        background-color="#545c64"
        text-color="#fff"
        active-text-color="#ffd04b"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <span v-show="!isCollapse">控制面板</span>
        </el-menu-item>
        
        <el-sub-menu index="elderly-care">
          <template #title>
            <el-icon><User /></el-icon>
            <span v-show="!isCollapse">老人服务</span>
          </template>
          <el-menu-item index="/elders">
            <el-icon><User /></el-icon>
            <span v-show="!isCollapse">老人档案</span>
          </el-menu-item>
          <el-menu-item index="/nursing-categories">
            <el-icon><FolderOpened /></el-icon>
            <span v-show="!isCollapse">护理分类</span>
          </el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="nursing">
          <template #title>
            <el-icon><CaretRight /></el-icon>
            <span v-show="!isCollapse">护理管理</span>
          </template>
          <el-menu-item index="/nursing-plans">
            <el-icon><Document /></el-icon>
            <span v-show="!isCollapse">护理计划</span>
          </el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="fee">
          <template #title>
            <el-icon><Money /></el-icon>
            <span v-show="!isCollapse">费用管理</span>
          </template>
          <el-menu-item index="/fee-settlement">
            <el-icon><Wallet /></el-icon>
            <span v-show="!isCollapse">费用结算</span>
          </el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="room-resource">
          <template #title>
            <el-icon><OfficeBuilding /></el-icon>
            <span v-show="!isCollapse">空间资源</span>
          </template>
          <el-menu-item index="/rooms">
            <el-icon><OfficeBuilding /></el-icon>
            <span v-show="!isCollapse">房间管理</span>
          </el-menu-item>
          <el-menu-item index="/beds">
            <el-icon><UserFilled /></el-icon>
            <span v-show="!isCollapse">床位管理</span>
          </el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span v-show="!isCollapse">系统管理</span>
          </template>
          <el-menu-item index="/users">
            <el-icon><Avatar /></el-icon>
            <span v-show="!isCollapse">用户管理</span>
          </el-menu-item>
          <el-menu-item index="/roles">
            <el-icon><UserFilled /></el-icon>
            <span v-show="!isCollapse">角色管理</span>
          </el-menu-item>
          <el-menu-item index="/profile">
            <el-icon><User /></el-icon>
            <span v-show="!isCollapse">个人信息</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <!-- 主内容区域 -->
    <el-container>
      <!-- 顶部栏 -->
      <el-header class="header">
        <el-row type="flex" justify="space-between" align="middle" class="header-row">
          <el-col :span="18" class="header-left">
            <div class="header-content">
              <el-button @click="toggleCollapse" class="collapse-btn">
                <el-icon v-if="!isCollapse">
                  <Fold />
                </el-icon>
                <el-icon v-if="isCollapse">
                  <Expand />
                </el-icon>
              </el-button>
              <div class="header-title">
                <h3>{{ title }}</h3>
              </div>
            </div>
          </el-col>
          <el-col :span="6" class="header-right">
            <div class="user-info">
              <el-dropdown>
                <span class="el-dropdown-link">
                  <el-avatar :size="30" :src="avatar" />
                  <span style="margin-left: 10px;">{{ username }}</span>
                  <el-icon class="el-icon--right">
                    <arrow-down />
                  </el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="goToProfile">个人资料</el-dropdown-item>
                    <el-dropdown-item @click="handleChangePassword">修改密码</el-dropdown-item>
                    <el-dropdown-item @click="handleLogout" divided>退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </el-col>
        </el-row>
      </el-header>

      <!-- 内容区域 -->
      <el-main class="main-content">
        <router-view />
      </el-main>

      <!-- 底部 -->
      <el-footer class="footer">
        <div class="footer-content">
          养老院入住管理系统 &copy; {{ new Date().getFullYear() }} - 版权所有
        </div>
      </el-footer>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  House, 
  User, 
  OfficeBuilding, 
  UserFilled, 
  Avatar, 
  Expand, 
  Fold, 
  ArrowDown,
  Menu,
  List,
  Setting,
  CaretRight,
  FolderOpened,
  Document,
  Money,
  Wallet
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 侧边栏折叠状态
const isCollapse = ref(false)

// 用户信息
const username = ref('管理员')
const avatar = ref('https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png') // 默认头像

// 根据当前路由设置标题
const title = computed(() => {
  const routeTitles = {
    '/dashboard': '控制面板',
    '/elders': '老人管理',
    '/rooms': '房间管理',
    '/beds': '床位管理',
    '/users': '用户管理',
    '/profile': '个人信息',
    '/nursing-categories': '护理分类管理',
    '/nursing-plans': '护理计划管理',
    '/fee-settlement': '费用结算'
  }
  
  // 处理动态路由，如 /elders/1
  if (route.path.startsWith('/elders/') && route.path !== '/elders') {
    return '老人详情'
  }
  
  return routeTitles[route.path] || '养老院入住管理系统'
})

// 当前激活的菜单项
const activeMenu = computed(() => {
  return route.path
})

// 切换侧边栏折叠
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('isLogin')
  localStorage.removeItem('userInfo')
  router.push('/login')
}

// 跳转到个人资料页
const goToProfile = () => {
  console.log('Go to profile')
}

// 修改密码
const handleChangePassword = () => {
  console.log('Change password')
}

onMounted(() => {
  // 从本地存储获取用户信息
  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    const parsedInfo = JSON.parse(userInfo)
    username.value = parsedInfo.realName || parsedInfo.username || '管理员'
  }
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
  width: 100vw;
  min-width: 1000px; /* 确保最小宽度，避免布局被压缩 */
}

.aside {
  background-color: #545c64;
  height: 100vh;
  overflow-y: auto; /* 侧边栏内容过多时可滚动 */
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 200px;
  height: calc(100vh - 60px); /* 减去logo的高度 */
}

.header {
  background-color: #ffffff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  flex-shrink: 0; /* 防止头部被压缩 */
}

.header-row {
  width: 100%;
}

.header-content {
  display: flex;
  align-items: center;
}

.collapse-btn {
  padding: 8px;
  border: none;
  background: transparent;
  color: #333;
}

.collapse-btn:hover {
  background-color: #f5f5f5;
  color: #409EFF;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.header-title {
  margin-left: 15px;
}

.header-title h3 {
  margin: 0;
  color: #303133;
}

.user-info {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  text-align: right;
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
  color: #606266;
}

.main-content {
  background-color: #f0f2f5;
  flex: 1; /* 占据剩余空间 */
  min-height: calc(100vh - 120px); /* 减去头部和底部的高度 */
  overflow-y: auto; /* 内容过多时可滚动 */
}

.footer {
  background-color: #ffffff;
  border-top: 1px solid #e6e6e6;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 40px;
  flex-shrink: 0; /* 防止底部被压缩 */
}

.footer-content {
  color: #909399;
  font-size: 14px;
}
</style>