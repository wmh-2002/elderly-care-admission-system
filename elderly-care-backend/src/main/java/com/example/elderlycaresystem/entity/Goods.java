package com.example.elderlycaresystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 物资字典实体类
 * 对应数据库表 goods
 */
@Data
@TableName("goods")
public class Goods {

    /**
     * 物资主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 物资名称
     */
    private String goodsName;

    /**
     * 分类
     */
    private String category;

    /**
     * 单位：包、箱、个...
     */
    private String unit;

    /**
     * 当前库存
     */
    private Integer stock;

    /**
     * 库存预警下限
     */
    private Integer minStock;
}