/**
 * 权限管理工具函数
 */

// 检查用户是否有特定权限
export function checkPermission(userPermissions, requiredPermission) {
  // 如果没有权限配置，允许访问
  if (!userPermissions || userPermissions.length === 0) {
    return true
  }
  
  // 检查是否包含所需权限
  return userPermissions.includes(requiredPermission)
}

// 检查用户角色
export function checkRole(userRole, allowedRoles) {
  if (!allowedRoles || allowedRoles.length === 0) {
    return true
  }
  
  return allowedRoles.includes(userRole)
}

// 根据用户角色和权限过滤菜单
export function filterMenuByPermission(menuList, userPermissions, userRole) {
  if (!menuList || menuList.length === 0) {
    return []
  }
  
  return menuList.filter(menu => {
    // 如果菜单项没有权限要求，直接显示
    if (!menu.permissions || menu.permissions.length === 0) {
      return true
    }
    
    // 检查是否有相关权限
    const hasPermission = menu.permissions.some(permission => 
      checkPermission(userPermissions, permission)
    )
    
    // 如果有子菜单，递归处理
    if (menu.children && menu.children.length > 0) {
      const filteredChildren = filterMenuByPermission(
        menu.children, 
        userPermissions, 
        userRole
      )
      
      // 如果有子菜单可见，也显示父菜单
      return hasPermission || filteredChildren.length > 0
    }
    
    return hasPermission
  })
}

// 权限指令
export function hasPermission(el, binding) {
  const { value } = binding
  const permissions = localStorage.getItem('userPermissions')
    ? JSON.parse(localStorage.getItem('userPermissions'))
    : []
  
  if (value && !checkPermission(permissions, value)) {
    el.style.display = 'none'
  }
}

// 获取用户菜单权限
export function getUserMenuPermissions(userId) {
  // 这里可以根据用户ID获取对应的菜单权限
  // 实际项目中应该从API获取
  const mockPermissions = {
    1: ['elder:view', 'elder:add', 'elder:edit', 'elder:delete', 'room:view', 'room:add', 'bed:view', 'care:view', 'bill:view', 'user:view', 'user:add', 'user:edit', 'role:view'], // 系统管理员
    2: ['elder:view', 'room:view', 'bill:view'], // 院长
    3: ['elder:view', 'care:view', 'care:add'], // 护理主管
    4: ['elder:view', 'care:view', 'care:add'], // 护理员
    5: ['bill:view', 'bill:add'], // 财务人员
    6: ['elder:view', 'elder:add', 'room:view', 'bed:view'] // 前台接待
  }
  
  return mockPermissions[userId] || []
}