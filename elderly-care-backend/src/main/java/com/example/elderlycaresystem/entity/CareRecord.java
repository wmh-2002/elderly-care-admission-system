package com.example.elderlycaresystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 日常护理记录实体类
 * 对应数据库表 care_record
 */
@Data
@TableName("care_record")
public class CareRecord {

    /**
     * 记录主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 老人→elder
     */
    private Long elderId;

    /**
     * 记录日期
     */
    private LocalDate recordDate;

    /**
     * 体温（℃）
     */
    private BigDecimal temperature;

    /**
     * 脉搏（次/分）
     */
    private Integer pulse;

    /**
     * 呼吸（次/分）
     */
    private Integer breath;

    /**
     * 血压，如 120/80
     */
    private String bloodPressure;

    /**
     * 饮食情况
     */
    private String diet;

    /**
     * 排泄情况
     */
    private String excrete;

    /**
     * 睡眠情况
     */
    private String sleep;

    /**
     * 用药记录
     */
    private String medicine;

    /**
     * 特殊情况
     */
    private String special;

    /**
     * 护理员→sys_user
     */
    private Long nurseId;

    /**
     * 填写时间
     */
    private LocalDateTime createTime;
}