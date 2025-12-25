package com.elderlycare.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CarePlanUpdateRequest {
    
    @NotNull(message = "老人ID不能为空")
    private Long elderId;
    
    @NotBlank(message = "计划内容不能为空")
    private String planContent;
}