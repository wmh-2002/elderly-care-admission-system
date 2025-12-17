package com.example.elderlycaresystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.elderlycaresystem.entity.Visitor;
import org.apache.ibatis.annotations.Mapper;

/**
 * 访客记录数据访问接口
 */
@Mapper
public interface VisitorRepository extends BaseMapper<Visitor> {
}