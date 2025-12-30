import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import App from './App.vue'
import './style.css'

// 导入路由配置
import routes from './router'

// 创建路由器实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 角色权限配置（与Layout.vue和router/index.js中的配置保持一致）
const rolePermissions = {
  '系统管理员': {
    dashboard: true,
    system: true,
    'system-users': true,
    'system-roles': true,
    'system-profile': true,
    'elderly-care': true,
    'elderly-care-elders': true,
    'elderly-care-nursing-categories': true,
    nursing: true,
    'nursing-plans': true,
    fee: true,
    'fee-settlement': true,
    'room-resource': true,
    'room-resource-rooms': true,
    'room-resource-beds': true,
    'data-analysis': true,
    'data-analysis-elder': true,
    'data-analysis-revenue': true,
    'data-analysis-operation': true
  },
  '院长': {
    dashboard: true,
    system: true,
    'system-users': true,
    'system-roles': true,
    'system-profile': true,
    'elderly-care': true,
    'elderly-care-elders': true,
    'room-resource': true,
    'room-resource-rooms': true,
    'room-resource-beds': true,
    'data-analysis': true,
    'data-analysis-elder': true,
    'data-analysis-revenue': true,
    'data-analysis-operation': true
  },
  '护理主管': {
    dashboard: true,
    'elderly-care': true,
    'elderly-care-elders': true,
    'elderly-care-nursing-categories': true,
    nursing: true,
    'nursing-plans': true,
    'room-resource': true,
    'room-resource-rooms': true,
    'room-resource-beds': true,
    'data-analysis': true,
    'data-analysis-elder': true,
    'data-analysis-operation': true,
    'system-profile': true
  },
  '护理员': {
    dashboard: true,
    'elderly-care': true,
    'elderly-care-elders': true,
    nursing: true,
    'nursing-plans': true,
    'system-profile': true
  },
  '财务人员': {
    dashboard: true,
    'elderly-care': true,
    'elderly-care-elders': true,
    fee: true,
    'fee-settlement': true,
    'data-analysis': true,
    'data-analysis-revenue': true,
    'system-profile': true
  },
  '前台接待': {
    dashboard: true,
    'elderly-care': true,
    'elderly-care-elders': true,
    'room-resource': true,
    'room-resource-rooms': true,
    'room-resource-beds': true,
    fee: true,
    'fee-settlement': true,
    'system-profile': true
  }
}

// 路由到权限键的映射
const routeToPermission = {
  '/dashboard': 'dashboard',
  '/users': 'system-users',
  '/roles': 'system-roles',
  '/profile': 'system-profile',
  '/elders': 'elderly-care-elders',
  '/nursing-categories': 'elderly-care-nursing-categories',
  '/nursing-plans': 'nursing-plans',
  '/fee-settlement': 'fee-settlement',
  '/rooms': 'room-resource-rooms',
  '/beds': 'room-resource-beds',
  '/data-analysis/elder': 'data-analysis-elder',
  '/data-analysis/revenue': 'data-analysis-revenue',
  '/data-analysis/operation': 'data-analysis-operation'
}

// 检查用户是否有权限访问某个路由
const hasPermission = (roleName, routePath) => {
  if (!roleName) return false
  const permissions = rolePermissions[roleName]
  if (!permissions) return false

  const permissionKey = routeToPermission[routePath]
  return permissionKey ? permissions[permissionKey] : false
}

// 路由守卫
router.beforeEach((to, from, next) => {
  // 检查是否需要登录验证的路由
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth !== false)

  // 检查用户是否已登录
  const isLogin = localStorage.getItem('isLogin') === 'true'

  if (requiresAuth && !isLogin && to.name !== 'Login') {
    // 如果需要登录但未登录，则重定向到登录页面
    next('/login')
  } else if (to.name === 'Login' && isLogin) {
    // 如果已登录且尝试访问登录页，则重定向到仪表盘
    next('/dashboard')
  } else if (requiresAuth && isLogin) {
    // 已登录，检查权限
    const userInfo = localStorage.getItem('userInfo')
    if (userInfo) {
      const parsedInfo = JSON.parse(userInfo)
      const userRole = parsedInfo.roleName

      if (userRole && !hasPermission(userRole, to.path)) {
        // 用户没有权限访问此路由，重定向到仪表盘
        next('/dashboard')
      } else {
        // 有权限，允许访问
        next()
      }
    } else {
      // 用户信息不完整，重定向到登录
      next('/login')
    }
  } else {
    // 其他情况允许访问
    next()
  }
})

// 创建应用实例
const app = createApp(App)

// 使用插件
app.use(router)
app.use(ElementPlus, {
  locale: zhCn
})

// 挂载应用
app.mount('#app')
