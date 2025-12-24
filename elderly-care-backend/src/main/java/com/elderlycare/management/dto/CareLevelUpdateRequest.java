package com.elderlycare.management.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CareLevelUpdateRequest {

    @Size(max = 50, message = "等级名称长度不能超过50个字符")
    private String levelName;

    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;

    @DecimalMin(value = "0.00", message = "日价格不能小于0")
    private BigDecimal dailyPrice;

    public CareLevelUpdateRequest() {
    }

    // Getters and Setters
    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(BigDecimal dailyPrice) {
        this.dailyPrice = dailyPrice;
    }
}
