package com.elderlycare.management.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 账单计算结果
 */
public class BillCalculationResult {

    private BigDecimal totalAmount = BigDecimal.ZERO;
    private List<BillDetailItem> details = new ArrayList<>();

    public void addDetail(String itemCode, String itemName, int quantity, BigDecimal unitPrice, BigDecimal amount) {
        details.add(new BillDetailItem(itemCode, itemName, quantity, unitPrice, amount));
        totalAmount = totalAmount.add(amount);
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public List<BillDetailItem> getDetails() {
        return details;
    }

    /**
     * 账单明细项
     */
    public static class BillDetailItem {
        private String itemCode;
        private String itemName;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal amount;

        public BillDetailItem(String itemCode, String itemName, int quantity, BigDecimal unitPrice, BigDecimal amount) {
            this.itemCode = itemCode;
            this.itemName = itemName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.amount = amount;
        }

        // Getters
        public String getItemCode() { return itemCode; }
        public String getItemName() { return itemName; }
        public int getQuantity() { return quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public BigDecimal getAmount() { return amount; }
    }
}
