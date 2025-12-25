package com.elderlycare.management.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

public class BillDetailUpdateRequest {

    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;

    @DecimalMin(value = "0.0", message = "单价不能为负数")
    private BigDecimal unitPrice;

    @DecimalMin(value = "0.0", message = "金额不能为负数")
    private BigDecimal amount;

    // Getters and Setters
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

