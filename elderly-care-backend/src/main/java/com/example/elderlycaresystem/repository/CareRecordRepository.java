package com.example.elderlycaresystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.elderlycaresystem.entity.CareRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 护理记录数据访问接口
 */
@Mapper
public interface CareRecordRepository extends BaseMapper<CareRecord> {
}