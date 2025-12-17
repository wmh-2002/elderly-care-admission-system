import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Layout from '@/views/Layout.vue'
import Dashboard from '@/views/Dashboard.vue'
import ElderManage from '@/views/elder/ElderManage.vue'
import RoomManage from '@/views/room/RoomManage.vue'
import BedManage from '@/views/bed/BedManage.vue'
import CareManage from '@/views/care/CareManage.vue'
import BillManage from '@/views/bill/BillManage.vue'
import UserManage from '@/views/system/UserManage.vue'
import RoleManage from '@/views/system/RoleManage.vue'
import { checkPermission, getUserMenuPermissions } from '@/utils/permission'

// 定义路由
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录' }
  },
  {
    path: '/',
    name: 'Layout',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { 
          title: '首页', 
          requiresAuth: true,
          permissions: []
        }
      },
      {
        path: '/elder',
        name: 'ElderManage',
        component: ElderManage,
        meta: { 
          title: '老人管理', 
          requiresAuth: true,
          permissions: ['elder:view']
        }
      },
      {
        path: '/room',
        name: 'RoomManage',
        component: RoomManage,
        meta: { 
          title: '房间管理', 
          requiresAuth: true,
          permissions: ['room:view']
        }
      },
      {
        path: '/bed',
        name: 'BedManage',
        component: BedManage,
        meta: { 
          title: '床位管理', 
          requiresAuth: true,
          permissions: ['bed:view']
        }
      },
      {
        path: '/care',
        name: 'CareManage',
        component: CareManage,
        meta: { 
          title: '护理管理', 
          requiresAuth: true,
          permissions: ['care:view']
        }
      },
      {
        path: '/bill',
        name: 'BillManage',
        component: BillManage,
        meta: { 
          title: '费用管理', 
          requiresAuth: true,
          permissions: ['bill:view']
        }
      },
      {
        path: '/user',
        name: 'UserManage',
        component: UserManage,
        meta: { 
          title: '用户管理', 
          requiresAuth: true,
          permissions: ['user:view']
        }
      },
      {
        path: '/role',
        name: 'RoleManage',
        component: RoleManage,
        meta: { 
          title: '角色管理', 
          requiresAuth: true,
          permissions: ['role:view']
        }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 - 检查用户是否已登录和权限
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title || '养老院入住管理系统'
  
  // 检查是否需要认证
  if (to.meta.requiresAuth) {
    // 检查本地存储中是否有用户信息
    const token = localStorage.getItem('token')
    if (!token) {
      // 如果没有token，重定向到登录页
      next('/login')
      return
    }
    
    // 检查权限
    if (to.meta.permissions && to.meta.permissions.length > 0) {
      // 从localStorage获取用户ID
      const userId = localStorage.getItem('userId')
      if (userId) {
        // 获取用户权限
        const userPermissions = getUserMenuPermissions(parseInt(userId))
        
        // 检查是否有权限访问该页面
        const hasPermission = to.meta.permissions.some(permission => 
          userPermissions.includes(permission)
        )
        
        if (!hasPermission) {
          // 没有权限，重定向到首页或显示403页面
          console.error(`用户没有权限访问: ${to.path}`)
          next('/dashboard')
          return
        }
      }
    }
    
    // 如果有token且有权限，继续访问
    next()
  } else {
    // 不需要认证的页面直接访问
    next()
  }
})

export default router