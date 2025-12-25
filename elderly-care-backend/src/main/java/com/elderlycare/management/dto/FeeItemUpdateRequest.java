package com.elderlycare.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class FeeItemUpdateRequest {

    @NotBlank(message = "费用项目名称不能为空")
    @Size(max = 50, message = "费用项目名称长度不能超过50")
    private String itemName;

    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.00", inclusive = true, message = "单价不能小于0")
    private BigDecimal unitPrice;

    @NotBlank(message = "费用项目类型不能为空")
    @Size(max = 20, message = "费用项目类型长度不能超过20")
    private String itemType;

    // Getters and Setters
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
}