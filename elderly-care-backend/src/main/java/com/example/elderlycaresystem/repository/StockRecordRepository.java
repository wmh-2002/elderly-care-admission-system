package com.example.elderlycaresystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.elderlycaresystem.entity.StockRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存变动流水数据访问接口
 */
@Mapper
public interface StockRecordRepository extends BaseMapper<StockRecord> {
}