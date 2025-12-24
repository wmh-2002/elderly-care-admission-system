package com.elderlycare.management.security;

import com.elderlycare.management.entity.SysUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private String realName;
    private Long roleId;
    private String roleName;
    private Boolean status;
    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, String username, String password, String realName, 
                        Long roleId, String roleName, Boolean status, 
                        Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.roleId = roleId;
        this.roleName = roleName;
        this.status = status;
        this.authorities = authorities;
    }

    public static UserPrincipal create(SysUser user) {
        String roleName = "USER"; // 默认角色

        // 首先尝试从关联的role对象获取角色名
        if (user.getRole() != null) {
            roleName = user.getRole().getRoleName();
        }
        // 如果role对象为null但有roleId，尝试根据roleId推断角色名
        else if (user.getRoleId() != null) {
            // 这里可以根据roleId映射到角色名，或者从数据库查询
            // 暂时使用默认映射
            switch (user.getRoleId().intValue()) {
                case 1: roleName = "系统管理员"; break;
                case 2: roleName = "院长"; break;
                case 3: roleName = "护理主管"; break;
                case 4: roleName = "护理员"; break;
                case 5: roleName = "财务人员"; break;
                case 6: roleName = "前台接待"; break;
                default: roleName = "USER"; break;
            }
        }

        // 将角色名称转换为Spring Security可以识别的格式，例如："系统管理员" -> "ROLE_系统管理员"
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleName);

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRealName(),
                user.getRoleId(),
                roleName,
                user.getStatus(),
                Collections.singletonList(authority)
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status;
    }
}

