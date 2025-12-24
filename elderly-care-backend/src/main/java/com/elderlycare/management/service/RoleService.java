package com.elderlycare.management.service;

import com.elderlycare.management.dto.*;
import com.elderlycare.management.entity.SysRole;
import com.elderlycare.management.exception.ResourceNotFoundException;
import com.elderlycare.management.repository.SysRoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {

    private final SysRoleRepository roleRepository;

    public RoleService(SysRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * 分页查询角色列表
     */
    public PageResponse<RoleResponse> getRoleList(RoleQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());

        Specification<SysRole> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getRoleName() != null && !request.getRoleName().trim().isEmpty()) {
                String searchKeyword = request.getRoleName().trim();
                predicates.add(criteriaBuilder.like(root.get("roleName"), "%" + searchKeyword + "%"));
            }

            if (request.getRemark() != null && !request.getRemark().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("remark"), "%" + request.getRemark().trim() + "%"));
            }

            if (predicates.isEmpty()) {
                return null;
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<SysRole> rolePage = roleRepository.findAll(spec, pageable);

        List<RoleResponse> roleResponses = rolePage.getContent().stream()
                .map(RoleResponse::new)
                .toList();

        return new PageResponse<>(roleResponses, request.getPage(), request.getSize(),
                rolePage.getTotalElements(), rolePage.getTotalPages(), rolePage.isLast());
    }

    /**
     * 根据ID获取角色信息
     */
    public RoleResponse getRoleById(Long id) {
        SysRole role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("角色不存在，ID: " + id));
        return new RoleResponse(role);
    }

    /**
     * 创建新角色
     */
    @Transactional
    public RoleResponse createRole(RoleCreateRequest request) {
        // 检查角色名称是否已存在
        if (roleRepository.existsByRoleName(request.getRoleName())) {
            throw new RuntimeException("角色名称已存在");
        }

        SysRole role = new SysRole();
        role.setRoleName(request.getRoleName().trim());
        role.setRemark(request.getRemark() != null ? request.getRemark().trim() : null);

        SysRole savedRole = roleRepository.save(role);
        return new RoleResponse(savedRole);
    }

    /**
     * 更新角色信息
     */
    @Transactional
    public RoleResponse updateRole(Long id, RoleUpdateRequest request) {
        SysRole role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("角色不存在，ID: " + id));

        // 检查角色名称是否已存在（排除自身）
        if (request.getRoleName() != null && !request.getRoleName().trim().equals(role.getRoleName())) {
            if (roleRepository.existsByRoleName(request.getRoleName())) {
                throw new RuntimeException("角色名称已存在");
            }
            role.setRoleName(request.getRoleName().trim());
        }

        // 更新备注
        if (request.getRemark() != null) {
            role.setRemark(request.getRemark().trim());
        }

        SysRole savedRole = roleRepository.save(role);
        return new RoleResponse(savedRole);
    }

    /**
     * 删除角色
     */
    @Transactional
    public void deleteRole(Long id) {
        SysRole role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("角色不存在，ID: " + id));

        // 检查是否为系统管理员角色，防止误删
        if (role.getRoleName().equals("系统管理员")) {
            throw new RuntimeException("不能删除系统管理员角色");
        }

        roleRepository.deleteById(id);
    }
}
