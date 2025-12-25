package com.elderlycare.management.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CareRecordResponse {
    private Long id;
    private Long elderId;
    private String elderName; // 关联老人的姓名
    private LocalDate recordDate;
    private BigDecimal temperature;
    private Integer pulse;
    private Integer breath;
    private String bloodPressure;
    private String diet;
    private String excrete;
    private String sleep;
    private String medicine;
    private String special;
    private Long nurseId;
    private String nurseName; // 关联护理员的姓名
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}