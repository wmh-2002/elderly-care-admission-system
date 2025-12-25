package com.elderlycare.management.dto;

import com.elderlycare.management.entity.BillDetail;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BillDetailResponse {
    private Long id;
    private Long billId;
    private String itemCode;
    private String itemName;
    private String itemType;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BillDetailResponse() {
    }

    public BillDetailResponse(BillDetail billDetail) {
        this.id = billDetail.getId();
        this.billId = billDetail.getBillId();
        this.itemCode = billDetail.getItemCode();
        this.quantity = billDetail.getQuantity();
        this.unitPrice = billDetail.getUnitPrice();
        this.amount = billDetail.getAmount();
        this.createdAt = billDetail.getCreatedAt();
        this.updatedAt = billDetail.getUpdatedAt();

        if (billDetail.getFeeItem() != null) {
            this.itemName = billDetail.getFeeItem().getItemName();
            this.itemType = billDetail.getFeeItem().getItemType();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
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

