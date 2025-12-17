package com.example.elderlycaresystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.elderlycaresystem.entity.CarePlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 护理计划数据访问接口
 */
@Mapper
public interface CarePlanRepository extends BaseMapper<CarePlan> {
}