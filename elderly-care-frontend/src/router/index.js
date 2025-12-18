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
      }
    ]
  }
]

export default routes