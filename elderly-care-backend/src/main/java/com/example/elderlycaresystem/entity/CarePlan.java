package com.example.elderlycaresystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 护理计划实体类
 * 对应数据库表 care_plan
 */
@Data
@TableName("care_plan")
public class CarePlan {

    /**
     * 计划主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 老人→elder
     */
    private Long elderId;

    /**
     * 计划内容（JSON 或长文本）
     */
    private String planContent;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最近修改
     */
    private LocalDateTime updateTime;
}