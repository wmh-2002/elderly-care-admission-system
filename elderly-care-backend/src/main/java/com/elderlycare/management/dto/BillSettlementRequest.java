package com.elderlycare.management.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;

/**
 * 账单结算请求
 * 根据老人信息自动计算费用并生成账单
 */
public class BillSettlementRequest {

    @NotNull(message = "老人ID不能为空")
    private Long elderId;

    @NotNull(message = "账单月份不能为空")
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "账单月份格式必须为 yyyy-MM")
    private String billMonth;

    // 可选的额外费用项（除了护理费用外的其他费用）
    private List<BillDetailCreateRequest> additionalFees;

    // 可选的支付方式，不提供则默认为"现金"
    private String paymentMethod;

    public BillSettlementRequest() {
    }

    public BillSettlementRequest(Long elderId, String billMonth) {
        this.elderId = elderId;
        this.billMonth = billMonth;
    }

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

    public List<BillDetailCreateRequest> getAdditionalFees() {
        return additionalFees;
    }

    public void setAdditionalFees(List<BillDetailCreateRequest> additionalFees) {
        this.additionalFees = additionalFees;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
