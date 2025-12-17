package com.example.elderlycaresystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统操作日志实体类
 * 对应数据库表 sys_log
 */
@Data
@TableName("sys_log")
public class SysLog {

    /**
     * 日志主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 操作人→sys_user
     */
    private Long userId;

    /**
     * 操作者 IP
     */
    private String operIp;

    /**
     * 操作类型：登录/新增/修改/删除...
     */
    private String operType;

    /**
     * 操作描述
     */
    private String operDesc;

    /**
     * 操作时间
     */
    private LocalDateTime operTime;
}