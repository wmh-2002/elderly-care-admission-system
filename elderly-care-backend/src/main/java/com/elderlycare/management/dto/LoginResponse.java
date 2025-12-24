package com.elderlycare.management.dto;

public class LoginResponse {

    private Integer code;
    private String message;
    private LoginData data;

    public LoginResponse() {
    }

    public LoginResponse(Integer code, String message, LoginData data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LoginData getData() {
        return data;
    }

    public void setData(LoginData data) {
        this.data = data;
    }

    public static class LoginData {
        private String token;
        private Long id;
        private String username;
        private String realName;
        private Long roleId;
        private String roleName;
        private String email;
        private String phone;

        public LoginData() {
        }

        public LoginData(String token, Long id, String username, String realName, Long roleId, String roleName, String email, String phone) {
            this.token = token;
            this.id = id;
            this.username = username;
            this.realName = realName;
            this.roleId = roleId;
            this.roleName = roleName;
            this.email = email;
            this.phone = phone;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public Long getRoleId() {
            return roleId;
        }

        public void setRoleId(Long roleId) {
            this.roleId = roleId;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }
    }
}

