package com.example.elderlycaresystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.elderlycaresystem.entity.CareLevel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 护理等级数据访问接口
 */
@Mapper
public interface CareLevelRepository extends BaseMapper<CareLevel> {
}