package com.example.elderlycaresystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.elderlycaresystem.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 缴费记录数据访问接口
 */
@Mapper
public interface PaymentRepository extends BaseMapper<Payment> {
}