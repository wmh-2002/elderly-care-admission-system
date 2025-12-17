package com.example.elderlycaresystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 床位档案实体类
 * 对应数据库表 bed
 */
@Data
@TableName("bed")
public class Bed {

    /**
     * 床位主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 外键→room
     */
    private Long roomId;

    /**
     * 床位编号，如 101-1
     */
    private String bedNo;

    /**
     * 状态：0 空闲  1 已入住  2 维修
     */
    private Integer status;
}