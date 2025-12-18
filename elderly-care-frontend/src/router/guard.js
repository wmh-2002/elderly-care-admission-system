// 路由守卫
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const route = useRoute()

// 前置路由守卫
router.beforeEach((to, from, next) => {
  // 检查是否需要登录验证的路由
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth !== false)
  
  // 检查用户是否已登录
  const isLogin = localStorage.getItem('isLogin') === 'true'
  
  if (requiresAuth && !isLogin) {
    // 如果需要登录但未登录，则重定向到登录页面
    next('/login')
  } else {
    // 否则允许访问
    next()
  }
})

export default router