package com.elderlycare.management.service;

import com.elderlycare.management.dto.*;
import com.elderlycare.management.entity.Bill;
import com.elderlycare.management.entity.BillDetail;
import com.elderlycare.management.entity.Elder;
import com.elderlycare.management.entity.FeeItem;
import com.elderlycare.management.exception.ResourceNotFoundException;
import com.elderlycare.management.repository.BillDetailRepository;
import com.elderlycare.management.repository.BillRepository;
import com.elderlycare.management.repository.ElderRepository;
import com.elderlycare.management.repository.FeeItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final BillDetailRepository billDetailRepository;
    private final ElderRepository elderRepository;
    private final FeeItemRepository feeItemRepository;

    public BillService(BillRepository billRepository, BillDetailRepository billDetailRepository,
                       ElderRepository elderRepository, FeeItemRepository feeItemRepository) {
        this.billRepository = billRepository;
        this.billDetailRepository = billDetailRepository;
        this.elderRepository = elderRepository;
        this.feeItemRepository = feeItemRepository;
    }

    /**
     * 分页查询账单列表
     */
    public PageResponse<BillResponse> getBillList(BillQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());

        Specification<Bill> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getElderId() != null) {
                predicates.add(cb.equal(root.get("elderId"), request.getElderId()));
            }
            if (request.getBillMonth() != null && !request.getBillMonth().trim().isEmpty()) {
                predicates.add(cb.equal(root.get("billMonth"), request.getBillMonth().trim()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            if (predicates.isEmpty()) {
                return null;
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Bill> page = billRepository.findAll(spec, pageable);
        List<BillResponse> responses = page.getContent().stream()
                .map(bill -> {
                    BillResponse response = new BillResponse(bill);
                    // 加载明细
                    List<BillDetail> details = billDetailRepository.findByBillId(bill.getId());
                    response.setDetails(details.stream()
                            .map(BillDetailResponse::new)
                            .collect(Collectors.toList()));
                    return response;
                })
                .collect(Collectors.toList());

        return new PageResponse<>(responses, request.getPage(), request.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    /**
     * 根据ID获取账单信息
     */
    public BillResponse getBillById(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("账单不存在，ID: " + id));
        BillResponse response = new BillResponse(bill);
        // 加载明细
        List<BillDetail> details = billDetailRepository.findByBillId(id);
        response.setDetails(details.stream()
                .map(BillDetailResponse::new)
                .collect(Collectors.toList()));
        return response;
    }

    /**
     * 根据老人ID和月份获取账单
     */
    public BillResponse getBillByElderAndMonth(Long elderId, String billMonth) {
        Bill bill = billRepository.findByElderIdAndBillMonth(elderId, billMonth)
                .orElseThrow(() -> new ResourceNotFoundException("账单不存在，老人ID: " + elderId + ", 月份: " + billMonth));
        BillResponse response = new BillResponse(bill);
        // 加载明细
        List<BillDetail> details = billDetailRepository.findByBillId(bill.getId());
        response.setDetails(details.stream()
                .map(BillDetailResponse::new)
                .collect(Collectors.toList()));
        return response;
    }

    /**
     * 创建账单
     */
    @Transactional
    public BillResponse createBill(BillCreateRequest request) {
        // 验证老人是否存在
        Elder elder = elderRepository.findById(request.getElderId())
                .orElseThrow(() -> new ResourceNotFoundException("老人不存在，ID: " + request.getElderId()));

        // 检查该老人该月份是否已有账单
        if (billRepository.existsByElderIdAndBillMonth(request.getElderId(), request.getBillMonth())) {
            throw new RuntimeException("该老人该月份账单已存在: " + request.getBillMonth());
        }

        // 创建账单
        Bill bill = new Bill();
        bill.setElderId(request.getElderId());
        bill.setBillMonth(request.getBillMonth().trim());
        bill.setTotalAmount(request.getTotalAmount());
        bill.setPaidAmount(request.getPaidAmount() != null ? request.getPaidAmount() : BigDecimal.ZERO);
        bill.setStatus(request.getStatus() != null ? request.getStatus() : 0);

        Bill savedBill = billRepository.save(bill);

        // 创建账单明细
        if (request.getDetails() != null && !request.getDetails().isEmpty()) {
            BigDecimal calculatedTotal = BigDecimal.ZERO;
            for (BillDetailCreateRequest detailRequest : request.getDetails()) {
                // 验证费用项目是否存在
                FeeItem feeItem = feeItemRepository.findById(detailRequest.getItemCode())
                        .orElseThrow(() -> new ResourceNotFoundException("费用项目不存在: " + detailRequest.getItemCode()));

                BillDetail detail = new BillDetail();
                detail.setBillId(savedBill.getId());
                detail.setItemCode(detailRequest.getItemCode());
                detail.setQuantity(detailRequest.getQuantity() != null ? detailRequest.getQuantity() : 1);
                // 如果未提供单价，使用费用项目的单价
                detail.setUnitPrice(detailRequest.getUnitPrice() != null ?
                        detailRequest.getUnitPrice() : feeItem.getUnitPrice());
                // 计算金额：如果未提供，则使用 单价 * 数量
                if (detailRequest.getAmount() != null) {
                    detail.setAmount(detailRequest.getAmount());
                } else {
                    detail.setAmount(detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())));
                }
                calculatedTotal = calculatedTotal.add(detail.getAmount());
                billDetailRepository.save(detail);
            }
            // 如果未提供总金额，使用明细计算的总金额
            if (request.getTotalAmount() == null) {
                savedBill.setTotalAmount(calculatedTotal);
                billRepository.save(savedBill);
            }
        }

        return getBillById(savedBill.getId());
    }

    /**
     * 更新账单
     */
    @Transactional
    public BillResponse updateBill(Long id, BillUpdateRequest request) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("账单不存在，ID: " + id));

        // 更新账单基本信息
        if (request.getTotalAmount() != null) {
            bill.setTotalAmount(request.getTotalAmount());
        }
        if (request.getPaidAmount() != null) {
            bill.setPaidAmount(request.getPaidAmount());
            // 如果已付金额大于等于应付金额，自动设置为已缴清
            if (bill.getPaidAmount().compareTo(bill.getTotalAmount()) >= 0) {
                bill.setStatus(1);
            }
        }
        if (request.getStatus() != null) {
            bill.setStatus(request.getStatus());
        }

        Bill savedBill = billRepository.save(bill);

        // 更新账单明细
        if (request.getDetails() != null) {
            // 删除原有明细
            billDetailRepository.deleteByBillId(id);
            // 创建新明细
            BigDecimal calculatedTotal = BigDecimal.ZERO;
            for (BillDetailCreateRequest detailRequest : request.getDetails()) {
                FeeItem feeItem = feeItemRepository.findById(detailRequest.getItemCode())
                        .orElseThrow(() -> new ResourceNotFoundException("费用项目不存在: " + detailRequest.getItemCode()));

                BillDetail detail = new BillDetail();
                detail.setBillId(savedBill.getId());
                detail.setItemCode(detailRequest.getItemCode());
                detail.setQuantity(detailRequest.getQuantity() != null ? detailRequest.getQuantity() : 1);
                detail.setUnitPrice(detailRequest.getUnitPrice() != null ?
                        detailRequest.getUnitPrice() : feeItem.getUnitPrice());
                if (detailRequest.getAmount() != null) {
                    detail.setAmount(detailRequest.getAmount());
                } else {
                    detail.setAmount(detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())));
                }
                calculatedTotal = calculatedTotal.add(detail.getAmount());
                billDetailRepository.save(detail);
            }
            // 如果未提供总金额，使用明细计算的总金额
            if (request.getTotalAmount() == null) {
                savedBill.setTotalAmount(calculatedTotal);
                billRepository.save(savedBill);
            }
        }

        return getBillById(savedBill.getId());
    }

    /**
     * 删除账单
     */
    @Transactional
    public void deleteBill(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("账单不存在，ID: " + id));

        // 删除账单明细
        billDetailRepository.deleteByBillId(id);
        // 删除账单
        billRepository.deleteById(id);
    }

    /**
     * 支付账单（更新已付金额）
     */
    @Transactional
    public BillResponse payBill(Long id, BigDecimal paidAmount) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("账单不存在，ID: " + id));

        BigDecimal newPaidAmount = bill.getPaidAmount().add(paidAmount);
        if (newPaidAmount.compareTo(bill.getTotalAmount()) > 0) {
            throw new RuntimeException("支付金额超过应付金额");
        }

        bill.setPaidAmount(newPaidAmount);
        // 如果已付金额等于应付金额，设置为已缴清
        if (newPaidAmount.compareTo(bill.getTotalAmount()) >= 0) {
            bill.setStatus(1);
        }

        billRepository.save(bill);
        return getBillById(id);
    }
}

