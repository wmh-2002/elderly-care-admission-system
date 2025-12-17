package com.example.elderlycaresystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.elderlycaresystem.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色数据访问接口
 */
@Mapper
public interface SysRoleRepository extends BaseMapper<SysRole> {
}