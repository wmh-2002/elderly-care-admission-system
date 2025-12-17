package com.example.elderlycaresystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 费用项目字典实体类
 * 对应数据库表 fee_item
 */
@Data
@TableName("fee_item")
public class FeeItem {

    /**
     * 项目代码
     */
    @TableId(value = "item_code", type = IdType.INPUT)
    private String itemCode;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 单价（元）
     */
    private BigDecimal unitPrice;

    /**
     * 类型：住宿/护理/餐饮/医疗/其他
     */
    private String itemType;
}