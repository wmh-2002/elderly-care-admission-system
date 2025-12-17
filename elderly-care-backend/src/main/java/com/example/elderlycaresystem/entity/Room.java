package com.example.elderlycaresystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 房间档案实体类
 * 对应数据库表 room
 */
@Data
@TableName("room")
public class Room {

    /**
     * 房间主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 房间编号，如 101、202
     */
    private String roomNo;

    /**
     * 单人间/双人间/多人间
     */
    private String roomType;

    /**
     * 楼层
     */
    private Integer floor;

    /**
     * 最大可放床数
     */
    private Integer maxBed;
}