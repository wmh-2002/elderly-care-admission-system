// 路由配置
const routes = [
  {
    path: '/login',
    name: 'Login',
    meta: { requiresAuth: false }, // 不需要验证
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: () => import('../layout/Layout.vue'),
    meta: { requiresAuth: true }, // 需要验证
    children: [
      {
        path: '',
        redirect: '/dashboard'
      },
      {
        path: '/dashboard',
        name: 'Dashboard',
        meta: { requiresAuth: true },
        component: () => import('../views/Dashboard.vue')
      },
      {
        path: '/elders',
        name: 'Elders',
        meta: { requiresAuth: true },
        component: () => import('../views/ElderManagement.vue')
      },
      {
        path: '/rooms',
        name: 'Rooms',
        meta: { requiresAuth: true },
        component: () => import('../views/RoomManagement.vue')
      },
      {
        path: '/beds',
        name: 'Beds',
        meta: { requiresAuth: true },
        component: () => import('../views/BedManagement.vue')
      },
      {
        path: '/users',
        name: 'Users',
        meta: { requiresAuth: true },
        component: () => import('../views/UserManagement.vue')
      },
      {
        path: '/roles',
        name: 'Roles',
        meta: { requiresAuth: true },
        component: () => import('../views/RoleManagement.vue')
      },
      {
        path: '/profile',
        name: 'Profile',
        meta: { requiresAuth: true },
        component: () => import('../views/UserProfile.vue')
      },
      {
        path: '/nursing-categories',
        name: 'NursingCategories',
        meta: { requiresAuth: true },
        component: () => import('../views/NursingCategoryManagement.vue')
      },
      {
        path: '/nursing-plans',
        name: 'NursingPlans',
        meta: { requiresAuth: true },
        component: () => import('../views/NursingPlanManagement.vue')
      },
      {
        path: '/elders/:id',
        name: 'ElderDetail',
        meta: { requiresAuth: true },
        component: () => import('../views/ElderDetail.vue')
      },
      {
        path: '/fee-settlement',
        name: 'FeeSettlement',
        meta: { requiresAuth: true },
        component: () => import('../views/FeeSettlementManagement.vue')
      },
      {
        path: '/data-analysis/elder',
        name: 'DataAnalysisElder',
        meta: { requiresAuth: true },
        component: () => import('../views/data-analysis/ElderAnalysis.vue')
      },
      {
        path: '/data-analysis/revenue',
        name: 'DataAnalysisRevenue',
        meta: { requiresAuth: true },
        component: () => import('../views/data-analysis/RevenueAnalysis.vue')
      },
      {
        path: '/data-analysis/operation',
        name: 'DataAnalysisOperation',
        meta: { requiresAuth: true },
        component: () => import('../views/data-analysis/OperationAnalysis.vue')
      },
      {
        path: '/data-analysis',
        redirect: '/data-analysis/elder'
      }
    ]
  }
]

// 前端路由权限配置（后端API对所有角色开放，只在前端控制路由访问）
const routePermissions = {
  '系统管理员': ['/dashboard', '/users', '/roles', '/profile', '/elders', '/nursing-categories', '/nursing-plans', '/fee-settlement', '/rooms', '/beds', '/data-analysis/elder', '/data-analysis/revenue', '/data-analysis/operation'],
  '院长': ['/dashboard', '/users', '/roles', '/profile', '/elders', '/rooms', '/beds', '/data-analysis/elder', '/data-analysis/revenue', '/data-analysis/operation'],
  '护理主管': ['/dashboard', '/elders', '/nursing-categories', '/nursing-plans', '/rooms', '/beds', '/data-analysis/elder', '/data-analysis/operation', '/profile'],
  '护理员': ['/dashboard', '/elders', '/nursing-plans', '/profile'],
  '财务人员': ['/dashboard', '/elders', '/fee-settlement', '/data-analysis/revenue', '/profile'],
  '前台接待': ['/dashboard', '/elders', '/rooms', '/beds', '/fee-settlement', '/profile']
}

// 检查用户是否有权限访问某个路由
const hasRoutePermission = (roleName, routePath) => {
  if (!roleName) return false
  const allowedRoutes = routePermissions[roleName]
  return allowedRoutes ? allowedRoutes.includes(routePath) : false
}

// Make sure to export the routes properly
export { routes }
// Also export as default for backwards compatibility
export default routes