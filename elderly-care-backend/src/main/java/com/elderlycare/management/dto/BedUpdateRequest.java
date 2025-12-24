package com.elderlycare.management.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class BedUpdateRequest {

    @Min(value = 0, message = "状态值不能小于0")
    @Max(value = 2, message = "状态值不能大于2")
    private Integer status;

    public BedUpdateRequest() {
    }

    public BedUpdateRequest(Integer status) {
        this.status = status;
    }

    // Getters and Setters
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
