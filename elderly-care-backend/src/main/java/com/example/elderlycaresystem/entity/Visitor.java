package com.example.elderlycaresystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 访客记录实体类
 * 对应数据库表 visitor
 */
@Data
@TableName("visitor")
public class Visitor {

    /**
     * 访客主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 被访老人→elder
     */
    private Long elderId;

    /**
     * 访客姓名
     */
    private String visitorName;

    /**
     * 访客电话
     */
    private String phone;

    /**
     * 与老人关系
     */
    private String relation;

    /**
     * 来访时间
     */
    private LocalDateTime visitDate;

    /**
     * 备注
     */
    private String remark;
}