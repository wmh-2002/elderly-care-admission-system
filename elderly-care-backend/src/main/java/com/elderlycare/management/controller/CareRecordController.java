package com.elderlycare.management.controller;

import com.elderlycare.management.dto.*;
import com.elderlycare.management.service.CareRecordService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/care-records")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CareRecordController {

    private final CareRecordService careRecordService;

    public CareRecordController(CareRecordService careRecordService) {
        this.careRecordService = careRecordService;
    }

    /**
     * 获取护理记录列表（分页）
     */
    @GetMapping
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管') or hasRole('护理员')")
    public ResponseEntity<ApiResponse<PageResponse<CareRecordResponse>>> getCareRecordList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long elderId,
            @RequestParam(required = false) Long nurseId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String recordDate) {

        CareRecordQueryRequest request = new CareRecordQueryRequest();
        request.setPage(page);
        request.setSize(size);
        request.setElderId(elderId);
        request.setNurseId(nurseId);
        
        if (startDate != null && !startDate.isEmpty()) {
            request.setStartDate(LocalDate.parse(startDate));
        }
        if (endDate != null && !endDate.isEmpty()) {
            request.setEndDate(LocalDate.parse(endDate));
        }
        if (recordDate != null && !recordDate.isEmpty()) {
            request.setRecordDate(LocalDate.parse(recordDate));
        }

        try {
            PageResponse<CareRecordResponse> result = careRecordService.getCareRecordList(request);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            System.err.println("Error in getCareRecordList: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<PageResponse<CareRecordResponse>>error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID获取护理记录信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管') or hasRole('护理员')")
    public ResponseEntity<ApiResponse<CareRecordResponse>> getCareRecordById(@PathVariable Long id) {
        try {
            CareRecordResponse careRecord = careRecordService.getCareRecordById(id);
            return ResponseEntity.ok(ApiResponse.success(careRecord));
        } catch (Exception e) {
            System.err.println("Error in getCareRecordById: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<CareRecordResponse>error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 根据老人ID获取护理记录列表
     */
    @GetMapping("/elder/{elderId}")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管') or hasRole('护理员')")
    public ResponseEntity<ApiResponse<List<CareRecordResponse>>> getCareRecordsByElderId(@PathVariable Long elderId) {
        try {
            List<CareRecordResponse> careRecords = careRecordService.getCareRecordsByElderId(elderId);
            return ResponseEntity.ok(ApiResponse.success(careRecords));
        } catch (Exception e) {
            System.err.println("Error in getCareRecordsByElderId: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<List<CareRecordResponse>>error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 创建护理记录
     */
    @PostMapping
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管') or hasRole('护理员')")
    public ResponseEntity<ApiResponse<CareRecordResponse>> createCareRecord(@Valid @RequestBody CareRecordCreateRequest request) {
        try {
            CareRecordResponse careRecord = careRecordService.createCareRecord(request);
            return ResponseEntity.ok(ApiResponse.success("护理记录创建成功", careRecord));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<CareRecordResponse>error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error in createCareRecord: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<CareRecordResponse>error("创建失败: " + e.getMessage()));
        }
    }

    /**
     * 更新护理记录
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管') or hasRole('护理员')")
    public ResponseEntity<ApiResponse<CareRecordResponse>> updateCareRecord(
            @PathVariable Long id,
            @Valid @RequestBody CareRecordUpdateRequest request) {
        try {
            CareRecordResponse careRecord = careRecordService.updateCareRecord(id, request);
            return ResponseEntity.ok(ApiResponse.success("护理记录更新成功", careRecord));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<CareRecordResponse>error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error in updateCareRecord: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<CareRecordResponse>error("更新失败: " + e.getMessage()));
        }
    }

    /**
     * 删除护理记录
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管')")
    public ResponseEntity<ApiResponse<String>> deleteCareRecord(@PathVariable Long id) {
        try {
            careRecordService.deleteCareRecord(id);
            return ResponseEntity.ok(ApiResponse.success("护理记录删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<String>error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error in deleteCareRecord: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<String>error("删除失败: " + e.getMessage()));
        }
    }
}