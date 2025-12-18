import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import './style.css'

// 导入路由配置
import routes from './router'

// 创建路由器实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

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
  } else {
    // 否则允许访问
    next()
  }
})

// 创建应用实例
const app = createApp(App)

// 使用插件
app.use(router)
app.use(ElementPlus)

// 挂载应用
app.mount('#app')
