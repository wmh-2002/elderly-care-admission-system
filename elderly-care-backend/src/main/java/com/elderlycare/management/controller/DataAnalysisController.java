package com.elderlycare.management.controller;

import com.elderlycare.management.dto.ApiResponse;
import com.elderlycare.management.dto.DataAnalysisResponse;
import com.elderlycare.management.service.DataAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/data-analysis")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DataAnalysisController {

    private final DataAnalysisService dataAnalysisService;

    public DataAnalysisController(DataAnalysisService dataAnalysisService) {
        this.dataAnalysisService = dataAnalysisService;
    }

    /**
     * 获取老人分析数据
     */
    @GetMapping("/elder")
    //@PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DataAnalysisResponse.ElderAnalysisData>> getElderAnalysisData() {
        try {
            DataAnalysisResponse.ElderAnalysisData response = dataAnalysisService.getElderAnalysisData();
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<DataAnalysisResponse.ElderAnalysisData>error("获取老人分析数据失败: " + e.getMessage()));
        }
    }

    /**
     * 获取营收分析数据
     */
    @GetMapping("/revenue")
    //@PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DataAnalysisResponse.RevenueAnalysisData>> getRevenueAnalysisData() {
        try {
            DataAnalysisResponse.RevenueAnalysisData response = dataAnalysisService.getRevenueAnalysisData();
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<DataAnalysisResponse.RevenueAnalysisData>error("获取营收分析数据失败: " + e.getMessage()));
        }
    }

    /**
     * 获取运营分析数据
     */
    @GetMapping("/operation")
    //@PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DataAnalysisResponse.OperationAnalysisData>> getOperationAnalysisData() {
        try {
            DataAnalysisResponse.OperationAnalysisData response = dataAnalysisService.getOperationAnalysisData();
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<DataAnalysisResponse.OperationAnalysisData>error("获取运营分析数据失败: " + e.getMessage()));
        }
    }

    /**
     * 获取完整的数据分析响应
     */
    @GetMapping
    //@PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DataAnalysisResponse>> getAllAnalysisData() {
        try {
            DataAnalysisResponse response = dataAnalysisService.getAllAnalysisData();
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<DataAnalysisResponse>error("获取数据分析失败: " + e.getMessage()));
        }
    }
}
