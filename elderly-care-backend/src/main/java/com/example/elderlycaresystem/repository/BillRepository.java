package com.example.elderlycaresystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.elderlycaresystem.entity.Bill;
import org.apache.ibatis.annotations.Mapper;

/**
 * 月度账单数据访问接口
 */
@Mapper
public interface BillRepository extends BaseMapper<Bill> {
}