package com.example.elderlycaresystem.controller;

import com.example.elderlycaresystem.dto.ApiResponse;
import com.example.elderlycaresystem.entity.SysUser;
import com.example.elderlycaresystem.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统用户管理控制器
 * 提供用户相关的API接口
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 根据ID获取用户信息
     * 
     * @param id 用户ID
     * @return 用户信息响应
     */
    @GetMapping("/{id}")
    public ApiResponse<SysUser> getUserById(@PathVariable Long id) {
        SysUser user = sysUserService.getUserById(id);
        return ApiResponse.success(user, "获取用户信息成功");
    }

    /**
     * 根据用户名获取用户信息
     * 
     * @param username 用户名
     * @return 用户信息响应
     */
    @GetMapping("/username/{username}")
    public ApiResponse<SysUser> getUserByUsername(@PathVariable String username) {
        SysUser user = sysUserService.getUserByUsername(username);
        return ApiResponse.success(user, "获取用户信息成功");
    }

    /**
     * 获取所有用户列表
     * 
     * @return 用户列表响应
     */
    @GetMapping("/list")
    public ApiResponse<List<SysUser>> getAllUsers() {
        List<SysUser> users = sysUserService.getAllUsers();
        return ApiResponse.success(users, "获取用户列表成功");
    }

    /**
     * 保存用户信息
     * 
     * @param sysUser 用户信息
     * @return 保存结果响应
     */
    @PostMapping("/save")
    public ApiResponse<Long> saveUser(@RequestBody SysUser sysUser) {
        Long userId = sysUserService.saveUser(sysUser);
        return ApiResponse.success(userId, "保存用户信息成功");
    }

    /**
     * 更新用户信息
     * 
     * @param sysUser 用户信息
     * @return 更新结果响应
     */
    @PutMapping("/update")
    public ApiResponse<Boolean> updateUser(@RequestBody SysUser sysUser) {
        boolean result = sysUserService.updateUser(sysUser);
        return ApiResponse.success(result, "更新用户信息" + (result ? "成功" : "失败"));
    }

    /**
     * 删除用户
     * 
     * @param id 用户ID
     * @return 删除结果响应
     */
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Boolean> deleteUser(@PathVariable Long id) {
        boolean result = sysUserService.deleteUser(id);
        return ApiResponse.success(result, "删除用户" + (result ? "成功" : "失败"));
    }

    /**
     * 用户登录
     * 
     * @param username 用户名
     * @param password 密码
     * @return 登录结果响应
     */
    @PostMapping("/login")
    public ApiResponse<SysUser> login(@RequestParam String username, @RequestParam String password) {
        SysUser user = sysUserService.login(username, password);
        return ApiResponse.success(user, "登录成功");
    }
}