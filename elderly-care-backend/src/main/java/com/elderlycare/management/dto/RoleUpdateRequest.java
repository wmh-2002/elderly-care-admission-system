package com.elderlycare.management.dto;

import jakarta.validation.constraints.Size;

public class RoleUpdateRequest {

    @Size(max = 50, message = "角色名称长度不能超过50个字符")
    private String roleName;

    @Size(max = 200, message = "备注长度不能超过200个字符")
    private String remark;

    public RoleUpdateRequest() {
    }

    public RoleUpdateRequest(String roleName, String remark) {
        this.roleName = roleName;
        this.remark = remark;
    }

    // Getters and Setters
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
}
