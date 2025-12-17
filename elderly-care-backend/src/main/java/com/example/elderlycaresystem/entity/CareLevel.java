package com.example.elderlycaresystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 护理等级字典实体类
 * 对应数据库表 care_level
 */
@Data
@TableName("care_level")
public class CareLevel {

    /**
     * 等级代码，如 L1/L2/L3
     */
    @TableId(value = "level_code", type = IdType.INPUT)
    private String levelCode;

    /**
     * 等级名称
     */
    private String levelName;

    /**
     * 描述
     */
    private String description;

    /**
     * 日单价（元）
     */
    private BigDecimal dailyPrice;
}