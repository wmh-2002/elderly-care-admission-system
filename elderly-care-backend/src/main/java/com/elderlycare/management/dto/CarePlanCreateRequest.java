package com.elderlycare.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CarePlanCreateRequest {

    @NotNull(message = "老人ID不能为空")
    private Long elderId;

    private String careLevelId; // 可选字段

    private String status; // 状态

    private LocalDateTime startDate; // 开始日期

    private LocalDateTime endDate; // 结束日期

    private Long assignedNurseId; // 分配的护理员ID

    @NotBlank(message = "计划内容不能为空")
    private String planContent;
}