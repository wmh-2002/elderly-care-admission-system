package com.example.elderlycaresystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 库存变动流水实体类
 * 对应数据库表 stock_record
 */
@Data
@TableName("stock_record")
public class StockRecord {

    /**
     * 流水主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 物资→goods
     */
    private Long goodsId;

    /**
     * 变动数量：正数入库/负数出库
     */
    private Integer quantity;

    /**
     * 变动时间
     */
    private LocalDateTime recordTime;

    /**
     * 操作人→sys_user
     */
    private Long operatorId;

    /**
     * 备注
     */
    private String remark;
}