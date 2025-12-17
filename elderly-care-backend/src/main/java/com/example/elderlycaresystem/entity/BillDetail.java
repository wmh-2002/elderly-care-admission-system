package com.example.elderlycaresystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 账单明细实体类
 * 对应数据库表 bill_detail
 */
@Data
@TableName("bill_detail")
public class BillDetail {

    /**
     * 明细主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 账单→bill
     */
    private Long billId;

    /**
     * 费用项目→fee_item
     */
    private String itemCode;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 单价（冗余）
     */
    private BigDecimal unitPrice;

    /**
     * 小计金额
     */
    private BigDecimal amount;
}