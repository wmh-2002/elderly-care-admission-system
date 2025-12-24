package com.elderlycare.management.repository;

import com.elderlycare.management.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long>, JpaSpecificationExecutor<SysUser> {

    Optional<SysUser> findByUsername(String username);

    boolean existsByUsername(String username);

    List<SysUser> findByRoleId(Long roleId);

    @Query("SELECT u FROM SysUser u LEFT JOIN FETCH u.role WHERE u.id = :id")
    Optional<SysUser> findByIdWithRole(@Param("id") Long id);
    
    @Query("SELECT u FROM SysUser u LEFT JOIN FETCH u.role WHERE u.username = :username")
    Optional<SysUser> findByUsernameWithRole(@Param("username") String username);
}

