package com.elderlycare.management.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class BillDetailCreateRequest {

    @NotBlank(message = "费用项目代码不能为空")
    @Size(max = 20, message = "费用项目代码长度不能超过20个字符")
    private String itemCode;

    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity = 1;

    @DecimalMin(value = "0.0", message = "单价不能为负数")
    private BigDecimal unitPrice;

    @DecimalMin(value = "0.0", message = "金额不能为负数")
    private BigDecimal amount;

    // Getters and Setters
    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

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

