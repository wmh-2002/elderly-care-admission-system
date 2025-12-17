package com.example.elderlycaresystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.elderlycaresystem.entity.FeeItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 费用项目数据访问接口
 */
@Mapper
public interface FeeItemRepository extends BaseMapper<FeeItem> {
}