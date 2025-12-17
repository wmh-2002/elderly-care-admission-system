package com.example.elderlycaresystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统统一字典表实体类
 * 对应数据库表 sys_dict
 */
@Data
@TableName("sys_dict")
public class SysDict {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 字典类型，例如：gender、pay_method
     */
    private String dictType;

    /**
     * 存储值，如 M/F
     */
    private String dictKey;

    /**
     * 显示值，如 男/女
     */
    private String dictValue;

    /**
     * 排序号，升序排列
     */
    private Integer sortNo;
}