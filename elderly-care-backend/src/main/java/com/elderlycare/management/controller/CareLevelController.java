package com.elderlycare.management.controller;

import com.elderlycare.management.dto.*;
import com.elderlycare.management.service.CareLevelService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/care-levels")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CareLevelController {

    private final CareLevelService careLevelService;

    public CareLevelController(CareLevelService careLevelService) {
        this.careLevelService = careLevelService;
    }

    /**
     * 获取护理等级列表（分页）
     */
    @GetMapping
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管') or hasRole('财务人员')")
    public ResponseEntity<ApiResponse<PageResponse<CareLevelResponse>>> getCareLevelList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String levelCode,
            @RequestParam(required = false) String levelName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        CareLevelQueryRequest request = new CareLevelQueryRequest();
        request.setPage(page);
        request.setSize(size);
        request.setLevelCode(levelCode);
        request.setLevelName(levelName);
        request.setDescription(description);
        request.setMinPrice(minPrice);
        request.setMaxPrice(maxPrice);

        try {
            PageResponse<CareLevelResponse> result = careLevelService.getCareLevelList(request);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            System.err.println("Error in getCareLevelList: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<PageResponse<CareLevelResponse>>error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 获取所有护理等级（不分页）
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管') or hasRole('财务人员') or hasRole('前台接待')")
    public ResponseEntity<ApiResponse<List<CareLevelResponse>>> getAllCareLevels() {
        try {
            List<CareLevelResponse> careLevels = careLevelService.getAllCareLevels();
            return ResponseEntity.ok(ApiResponse.success(careLevels));
        } catch (Exception e) {
            System.err.println("Error in getAllCareLevels: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<List<CareLevelResponse>>error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 根据编码获取护理等级信息
     */
    @GetMapping("/{levelCode}")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('护理主管') or hasRole('财务人员')")
    public ResponseEntity<ApiResponse<CareLevelResponse>> getCareLevelByCode(@PathVariable String levelCode) {
        try {
            CareLevelResponse careLevel = careLevelService.getCareLevelByCode(levelCode);
            return ResponseEntity.ok(ApiResponse.success(careLevel));
        } catch (Exception e) {
            System.err.println("Error in getCareLevelByCode: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<CareLevelResponse>error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 根据价格范围获取护理等级
     */
    @GetMapping("/price-range")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长') or hasRole('财务人员')")
    public ResponseEntity<ApiResponse<List<CareLevelResponse>>> getCareLevelsByPriceRange(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        try {
            List<CareLevelResponse> careLevels = careLevelService.getCareLevelsByPriceRange(minPrice, maxPrice);
            return ResponseEntity.ok(ApiResponse.success(careLevels));
        } catch (Exception e) {
            System.err.println("Error in getCareLevelsByPriceRange: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<List<CareLevelResponse>>error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 创建新护理等级
     */
    @PostMapping
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长')")
    public ResponseEntity<ApiResponse<CareLevelResponse>> createCareLevel(@Valid @RequestBody CareLevelCreateRequest request) {
        try {
            CareLevelResponse careLevel = careLevelService.createCareLevel(request);
            return ResponseEntity.ok(ApiResponse.success("护理等级创建成功", careLevel));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<CareLevelResponse>error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error in createCareLevel: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<CareLevelResponse>error("创建失败: " + e.getMessage()));
        }
    }

    /**
     * 更新护理等级信息
     */
    @PutMapping("/{levelCode}")
    @PreAuthorize("hasRole('系统管理员') or hasRole('院长')")
    public ResponseEntity<ApiResponse<CareLevelResponse>> updateCareLevel(
            @PathVariable String levelCode,
            @Valid @RequestBody CareLevelUpdateRequest request) {
        try {
            CareLevelResponse careLevel = careLevelService.updateCareLevel(levelCode, request);
            return ResponseEntity.ok(ApiResponse.success("护理等级信息更新成功", careLevel));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<CareLevelResponse>error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error in updateCareLevel: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<CareLevelResponse>error("更新失败: " + e.getMessage()));
        }
    }

    /**
     * 删除护理等级
     */
    @DeleteMapping("/{levelCode}")
    @PreAuthorize("hasRole('系统管理员')")
    public ResponseEntity<ApiResponse<String>> deleteCareLevel(@PathVariable String levelCode) {
        try {
            careLevelService.deleteCareLevel(levelCode);
            return ResponseEntity.ok(ApiResponse.success("护理等级删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<String>error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error in deleteCareLevel: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<String>error("删除失败: " + e.getMessage()));
        }
    }
}
