package com.elderlycare.management.dto;

import com.elderlycare.management.entity.SysRole;

import java.time.LocalDateTime;

public class RoleResponse {
    private Long id;
    private String roleName;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RoleResponse() {
    }

    public RoleResponse(SysRole role) {
        this.id = role.getId();
        this.roleName = role.getRoleName();
        this.remark = role.getRemark();
        this.createdAt = role.getCreatedAt();
        this.updatedAt = role.getUpdatedAt();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
