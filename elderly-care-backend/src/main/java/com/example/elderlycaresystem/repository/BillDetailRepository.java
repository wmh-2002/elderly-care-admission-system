package com.example.elderlycaresystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.elderlycaresystem.entity.BillDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账单明细数据访问接口
 */
@Mapper
public interface BillDetailRepository extends BaseMapper<BillDetail> {
}