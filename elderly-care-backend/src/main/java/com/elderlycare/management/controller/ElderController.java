package com.elderlycare.management.controller;

import com.elderlycare.management.dto.*;
import com.elderlycare.management.service.ElderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/elders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ElderController {

    private final ElderService elderService;

    public ElderController(ElderService elderService) {
        this.elderService = elderService;
    }

    /**
     * 获取老人列表（分页）
     */
    @GetMapping
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管') or hasRole('护理员') or hasRole('前台接待')")
    public ResponseEntity<ApiResponse<PageResponse<ElderResponse>>> getElderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String elderNo,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String idCard,
            @RequestParam(required = false) Long bedId,
            @RequestParam(required = false) String careLevel,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String checkinDateFrom,
            @RequestParam(required = false) String checkinDateTo) {

        ElderQueryRequest request = new ElderQueryRequest();
        request.setPage(page);
        request.setSize(size);
        request.setElderNo(elderNo);
        request.setName(name);
        request.setGender(gender);
        request.setPhone(phone);
        request.setIdCard(idCard);
        request.setBedId(bedId);
        request.setCareLevel(careLevel);
        request.setStatus(status);
        if (checkinDateFrom != null && !checkinDateFrom.isBlank()) {
            request.setCheckinDateFrom(java.time.LocalDate.parse(checkinDateFrom));
        }
        if (checkinDateTo != null && !checkinDateTo.isBlank()) {
            request.setCheckinDateTo(java.time.LocalDate.parse(checkinDateTo));
        }

        try {
            PageResponse<ElderResponse> result = elderService.getElderList(request);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<PageResponse<ElderResponse>>error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID获取老人信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管') or hasRole('护理员') or hasRole('前台接待')")
    public ResponseEntity<ApiResponse<ElderResponse>> getElderById(@PathVariable Long id) {
        ElderResponse elder = elderService.getElderById(id);
        return ResponseEntity.ok(ApiResponse.success(elder));
    }

    /**
     * 创建老人档案
     */
    @PostMapping
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管')")
    public ResponseEntity<ApiResponse<ElderResponse>> createElder(@Valid @RequestBody ElderCreateRequest request) {
        try {
            ElderResponse elder = elderService.createElder(request);
            return ResponseEntity.ok(ApiResponse.success("老人档案创建成功", elder));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<ElderResponse>error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<ElderResponse>error("创建失败: " + e.getMessage()));
        }
    }

    /**
     * 更新老人档案
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管')")
    public ResponseEntity<ApiResponse<ElderResponse>> updateElder(
            @PathVariable Long id,
            @Valid @RequestBody ElderUpdateRequest request) {
        try {
            ElderResponse elder = elderService.updateElder(id, request);
            return ResponseEntity.ok(ApiResponse.success("老人档案更新成功", elder));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<ElderResponse>error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<ElderResponse>error("更新失败: " + e.getMessage()));
        }
    }

    /**
     * 删除老人档案
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长')")
    public ResponseEntity<ApiResponse<String>> deleteElder(@PathVariable Long id) {
        try {
            elderService.deleteElder(id);
            return ResponseEntity.ok(ApiResponse.success("老人档案删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<String>error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<String>error("删除失败: " + e.getMessage()));
        }
    }
}

