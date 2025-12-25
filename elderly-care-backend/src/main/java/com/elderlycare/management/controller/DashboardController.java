package com.elderlycare.management.controller;

import com.elderlycare.management.dto.ApiResponse;
import com.elderlycare.management.dto.DashboardResponse;
import com.elderlycare.management.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * 获取仪表盘数据
     */
    @GetMapping
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管') or hasRole('护理员') or hasRole('前台接待')")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardData() {
        try {
            DashboardResponse response = dashboardService.getDashboardData();
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<DashboardResponse>error("获取仪表盘数据失败: " + e.getMessage()));
        }
    }
}