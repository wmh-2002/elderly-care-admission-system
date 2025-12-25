package com.elderlycare.management.dto;

import lombok.Data;

@Data
public class CarePlanQueryRequest {
    private Integer page = 1;
    private Integer size = 10;
    private Long elderId;
    private String careLevelId;
    private String planContent;
}