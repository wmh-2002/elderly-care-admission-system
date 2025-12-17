package com.example.elderlycaresystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 老人档案实体类
 * 对应数据库表 elder
 */
@Data
@TableName("elder")
public class Elder {

    /**
     * 老人主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 老人编号，可自动生成
     */
    private String elderNo;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别：M 男  F 女
     */
    private String gender;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 民族
     */
    private String nation;

    /**
     * 籍贯
     */
    private String nativePlace;

    /**
     * 本人电话
     */
    private String phone;

    /**
     * 家庭地址
     */
    private String address;

    /**
     * 血型
     */
    private String bloodType;

    /**
     * 过敏史
     */
    private String allergy;

    /**
     * 既往病史
     */
    private String medicalHistory;

    /**
     * 目前健康状况
     */
    private String healthStatus;

    /**
     * 紧急联系人姓名
     */
    private String contactName;

    /**
     * 紧急联系人电话
     */
    private String contactPhone;

    /**
     * 与老人关系
     */
    private String contactRelation;

    /**
     * 照片 URL
     */
    private String photo;

    /**
     * 入住日期
     */
    private LocalDate checkinDate;

    /**
     * 外键→bed，唯一约束保证一人一床
     */
    private Long bedId;

    /**
     * 护理等级→care_level.level_code
     */
    private String careLevel;

    /**
     * 月收费标准（元）
     */
    private BigDecimal feeStandard;

    /**
     * 缴费方式→字典
     */
    private String payType;

    /**
     * 状态：1 在院  0 退住
     */
    private Integer status;

    /**
     * 建档时间
     */
    private LocalDateTime createTime;
}