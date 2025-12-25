package com.elderlycare.management.controller;

import com.elderlycare.management.dto.*;
import com.elderlycare.management.service.BillService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    /**
     * 获取账单列表（分页）
     */
    @GetMapping
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管') or hasRole('前台接待')")
    public ResponseEntity<ApiResponse<PageResponse<BillResponse>>> getBillList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long elderId,
            @RequestParam(required = false) String billMonth,
            @RequestParam(required = false) Integer status) {

        BillQueryRequest request = new BillQueryRequest();
        request.setPage(page);
        request.setSize(size);
        request.setElderId(elderId);
        request.setBillMonth(billMonth);
        request.setStatus(status);

        try {
            PageResponse<BillResponse> result = billService.getBillList(request);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<PageResponse<BillResponse>>error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID获取账单信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管') or hasRole('前台接待')")
    public ResponseEntity<ApiResponse<BillResponse>> getBillById(@PathVariable Long id) {
        try {
            BillResponse bill = billService.getBillById(id);
            return ResponseEntity.ok(ApiResponse.success(bill));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<BillResponse>error(e.getMessage()));
        }
    }

    /**
     * 根据老人ID和月份获取账单
     */
    @GetMapping("/elder/{elderId}/month/{billMonth}")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管') or hasRole('前台接待')")
    public ResponseEntity<ApiResponse<BillResponse>> getBillByElderAndMonth(
            @PathVariable Long elderId,
            @PathVariable String billMonth) {
        try {
            BillResponse bill = billService.getBillByElderAndMonth(elderId, billMonth);
            return ResponseEntity.ok(ApiResponse.success(bill));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<BillResponse>error(e.getMessage()));
        }
    }

    /**
     * 创建账单
     */
    @PostMapping
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管')")
    public ResponseEntity<ApiResponse<BillResponse>> createBill(@Valid @RequestBody BillCreateRequest request) {
        try {
            BillResponse bill = billService.createBill(request);
            return ResponseEntity.ok(ApiResponse.success("账单创建成功", bill));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<BillResponse>error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<BillResponse>error("创建失败: " + e.getMessage()));
        }
    }

    /**
     * 更新账单
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管')")
    public ResponseEntity<ApiResponse<BillResponse>> updateBill(
            @PathVariable Long id,
            @Valid @RequestBody BillUpdateRequest request) {
        try {
            BillResponse bill = billService.updateBill(id, request);
            return ResponseEntity.ok(ApiResponse.success("账单更新成功", bill));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<BillResponse>error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<BillResponse>error("更新失败: " + e.getMessage()));
        }
    }

    /**
     * 删除账单
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长')")
    public ResponseEntity<ApiResponse<String>> deleteBill(@PathVariable Long id) {
        try {
            billService.deleteBill(id);
            return ResponseEntity.ok(ApiResponse.success("账单删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<String>error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<String>error("删除失败: " + e.getMessage()));
        }
    }

    /**
     * 支付账单
     */
    @PostMapping("/{id}/pay")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('前台接待')")
    public ResponseEntity<ApiResponse<BillResponse>> payBill(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, BigDecimal> request) {
        try {
            BigDecimal paidAmount = request.get("paidAmount");
            if (paidAmount == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.<BillResponse>error("支付金额不能为空"));
            }
            BillResponse bill = billService.payBill(id, paidAmount);
            return ResponseEntity.ok(ApiResponse.success("支付成功", bill));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<BillResponse>error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<BillResponse>error("支付失败: " + e.getMessage()));
        }
    }
}

