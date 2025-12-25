package com.elderlycare.management.dto;

import com.elderlycare.management.entity.Bill;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BillResponse {
    private Long id;
    private Long elderId;
    private String elderName;
    private String elderNo;
    private String billMonth;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private Integer status;
    private String statusText; // 状态文本：未缴清/已缴清
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<BillDetailResponse> details; // 账单明细

    public BillResponse() {
    }

    public BillResponse(Bill bill) {
        this.id = bill.getId();
        this.elderId = bill.getElderId();
        this.billMonth = bill.getBillMonth();
        this.totalAmount = bill.getTotalAmount();
        this.paidAmount = bill.getPaidAmount();
        this.status = bill.getStatus();
        this.statusText = bill.getStatus() != null && bill.getStatus() == 1 ? "已缴清" : "未缴清";
        this.createdAt = bill.getCreatedAt();
        this.updatedAt = bill.getUpdatedAt();

        if (bill.getElder() != null) {
            this.elderName = bill.getElder().getName();
            this.elderNo = bill.getElder().getElderNo();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getElderId() {
        return elderId;
    }

    public void setElderId(Long elderId) {
        this.elderId = elderId;
    }

    public String getElderName() {
        return elderName;
    }

    public void setElderName(String elderName) {
        this.elderName = elderName;
    }

    public String getElderNo() {
        return elderNo;
    }

    public void setElderNo(String elderNo) {
        this.elderNo = elderNo;
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

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
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

    public List<BillDetailResponse> getDetails() {
        return details;
    }

    public void setDetails(List<BillDetailResponse> details) {
        this.details = details;
    }
}

