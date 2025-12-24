package com.elderlycare.management.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CareLevelCreateRequest {

    @NotBlank(message = "等级编码不能为空")
    @Size(max = 20, message = "等级编码长度不能超过20个字符")
    private String levelCode;

    @NotBlank(message = "等级名称不能为空")
    @Size(max = 50, message = "等级名称长度不能超过50个字符")
    private String levelName;

    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;

    @NotNull(message = "日价格不能为空")
    @DecimalMin(value = "0.00", message = "日价格不能小于0")
    private BigDecimal dailyPrice;

    public CareLevelCreateRequest() {
    }

    public CareLevelCreateRequest(String levelCode, String levelName, String description, BigDecimal dailyPrice) {
        this.levelCode = levelCode;
        this.levelName = levelName;
        this.description = description;
        this.dailyPrice = dailyPrice;
    }

    // Getters and Setters
    public String getLevelCode() {
        return levelCode;
    }

    public void setLevelCode(String levelCode) {
        this.levelCode = levelCode;
    }

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
