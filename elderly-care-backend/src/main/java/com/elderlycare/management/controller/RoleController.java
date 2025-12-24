package com.elderlycare.management.controller;

import com.elderlycare.management.dto.*;
import com.elderlycare.management.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * 获取角色列表（分页）
     */
    @GetMapping
    @PreAuthorize("hasRole('系统管理员')")
    public ResponseEntity<ApiResponse<PageResponse<RoleResponse>>> getRoleList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) String remark) {

        RoleQueryRequest request = new RoleQueryRequest();
        request.setPage(page);
        request.setSize(size);
        request.setRoleName(roleName);
        request.setRemark(remark);

        try {
            PageResponse<RoleResponse> result = roleService.getRoleList(request);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            System.err.println("Error in getRoleList: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<PageResponse<RoleResponse>>error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID获取角色信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('系统管理员')")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleById(@PathVariable Long id) {
        try {
            RoleResponse role = roleService.getRoleById(id);
            return ResponseEntity.ok(ApiResponse.success(role));
        } catch (Exception e) {
            System.err.println("Error in getRoleById: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<RoleResponse>error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 创建新角色
     */
    @PostMapping
    @PreAuthorize("hasRole('系统管理员')")
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@Valid @RequestBody RoleCreateRequest request) {
        try {
            RoleResponse role = roleService.createRole(request);
            return ResponseEntity.ok(ApiResponse.success("角色创建成功", role));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<RoleResponse>error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error in createRole: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<RoleResponse>error("创建失败: " + e.getMessage()));
        }
    }

    /**
     * 更新角色信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('系统管理员')")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequest request) {
        try {
            RoleResponse role = roleService.updateRole(id, request);
            return ResponseEntity.ok(ApiResponse.success("角色信息更新成功", role));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<RoleResponse>error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error in updateRole: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<RoleResponse>error("更新失败: " + e.getMessage()));
        }
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('系统管理员')")
    public ResponseEntity<ApiResponse<String>> deleteRole(@PathVariable Long id) {
        try {
            roleService.deleteRole(id);
            return ResponseEntity.ok(ApiResponse.success("角色删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<String>error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error in deleteRole: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<String>error("删除失败: " + e.getMessage()));
        }
    }
}
