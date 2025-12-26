package com.elderlycare.management.controller;

import com.elderlycare.management.dto.*;
import com.elderlycare.management.service.BillDetailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bill-details")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BillDetailController {

    private final BillDetailService billDetailService;

    public BillDetailController(BillDetailService billDetailService) {
        this.billDetailService = billDetailService;
    }

    /**
     * 根据账单ID获取账单明细列表
     */
    @GetMapping("/bill/{billId}")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管') or hasRole('前台接待')")
    public ResponseEntity<ApiResponse<List<BillDetailResponse>>> getBillDetailsByBillId(@PathVariable Long billId) {
        try {
            List<BillDetailResponse> details = billDetailService.getBillDetailsByBillId(billId);
            return ResponseEntity.ok(ApiResponse.success(details));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<List<BillDetailResponse>>error(e.getMessage()));
        }
    }

    /**
     * 根据ID获取账单明细
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管') or hasRole('前台接待')")
    public ResponseEntity<ApiResponse<BillDetailResponse>> getBillDetailById(@PathVariable Long id) {
        try {
            BillDetailResponse detail = billDetailService.getBillDetailById(id);
            return ResponseEntity.ok(ApiResponse.success(detail));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<BillDetailResponse>error(e.getMessage()));
        }
    }

    /**
     * 创建账单明细
     */
    @PostMapping("/bill/{billId}")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管')")
    public ResponseEntity<ApiResponse<BillDetailResponse>> createBillDetail(
            @PathVariable Long billId,
            @Valid @RequestBody BillDetailCreateRequest request) {
        try {
            BillDetailResponse detail = billDetailService.createBillDetail(billId, request);
            return ResponseEntity.ok(ApiResponse.success("账单明细创建成功", detail));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<BillDetailResponse>error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<BillDetailResponse>error("创建失败: " + e.getMessage()));
        }
    }

    /**
     * 更新账单明细
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管')")
    public ResponseEntity<ApiResponse<BillDetailResponse>> updateBillDetail(
            @PathVariable Long id,
            @Valid @RequestBody BillDetailUpdateRequest request) {
        try {
            BillDetailResponse detail = billDetailService.updateBillDetail(id, request);
            return ResponseEntity.ok(ApiResponse.success("账单明细更新成功", detail));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<BillDetailResponse>error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<BillDetailResponse>error("更新失败: " + e.getMessage()));
        }
    }

    /**
     * 删除账单明细
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管')")
    public ResponseEntity<ApiResponse<String>> deleteBillDetail(@PathVariable Long id) {
        try {
            billDetailService.deleteBillDetail(id);
            return ResponseEntity.ok(ApiResponse.success("账单明细删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<String>error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<String>error("删除失败: " + e.getMessage()));
        }
    }
}


