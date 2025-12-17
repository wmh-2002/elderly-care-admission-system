package com.example.elderlycaresystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.elderlycaresystem.entity.Goods;
import org.apache.ibatis.annotations.Mapper;

/**
 * 物资数据访问接口
 */
@Mapper
public interface GoodsRepository extends BaseMapper<Goods> {
}