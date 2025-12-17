<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="sidebarWidth" class="sidebar">
      <div class="sidebar-header">
        <h3 v-show="!sidebarCollapsed">养老院系统</h3>
        <h3 v-show="sidebarCollapsed">养老</h3>
      </div>
      
      <!-- 菜单 -->
      <el-menu
        :default-active="activeMenu"
        :collapse="sidebarCollapsed"
        :unique-opened="true"
        :router="true"
        class="sidebar-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <span>首页</span>
        </el-menu-item>
        
        <el-sub-menu index="/elder">
          <template #title>
            <el-icon><User /></el-icon>
            <span>老人管理</span>
          </template>
          <el-menu-item index="/elder">老人档案</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="/room">
          <template #title>
            <el-icon><House /></el-icon>
            <span>房间床位</span>
          </template>
          <el-menu-item index="/room">房间管理</el-menu-item>
          <el-menu-item index="/bed">床位管理</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="/care">
          <template #title>
            <el-icon><Health /></el-icon>
            <span>护理管理</span>
          </template>
          <el-menu-item index="/care">护理记录</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="/bill">
          <template #title>
            <el-icon><Money /></el-icon>
            <span>费用管理</span>
          </template>
          <el-menu-item index="/bill">费用账单</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="/system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/user">用户管理</el-menu-item>
          <el-menu-item index="/role">角色管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <!-- 主内容区域 -->
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-button @click="toggleSidebar" size="large" :icon="sidebarCollapsed ? 'Expand' : 'Fold'" />
        </div>
        
        <div class="header-center">
          <h2>{{ title }}</h2>
        </div>
        
        <div class="header-right">
          <el-dropdown>
            <span class="el-dropdown-link">
              {{ user?.realName || user?.username }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="showProfile">个人信息</el-dropdown-item>
                <el-dropdown-item @click="changePassword">修改密码</el-dropdown-item>
                <el-dropdown-item @click="handleLogout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 页面内容 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
      
      <!-- 页脚 -->
      <el-footer class="footer">
        <p>© 2025 养老院入住管理系统 - 版权所有</p>
      </el-footer>
    </el-container>
  </el-container>
</template>

<script>
import { computed, ref, watch } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  House,
  User,
  Health,
  Money,
  Setting,
  ArrowDown,
  Expand,
  Fold
} from '@element-plus/icons-vue'

export default {
  name: 'Layout',
  components: {
    House,
    User,
    Health,
    Money,
    Setting,
    ArrowDown,
    Expand,
    Fold
  },
  setup() {
    const store = useStore()
    const router = useRouter()
    const route = useRoute()
    
    // 计算属性
    const sidebarCollapsed = computed(() => store.state.sidebarCollapsed)
    const activeMenu = computed(() => store.state.activeMenu)
    const user = computed(() => store.state.user)
    
    // 侧边栏宽度
    const sidebarWidth = computed(() => sidebarCollapsed.value ? '64px' : '200px')
    
    // 当前页面标题
    const title = computed(() => route.meta.title || '首页')
    
    // 监听路由变化，更新激活的菜单项
    watch(
      () => route.path,
      (newPath) => {
        store.dispatch('setActiveMenu', newPath)
      },
      { immediate: true }
    )
    
    // 切换侧边栏
    const toggleSidebar = () => {
      store.dispatch('toggleSidebar')
    }
    
    // 退出登录
    const handleLogout = async () => {
      try {
        await ElMessageBox.confirm(
          '确定要退出登录吗？',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        // 清除用户信息和token
        store.dispatch('logout')
        
        // 跳转到登录页
        router.push('/login')
        
        ElMessage.success('已退出登录')
      } catch (error) {
        // 用户取消退出，不执行任何操作
      }
    }
    
    // 显示个人资料（暂未实现）
    const showProfile = () => {
      ElMessage.info('个人信息功能暂未实现')
    }
    
    // 修改密码（暂未实现）
    const changePassword = () => {
      ElMessage.info('修改密码功能暂未实现')
    }
    
    return {
      sidebarCollapsed,
      activeMenu,
      user,
      sidebarWidth,
      title,
      toggleSidebar,
      handleLogout,
      showProfile,
      changePassword
    }
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background-color: #2c3e50;
  color: #fff;
  transition: all 0.3s ease;
}

.sidebar-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #34495e;
}

.sidebar-header h3 {
  margin: 0;
  color: #fff;
  font-size: 18px;
}

.sidebar-menu {
  border-right: none;
  background-color: #2c3e50;
}

.sidebar-menu :deep(.el-menu-item) {
  color: #bdc3c7;
  height: 50px;
  line-height: 50px;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  color: #fff;
  background-color: #34495e;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  color: #3498db;
  background-color: #34495e;
  border-right: 3px solid #3498db;
}

.sidebar-menu :deep(.el-sub-menu__title) {
  color: #bdc3c7;
}

.sidebar-menu :deep(.el-sub-menu__title:hover) {
  background-color: #34495e;
  color: #fff;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  height: 60px;
}

.header-left {
  flex: 1;
}

.header-center {
  flex: 2;
  text-align: center;
}

.header-center h2 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.header-right {
  flex: 1;
  text-align: right;
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
  color: #606266;
}

.main-content {
  background-color: #f5f7fa;
  padding: 20px;
}

.footer {
  text-align: center;
  padding: 10px 20px;
  color: #909399;
  font-size: 12px;
  border-top: 1px solid #e6e6e6;
}
</style>