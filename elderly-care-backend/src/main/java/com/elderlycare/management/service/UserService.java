package com.elderlycare.management.service;

import com.elderlycare.management.dto.*;
import com.elderlycare.management.entity.SysRole;
import com.elderlycare.management.entity.SysUser;
import com.elderlycare.management.exception.ResourceNotFoundException;
import com.elderlycare.management.repository.SysRoleRepository;
import com.elderlycare.management.repository.SysUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final SysUserRepository userRepository;
    private final SysRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(SysUserRepository userRepository, SysRoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 分页查询用户列表
     */
    public PageResponse<UserResponse> getUserList(UserQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());

        Specification<SysUser> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
                String searchKeyword = request.getUsername().trim();
                predicates.add(criteriaBuilder.like(root.get("username"), "%" + searchKeyword + "%"));
            }

        if (request.getRealName() != null && !request.getRealName().trim().isEmpty()) {
            predicates.add(criteriaBuilder.like(root.get("realName"), "%" + request.getRealName().trim() + "%"));
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            predicates.add(criteriaBuilder.like(root.get("email"), "%" + request.getEmail().trim() + "%"));
        }

        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            predicates.add(criteriaBuilder.like(root.get("phone"), "%" + request.getPhone().trim() + "%"));
        }

        if (request.getRoleId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("roleId"), request.getRoleId()));
            }

            if (request.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }

            if (predicates.isEmpty()) {
                return null;
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<SysUser> userPage = userRepository.findAll(spec, pageable);

        List<UserResponse> userResponses = userPage.getContent().stream()
                .map(this::convertToUserResponse)
                .toList();

        return new PageResponse<>(userResponses, request.getPage(), request.getSize(),
                userPage.getTotalElements(), userPage.getTotalPages(), userPage.isLast());
    }

    /**
     * 根据ID获取用户信息
     */
    public UserResponse getUserById(Long id) {
        SysUser user = userRepository.findByIdWithRole(id)
                .orElse(userRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("用户不存在，ID: " + id)));
        return convertToUserResponse(user);
    }

    /**
     * 创建新用户
     */
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查角色是否存在
        SysRole role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("角色不存在，ID: " + request.getRoleId()));

        SysUser user = new SysUser();
        user.setUsername(request.getUsername().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName() != null ? request.getRealName().trim() : request.getUsername().trim());
        user.setEmail(null);
        user.setPhone(null);
        user.setRoleId(request.getRoleId());
        user.setStatus(request.getStatus() != null ? request.getStatus() : true);

        SysUser savedUser = userRepository.save(user);
        return convertToUserResponse(savedUser);
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        SysUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在，ID: " + id));

        // 检查角色是否存在（如果提供了roleId）
        if (request.getRoleId() != null) {
            SysRole role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("角色不存在，ID: " + request.getRoleId()));
            user.setRoleId(request.getRoleId());
        }

        // 更新字段
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword().trim()));
        }

        if (request.getRealName() != null && !request.getRealName().trim().isEmpty()) {
            user.setRealName(request.getRealName().trim());
        }

        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        // 更新邮箱和电话（如果提供了）
        user.setEmail(request.getEmail() != null ? request.getEmail().trim() : user.getEmail());
        user.setPhone(request.getPhone() != null ? request.getPhone().trim() : user.getPhone());

        SysUser savedUser = userRepository.save(user);
        return convertToUserResponse(savedUser);
    }

    /**
     * 删除用户
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("用户不存在，ID: " + id);
        }

        // 检查是否为管理员用户，防止误删管理员
        SysUser user = userRepository.findById(id).get();
        if (user.getUsername().equals("admin")) {
            throw new RuntimeException("不能删除系统管理员账户");
        }

        userRepository.deleteById(id);
    }

    /**
     * 启用/禁用用户
     */
    @Transactional
    public UserResponse updateUserStatus(Long id, Boolean status) {
        SysUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在，ID: " + id));

        // 防止禁用管理员账户
        if (!status && user.getUsername().equals("admin")) {
            throw new RuntimeException("不能禁用系统管理员账户");
        }

        user.setStatus(status);
        SysUser savedUser = userRepository.save(user);
        return convertToUserResponse(savedUser);
    }

    private UserResponse convertToUserResponse(SysUser user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRoleId(user.getRoleId());
        response.setStatus(user.getStatus());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        
        // 如果角色已加载，设置角色名称
        if (user.getRole() != null) {
            response.setRoleName(user.getRole().getRoleName());
        } else {
            // 如果没有加载，尝试从数据库加载
            SysRole role = roleRepository.findById(user.getRoleId()).orElse(null);
            if (role != null) {
                response.setRoleName(role.getRoleName());
            }
        }
        
        return response;
    }
}

