package com.example.elderlycaresystem.service;

import com.example.elderlycaresystem.entity.SysUser;
import java.util.List;

/**
 * 系统用户服务接口
 */
public interface SysUserService {
    
    /**
     * 根据ID获取用户信息
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    SysUser getUserById(Long id);
    
    /**
     * 根据用户名获取用户信息
     * 
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getUserByUsername(String username);
    
    /**
     * 获取所有用户列表
     * 
     * @return 用户列表
     */
    List<SysUser> getAllUsers();
    
    /**
     * 保存用户信息
     * 
     * @param sysUser 用户信息
     * @return 保存后的用户ID
     */
    Long saveUser(SysUser sysUser);
    
    /**
     * 更新用户信息
     * 
     * @param sysUser 用户信息
     * @return 更新是否成功
     */
    boolean updateUser(SysUser sysUser);
    
    /**
     * 删除用户
     * 
     * @param id 用户ID
     * @return 删除是否成功
     */
    boolean deleteUser(Long id);
    
    /**
     * 用户登录验证
     * 
     * @param username 用户名
     * @param password 密码
     * @return 用户信息，如果验证失败返回null
     */
    SysUser login(String username, String password);
}