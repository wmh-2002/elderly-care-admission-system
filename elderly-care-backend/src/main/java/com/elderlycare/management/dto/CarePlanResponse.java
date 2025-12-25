package com.elderlycare.management.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CarePlanResponse {
    private Long id;
    private Long elderId;
    private String elderName; // 新增：关联老人的姓名
    private String planContent;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}