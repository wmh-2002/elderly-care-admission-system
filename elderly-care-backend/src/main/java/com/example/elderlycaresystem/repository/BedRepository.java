package com.example.elderlycaresystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.elderlycaresystem.entity.Bed;
import org.apache.ibatis.annotations.Mapper;

/**
 * 床位档案数据访问接口
 */
@Mapper
public interface BedRepository extends BaseMapper<Bed> {
}