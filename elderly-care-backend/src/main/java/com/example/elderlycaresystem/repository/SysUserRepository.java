package com.example.elderlycaresystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.elderlycaresystem.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户数据访问接口
 */
@Mapper
public interface SysUserRepository extends BaseMapper<SysUser> {
}