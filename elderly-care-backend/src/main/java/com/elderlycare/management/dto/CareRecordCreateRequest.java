package com.elderlycare.management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CareRecordCreateRequest {
    
    @NotNull(message = "老人ID不能为空")
    private Long elderId;
    
    @NotNull(message = "记录日期不能为空")
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
    
    @NotNull(message = "护理员ID不能为空")
    private Long nurseId;
}