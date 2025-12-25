package com.elderlycare.management.dto;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.List;

public class BillUpdateRequest {

    @DecimalMin(value = "0.0", message = "应付金额不能为负数")
    private BigDecimal totalAmount;

    @DecimalMin(value = "0.0", message = "已付金额不能为负数")
    private BigDecimal paidAmount;

    private Integer status; // 0 未缴清  1 已缴清

    private List<BillDetailCreateRequest> details; // 账单明细

    // Getters and Setters
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<BillDetailCreateRequest> getDetails() {
        return details;
    }

    public void setDetails(List<BillDetailCreateRequest> details) {
        this.details = details;
    }
}

