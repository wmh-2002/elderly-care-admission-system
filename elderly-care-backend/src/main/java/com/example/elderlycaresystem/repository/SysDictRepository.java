package com.example.elderlycaresystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.elderlycaresystem.entity.SysDict;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统字典数据访问接口
 */
@Mapper
public interface SysDictRepository extends BaseMapper<SysDict> {
}