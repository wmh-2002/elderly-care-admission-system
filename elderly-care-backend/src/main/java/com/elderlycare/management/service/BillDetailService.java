package com.elderlycare.management.service;

import com.elderlycare.management.dto.BillDetailCreateRequest;
import com.elderlycare.management.dto.BillDetailResponse;
import com.elderlycare.management.dto.BillDetailUpdateRequest;
import com.elderlycare.management.entity.Bill;
import com.elderlycare.management.entity.BillDetail;
import com.elderlycare.management.entity.FeeItem;
import com.elderlycare.management.exception.ResourceNotFoundException;
import com.elderlycare.management.repository.BillDetailRepository;
import com.elderlycare.management.repository.BillRepository;
import com.elderlycare.management.repository.FeeItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillDetailService {

    private final BillDetailRepository billDetailRepository;
    private final BillRepository billRepository;
    private final FeeItemRepository feeItemRepository;

    public BillDetailService(BillDetailRepository billDetailRepository, BillRepository billRepository,
                              FeeItemRepository feeItemRepository) {
        this.billDetailRepository = billDetailRepository;
        this.billRepository = billRepository;
        this.feeItemRepository = feeItemRepository;
    }

    /**
     * 根据账单ID获取账单明细列表
     */
    public List<BillDetailResponse> getBillDetailsByBillId(Long billId) {
        // 验证账单是否存在
        billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("账单不存在，ID: " + billId));

        List<BillDetail> details = billDetailRepository.findByBillId(billId);
        return details.stream()
                .map(BillDetailResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取账单明细
     */
    public BillDetailResponse getBillDetailById(Long id) {
        BillDetail detail = billDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("账单明细不存在，ID: " + id));
        return new BillDetailResponse(detail);
    }

    /**
     * 创建账单明细
     */
    @Transactional
    public BillDetailResponse createBillDetail(Long billId, BillDetailCreateRequest request) {
        // 验证账单是否存在
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("账单不存在，ID: " + billId));

        // 验证费用项目是否存在
        FeeItem feeItem = feeItemRepository.findById(request.getItemCode())
                .orElseThrow(() -> new ResourceNotFoundException("费用项目不存在: " + request.getItemCode()));

        BillDetail detail = new BillDetail();
        detail.setBillId(billId);
        detail.setItemCode(request.getItemCode());
        detail.setQuantity(request.getQuantity() != null ? request.getQuantity() : 1);
        detail.setUnitPrice(request.getUnitPrice() != null ? request.getUnitPrice() : feeItem.getUnitPrice());
        if (request.getAmount() != null) {
            detail.setAmount(request.getAmount());
        } else {
            detail.setAmount(detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())));
        }

        BillDetail saved = billDetailRepository.save(detail);

        // 更新账单总金额
        updateBillTotalAmount(billId);

        return new BillDetailResponse(saved);
    }

    /**
     * 更新账单明细
     */
    @Transactional
    public BillDetailResponse updateBillDetail(Long id, BillDetailUpdateRequest request) {
        BillDetail detail = billDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("账单明细不存在，ID: " + id));

        if (request.getQuantity() != null) {
            detail.setQuantity(request.getQuantity());
        }
        if (request.getUnitPrice() != null) {
            detail.setUnitPrice(request.getUnitPrice());
        }
        if (request.getAmount() != null) {
            detail.setAmount(request.getAmount());
        } else {
            // 如果金额未提供，重新计算
            if (detail.getUnitPrice() != null && detail.getQuantity() != null) {
                detail.setAmount(detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())));
            }
        }

        BillDetail saved = billDetailRepository.save(detail);

        // 更新账单总金额
        updateBillTotalAmount(detail.getBillId());

        return new BillDetailResponse(saved);
    }

    /**
     * 删除账单明细
     */
    @Transactional
    public void deleteBillDetail(Long id) {
        BillDetail detail = billDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("账单明细不存在，ID: " + id));

        Long billId = detail.getBillId();
        billDetailRepository.deleteById(id);

        // 更新账单总金额
        updateBillTotalAmount(billId);
    }

    /**
     * 更新账单总金额（根据明细计算）
     */
    private void updateBillTotalAmount(Long billId) {
        List<BillDetail> details = billDetailRepository.findByBillId(billId);
        BigDecimal totalAmount = details.stream()
                .map(BillDetail::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("账单不存在，ID: " + billId));
        bill.setTotalAmount(totalAmount);
        billRepository.save(bill);
    }
}

