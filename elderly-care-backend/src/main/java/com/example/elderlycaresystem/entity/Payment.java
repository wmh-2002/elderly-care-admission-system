package com.example.elderlycaresystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 缴费记录实体类
 * 对应数据库表 payment
 */
@Data
@TableName("payment")
public class Payment {

    /**
     * 缴费主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 账单→bill
     */
    private Long billId;

    /**
     * 本次缴费金额
     */
    private BigDecimal payAmount;

    /**
     * 缴费方式→字典
     */
    private String payMethod;

    /**
     * 缴费时间
     */
    private LocalDateTime payTime;

    /**
     * 备注
     */
    private String remark;
}