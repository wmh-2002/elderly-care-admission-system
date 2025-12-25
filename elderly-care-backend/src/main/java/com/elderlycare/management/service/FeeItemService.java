package com.elderlycare.management.service;

import com.elderlycare.management.dto.*;
import com.elderlycare.management.entity.FeeItem;
import com.elderlycare.management.exception.ResourceNotFoundException;
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
public class FeeItemService {

    private final FeeItemRepository feeItemRepository;

    public FeeItemService(FeeItemRepository feeItemRepository) {
        this.feeItemRepository = feeItemRepository;
    }

    /**
     * 分页查询费用项目列表
     */
    public PageResponse<FeeItemResponse> getFeeItemList(FeeItemQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());

        Specification<FeeItem> spec = createSpecification(request);

        Page<FeeItem> feeItemPage = feeItemRepository.findAll(spec, pageable);

        List<FeeItemResponse> feeItemResponses = feeItemPage.getContent().stream()
                .map(this::convertToFeeItemResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(feeItemResponses, request.getPage(), request.getSize(),
                feeItemPage.getTotalElements(), feeItemPage.getTotalPages(), feeItemPage.isLast());
    }

    private Specification<FeeItem> createSpecification(FeeItemQueryRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getItemCode() != null && !request.getItemCode().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("itemCode"), "%" + request.getItemCode().trim() + "%"));
            }

            if (request.getItemName() != null && !request.getItemName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("itemName"), "%" + request.getItemName().trim() + "%"));
            }

            if (request.getItemType() != null && !request.getItemType().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("itemType"), "%" + request.getItemType().trim() + "%"));
            }

            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction(); // Return all records
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 根据代码获取费用项目
     */
    public FeeItemResponse getFeeItemByCode(String itemCode) {
        FeeItem feeItem = feeItemRepository.findByItemCode(itemCode)
                .orElseThrow(() -> new ResourceNotFoundException("费用项目不存在，代码: " + itemCode));
        return convertToFeeItemResponse(feeItem);
    }

    /**
     * 创建费用项目
     */
    @Transactional
    public FeeItemResponse createFeeItem(FeeItemCreateRequest request) {
        // 检查费用项目代码是否已存在
        if (feeItemRepository.existsByItemCode(request.getItemCode())) {
            throw new RuntimeException("费用项目代码已存在: " + request.getItemCode());
        }

        FeeItem feeItem = new FeeItem();
        feeItem.setItemCode(request.getItemCode().trim().toUpperCase());
        feeItem.setItemName(request.getItemName().trim());
        feeItem.setUnitPrice(request.getUnitPrice());
        feeItem.setItemType(request.getItemType().trim());

        FeeItem savedFeeItem = feeItemRepository.save(feeItem);
        return convertToFeeItemResponse(savedFeeItem);
    }

    /**
     * 更新费用项目
     */
    @Transactional
    public FeeItemResponse updateFeeItem(String itemCode, FeeItemUpdateRequest request) {
        FeeItem feeItem = feeItemRepository.findByItemCode(itemCode)
                .orElseThrow(() -> new ResourceNotFoundException("费用项目不存在，代码: " + itemCode));

        // 更新字段
        feeItem.setItemName(request.getItemName().trim());
        feeItem.setUnitPrice(request.getUnitPrice());
        feeItem.setItemType(request.getItemType().trim());

        FeeItem savedFeeItem = feeItemRepository.save(feeItem);
        return convertToFeeItemResponse(savedFeeItem);
    }

    /**
     * 删除费用项目
     */
    @Transactional
    public void deleteFeeItem(String itemCode) {
        if (!feeItemRepository.existsByItemCode(itemCode)) {
            throw new ResourceNotFoundException("费用项目不存在，代码: " + itemCode);
        }
        feeItemRepository.deleteById(itemCode);
    }

    /**
     * 根据类型查询费用项目列表
     */
    public List<FeeItemResponse> getFeeItemsByType(String itemType) {
        List<FeeItem> feeItems = feeItemRepository.findByItemType(itemType);
        return feeItems.stream()
                .map(this::convertToFeeItemResponse)
                .collect(Collectors.toList());
    }

    private FeeItemResponse convertToFeeItemResponse(FeeItem feeItem) {
        FeeItemResponse response = new FeeItemResponse();
        response.setItemCode(feeItem.getItemCode());
        response.setItemName(feeItem.getItemName());
        response.setUnitPrice(feeItem.getUnitPrice());
        response.setItemType(feeItem.getItemType());
        response.setCreatedAt(feeItem.getCreatedAt());
        response.setUpdatedAt(feeItem.getUpdatedAt());
        return response;
    }
}