package com.elderlycare.management.controller;

import com.elderlycare.management.dto.*;
import com.elderlycare.management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取用户列表（分页）
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false) Boolean status) {

        UserQueryRequest request = new UserQueryRequest();
        request.setPage(page);
        request.setSize(size);
        request.setUsername(username);
        request.setRealName(realName);
        request.setEmail(email);
        request.setPhone(phone);
        request.setRoleId(roleId);
        request.setStatus(status);

        try {
            PageResponse<UserResponse> result = userService.getUserList(request);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            System.err.println("Error in getUserList: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<PageResponse<UserResponse>>error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * 创建新用户
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.ok(ApiResponse.success("用户创建成功", user));
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("用户信息更新成功", user));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("用户删除成功"));
    }

    /**
     * 启用用户
     */
    @PutMapping("/{id}/enable")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> enableUser(@PathVariable Long id) {
        UserResponse user = userService.updateUserStatus(id, true);
        return ResponseEntity.ok(ApiResponse.success("用户已启用", user));
    }

    /**
     * 禁用用户
     */
    @PutMapping("/{id}/disable")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> disableUser(@PathVariable Long id) {
        UserResponse user = userService.updateUserStatus(id, false);
        return ResponseEntity.ok(ApiResponse.success("用户已禁用", user));
    }
}

