package com.elderlycare.management.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public class BillCreateRequest {

    @NotNull(message = "老人ID不能为空")
    private Long elderId;

    @NotBlank(message = "账单月份不能为空")
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "账单月份格式必须为 yyyy-MM")
    private String billMonth;

    @NotNull(message = "应付金额不能为空")
    @DecimalMin(value = "0.0", message = "应付金额不能为负数")
    private BigDecimal totalAmount;

    @DecimalMin(value = "0.0", message = "已付金额不能为负数")
    private BigDecimal paidAmount;

    private Integer status = 0; // 默认未缴清

    private List<BillDetailCreateRequest> details; // 账单明细

    // Getters and Setters
    public Long getElderId() {
        return elderId;
    }

    public void setElderId(Long elderId) {
        this.elderId = elderId;
    }

    public String getBillMonth() {
        return billMonth;
    }

    public void setBillMonth(String billMonth) {
        this.billMonth = billMonth;
    }

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

