package com.elderlycare.management.dto;

import com.elderlycare.management.entity.CareLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CareLevelResponse {
    private String levelCode;
    private String levelName;
    private String description;
    private BigDecimal dailyPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CareLevelResponse() {
    }

    public CareLevelResponse(CareLevel careLevel) {
        this.levelCode = careLevel.getLevelCode();
        this.levelName = careLevel.getLevelName();
        this.description = careLevel.getDescription();
        this.dailyPrice = careLevel.getDailyPrice();
        this.createdAt = careLevel.getCreatedAt();
        this.updatedAt = careLevel.getUpdatedAt();
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
