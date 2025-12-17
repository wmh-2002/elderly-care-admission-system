package com.example.elderlycaresystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.elderlycaresystem.entity.SysLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统日志数据访问接口
 */
@Mapper
public interface SysLogRepository extends BaseMapper<SysLog> {
}