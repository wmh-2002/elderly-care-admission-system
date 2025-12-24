package com.elderlycare.management.service;

import com.elderlycare.management.dto.LoginRequest;
import com.elderlycare.management.dto.LoginResponse;
import com.elderlycare.management.entity.SysUser;
import com.elderlycare.management.repository.SysUserRepository;
import com.elderlycare.management.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final SysUserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public AuthService(AuthenticationManager authenticationManager, SysUserRepository userRepository,
                      PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        // 使用Spring Security进行认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 生成真实的JWT token
        String jwt = jwtUtils.generateJwtToken(authentication);

        // 从数据库获取用户信息（包含角色信息）
        SysUser user = userRepository.findByUsernameWithRole(loginRequest.getUsername())
                .orElse(userRepository.findByUsername(loginRequest.getUsername())
                        .orElseThrow(() -> new RuntimeException("用户不存在")));

        LoginResponse.LoginData loginData = new LoginResponse.LoginData(
                jwt,
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                user.getRoleId(),
                user.getRole() != null ? user.getRole().getRoleName() : "",
                user.getEmail(),
                user.getPhone()
        );

        return new LoginResponse(200, "登录成功", loginData);
    }

    public void registerUser(LoginRequest registerRequest) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建新用户（这里需要指定roleId，暂时使用默认值1，实际应该通过参数传入）
        SysUser user = new SysUser();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(encoder.encode(registerRequest.getPassword()));
        user.setRealName(registerRequest.getUsername()); // 默认真实姓名为用户名，可后续修改
        user.setRoleId(1L); // 默认角色，应该根据业务需求调整
        user.setStatus(true);

        userRepository.save(user);
    }
}

