package com.elderlycare.management.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CarePlanResponse {
    private Long id;
    private Long elderId;
    private String elderName; // 新增：关联老人的姓名
    private String careLevelId;
    private String careLevelName; // 新增：关联护理等级的名称
    private String status; // 状态
    private LocalDateTime startDate; // 开始日期
    private LocalDateTime endDate; // 结束日期
    private Long assignedNurseId; // 分配的护理员ID
    private String assignedNurseName; // 分配的护理员姓名
    private String planContent;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}