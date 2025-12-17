package com.example.elderlycaresystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.elderlycaresystem.entity.Elder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 老人档案数据访问接口
 */
@Mapper
public interface ElderRepository extends BaseMapper<Elder> {
}