package com.elderlycare.management.controller;

import com.elderlycare.management.dto.*;
import com.elderlycare.management.service.FeeItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fee-items")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FeeItemController {

    private final FeeItemService feeItemService;

    public FeeItemController(FeeItemService feeItemService) {
        this.feeItemService = feeItemService;
    }

    /**
     * 获取费用项目列表（分页）
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<FeeItemResponse>>> getFeeItemList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String itemCode,
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) String itemType) {

        FeeItemQueryRequest request = new FeeItemQueryRequest();
        request.setPage(page);
        request.setSize(size);
        request.setItemCode(itemCode);
        request.setItemName(itemName);
        request.setItemType(itemType);

        try {
            PageResponse<FeeItemResponse> result = feeItemService.getFeeItemList(request);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            System.err.println("Error in getFeeItemList: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<PageResponse<FeeItemResponse>>error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 根据代码获取费用项目
     */
    @GetMapping("/{itemCode}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<FeeItemResponse>> getFeeItemByCode(@PathVariable String itemCode) {
        FeeItemResponse feeItem = feeItemService.getFeeItemByCode(itemCode);
        return ResponseEntity.ok(ApiResponse.success(feeItem));
    }

    /**
     * 创建费用项目
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<FeeItemResponse>> createFeeItem(@Valid @RequestBody FeeItemCreateRequest request) {
        FeeItemResponse feeItem = feeItemService.createFeeItem(request);
        return ResponseEntity.ok(ApiResponse.success("费用项目创建成功", feeItem));
    }

    /**
     * 更新费用项目
     */
    @PutMapping("/{itemCode}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<FeeItemResponse>> updateFeeItem(
            @PathVariable String itemCode,
            @Valid @RequestBody FeeItemUpdateRequest request) {
        FeeItemResponse feeItem = feeItemService.updateFeeItem(itemCode, request);
        return ResponseEntity.ok(ApiResponse.success("费用项目更新成功", feeItem));
    }

    /**
     * 删除费用项目
     */
    @DeleteMapping("/{itemCode}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> deleteFeeItem(@PathVariable String itemCode) {
        feeItemService.deleteFeeItem(itemCode);
        return ResponseEntity.ok(ApiResponse.success("费用项目删除成功"));
    }

    /**
     * 根据类型获取费用项目列表
     */
    @GetMapping("/type/{itemType}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<FeeItemResponse>>> getFeeItemsByType(@PathVariable String itemType) {
        List<FeeItemResponse> feeItems = feeItemService.getFeeItemsByType(itemType);
        return ResponseEntity.ok(ApiResponse.success(feeItems));
    }
}