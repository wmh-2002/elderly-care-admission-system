package com.elderlycare.management.controller;

import com.elderlycare.management.dto.*;
import com.elderlycare.management.service.BedService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beds")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BedController {

    private final BedService bedService;

    public BedController(BedService bedService) {
        this.bedService = bedService;
    }

    /**
     * 获取床位列表（分页）
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<BedResponse>>> getBedList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) String bedNo,
            @RequestParam(required = false) Integer status) {

        BedQueryRequest request = new BedQueryRequest();
        request.setPage(page);
        request.setSize(size);
        request.setRoomId(roomId);
        request.setBedNo(bedNo);
        request.setStatus(status);

        try {
            PageResponse<BedResponse> result = bedService.getBedList(request);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            System.err.println("Error in getBedList: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<PageResponse<BedResponse>>error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID获取床位信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BedResponse>> getBedById(@PathVariable Long id) {
        try {
            BedResponse bed = bedService.getBedById(id);
            return ResponseEntity.ok(ApiResponse.success(bed));
        } catch (Exception e) {
            System.err.println("Error in getBedById: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<BedResponse>error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 根据房间ID获取该房间的所有床位
     */
    @GetMapping("/room/{roomId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<BedResponse>>> getBedsByRoomId(@PathVariable Long roomId) {
        try {
            List<BedResponse> beds = bedService.getBedsByRoomId(roomId);
            return ResponseEntity.ok(ApiResponse.success(beds));
        } catch (Exception e) {
            System.err.println("Error in getBedsByRoomId: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<List<BedResponse>>error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 获取所有空闲床位
     */
    @GetMapping("/available")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<BedResponse>>> getAvailableBeds() {
        try {
            List<BedResponse> availableBeds = bedService.getAvailableBeds();
            return ResponseEntity.ok(ApiResponse.success(availableBeds));
        } catch (Exception e) {
            System.err.println("Error in getAvailableBeds: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<List<BedResponse>>error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 获取指定房间的空闲床位
     */
    @GetMapping("/available/room/{roomId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<BedResponse>>> getAvailableBedsByRoom(@PathVariable Long roomId) {
        try {
            List<BedResponse> availableBeds = bedService.getAvailableBedsByRoom(roomId);
            return ResponseEntity.ok(ApiResponse.success(availableBeds));
        } catch (Exception e) {
            System.err.println("Error in getAvailableBedsByRoom: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<List<BedResponse>>error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 创建新床位
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BedResponse>> createBed(@Valid @RequestBody BedCreateRequest request) {
        try {
            BedResponse bed = bedService.createBed(request);
            return ResponseEntity.ok(ApiResponse.success("床位创建成功", bed));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<BedResponse>error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error in createBed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<BedResponse>error("创建失败: " + e.getMessage()));
        }
    }

    /**
     * 更新床位信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BedResponse>> updateBed(
            @PathVariable Long id,
            @Valid @RequestBody BedUpdateRequest request) {
        try {
            BedResponse bed = bedService.updateBed(id, request);
            return ResponseEntity.ok(ApiResponse.success("床位信息更新成功", bed));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<BedResponse>error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error in updateBed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<BedResponse>error("更新失败: " + e.getMessage()));
        }
    }

    /**
     * 更新床位状态 - 设为空闲
     */
    @PutMapping("/{id}/available")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BedResponse>> setBedAvailable(@PathVariable Long id) {
        try {
            BedResponse bed = bedService.updateBedStatus(id, 0);
            return ResponseEntity.ok(ApiResponse.success("床位已设为空闲", bed));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<BedResponse>error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error in setBedAvailable: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<BedResponse>error("操作失败: " + e.getMessage()));
        }
    }

    /**
     * 更新床位状态 - 设为已入住
     */
    @PutMapping("/{id}/occupied")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BedResponse>> setBedOccupied(@PathVariable Long id) {
        try {
            BedResponse bed = bedService.updateBedStatus(id, 1);
            return ResponseEntity.ok(ApiResponse.success("床位已设为已入住", bed));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<BedResponse>error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error in setBedOccupied: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<BedResponse>error("操作失败: " + e.getMessage()));
        }
    }

    /**
     * 更新床位状态 - 设为维修
     */
    @PutMapping("/{id}/maintenance")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BedResponse>> setBedMaintenance(@PathVariable Long id) {
        try {
            BedResponse bed = bedService.updateBedStatus(id, 2);
            return ResponseEntity.ok(ApiResponse.success("床位已设为维修", bed));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<BedResponse>error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error in setBedMaintenance: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<BedResponse>error("操作失败: " + e.getMessage()));
        }
    }

    /**
     * 删除床位
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> deleteBed(@PathVariable Long id) {
        try {
            bedService.deleteBed(id);
            return ResponseEntity.ok(ApiResponse.success("床位删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<String>error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error in deleteBed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<String>error("删除失败: " + e.getMessage()));
        }
    }
}
