package com.example.elderlycaresystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 月度账单主表实体类
 * 对应数据库表 bill
 */
@Data
@TableName("bill")
public class Bill {

    /**
     * 账单主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 老人→elder
     */
    private Long elderId;

    /**
     * 账单月份 yyyy-MM
     */
    private String billMonth;

    /**
     * 应付金额
     */
    private BigDecimal totalAmount;

    /**
     * 已付金额
     */
    private BigDecimal paidAmount;

    /**
     * 0 未缴清  1 已缴清
     */
    private Integer status;

    /**
     * 生成时间
     */
    private LocalDateTime createTime;
}