import { createStore } from 'vuex'

export const store = createStore({
  state: {
    // 用户信息
    user: null,
    // 认证token
    token: localStorage.getItem('token') || null,
    // 侧边栏是否折叠
    sidebarCollapsed: false,
    // 当前激活的菜单项
    activeMenu: 'dashboard'
  },
  mutations: {
    // 设置用户信息
    SET_USER(state, user) {
      state.user = user
    },
    // 设置token
    SET_TOKEN(state, token) {
      state.token = token
      if (token) {
        localStorage.setItem('token', token)
      } else {
        localStorage.removeItem('token')
      }
    },
    // 切换侧边栏折叠状态
    TOGGLE_SIDEBAR(state) {
      state.sidebarCollapsed = !state.sidebarCollapsed
    },
    // 设置侧边栏折叠状态
    SET_SIDEBAR_COLLAPSED(state, collapsed) {
      state.sidebarCollapsed = collapsed
    },
    // 设置当前激活的菜单
    SET_ACTIVE_MENU(state, menu) {
      state.activeMenu = menu
    }
  },
  actions: {
    // 用户登录
    login({ commit }, { user, token }) {
      commit('SET_USER', user)
      commit('SET_TOKEN', token)
    },
    // 用户登出
    logout({ commit }) {
      commit('SET_USER', null)
      commit('SET_TOKEN', null)
    },
    // 切换侧边栏
    toggleSidebar({ commit }) {
      commit('TOGGLE_SIDEBAR')
    },
    // 设置侧边栏折叠状态
    setSidebarCollapsed({ commit }, collapsed) {
      commit('SET_SIDEBAR_COLLAPSED', collapsed)
    },
    // 设置当前激活的菜单
    setActiveMenu({ commit }, menu) {
      commit('SET_ACTIVE_MENU', menu)
    }
  },
  getters: {
    // 获取用户信息
    getUser: state => state.user,
    // 检查是否已认证
    isAuthenticated: state => !!state.token,
    // 获取侧边栏折叠状态
    getSidebarCollapsed: state => state.sidebarCollapsed,
    // 获取当前激活的菜单
    getActiveMenu: state => state.activeMenu
  }
})