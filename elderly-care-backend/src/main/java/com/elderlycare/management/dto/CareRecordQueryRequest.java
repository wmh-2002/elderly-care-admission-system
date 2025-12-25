package com.elderlycare.management.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CareRecordQueryRequest {
    private Integer page = 1;
    private Integer size = 10;
    private Long elderId;
    private Long nurseId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate recordDate;
}