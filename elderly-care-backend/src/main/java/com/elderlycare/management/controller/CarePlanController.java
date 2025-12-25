package com.elderlycare.management.controller;

import com.elderlycare.management.dto.*;
import com.elderlycare.management.service.CarePlanService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/care-plans")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CarePlanController {

    private final CarePlanService carePlanService;

    public CarePlanController(CarePlanService carePlanService) {
        this.carePlanService = carePlanService;
    }

    /**
     * 获取护理计划列表（分页）
     */
    @GetMapping
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管') or hasRole('护理员')")
    public ResponseEntity<ApiResponse<PageResponse<CarePlanResponse>>> getCarePlanList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long elderId,
            @RequestParam(required = false) String careLevelId,
            @RequestParam(required = false) String planContent) {

        CarePlanQueryRequest request = new CarePlanQueryRequest();
        request.setPage(page);
        request.setSize(size);
        request.setElderId(elderId);
        request.setCareLevelId(careLevelId);
        request.setPlanContent(planContent);

        try {
            PageResponse<CarePlanResponse> result = carePlanService.getCarePlanList(request);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            System.err.println("Error in getCarePlanList: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<PageResponse<CarePlanResponse>>error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID获取护理计划信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管') or hasRole('护理员')")
    public ResponseEntity<ApiResponse<CarePlanResponse>> getCarePlanById(@PathVariable Long id) {
        try {
            CarePlanResponse carePlan = carePlanService.getCarePlanById(id);
            return ResponseEntity.ok(ApiResponse.success(carePlan));
        } catch (Exception e) {
            System.err.println("Error in getCarePlanById: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<CarePlanResponse>error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 根据老人ID获取最新的护理计划
     */
    @GetMapping("/elder/{elderId}/latest")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管') or hasRole('护理员')")
    public ResponseEntity<ApiResponse<CarePlanResponse>> getLatestCarePlanByElderId(@PathVariable Long elderId) {
        try {
            CarePlanResponse carePlan = carePlanService.getLatestCarePlanByElderId(elderId);
            return ResponseEntity.ok(ApiResponse.success(carePlan));
        } catch (Exception e) {
            System.err.println("Error in getLatestCarePlanByElderId: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<CarePlanResponse>error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 创建护理计划
     */
    @PostMapping
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管')")
    public ResponseEntity<ApiResponse<CarePlanResponse>> createCarePlan(@Valid @RequestBody CarePlanCreateRequest request) {
        try {
            CarePlanResponse carePlan = carePlanService.createCarePlan(request);
            return ResponseEntity.ok(ApiResponse.success("护理计划创建成功", carePlan));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<CarePlanResponse>error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error in createCarePlan: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<CarePlanResponse>error("创建失败: " + e.getMessage()));
        }
    }

    /**
     * 更新护理计划
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管')")
    public ResponseEntity<ApiResponse<CarePlanResponse>> updateCarePlan(
            @PathVariable Long id,
            @Valid @RequestBody CarePlanUpdateRequest request) {
        try {
            CarePlanResponse carePlan = carePlanService.updateCarePlan(id, request);
            return ResponseEntity.ok(ApiResponse.success("护理计划更新成功", carePlan));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<CarePlanResponse>error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error in updateCarePlan: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<CarePlanResponse>error("更新失败: " + e.getMessage()));
        }
    }

    /**
     * 删除护理计划
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管')")
    public ResponseEntity<ApiResponse<String>> deleteCarePlan(@PathVariable Long id) {
        try {
            carePlanService.deleteCarePlan(id);
            return ResponseEntity.ok(ApiResponse.success("护理计划删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<String>error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error in deleteCarePlan: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<String>error("删除失败: " + e.getMessage()));
        }
    }
}