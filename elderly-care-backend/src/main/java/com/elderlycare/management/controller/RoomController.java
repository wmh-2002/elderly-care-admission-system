package com.elderlycare.management.controller;

import com.elderlycare.management.dto.*;
import com.elderlycare.management.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * 获取房间列表（分页）
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<RoomResponse>>> getRoomList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String roomNo,
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) Integer maxBed) {

        RoomQueryRequest request = new RoomQueryRequest();
        request.setPage(page);
        request.setSize(size);
        request.setRoomNo(roomNo);
        request.setRoomType(roomType);
        request.setFloor(floor);
        request.setMaxBed(maxBed);

        try {
            PageResponse<RoomResponse> result = roomService.getRoomList(request);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            System.err.println("Error in getRoomList: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<PageResponse<RoomResponse>>error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID获取房间信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomById(@PathVariable Long id) {
        try {
            RoomResponse room = roomService.getRoomById(id);
            return ResponseEntity.ok(ApiResponse.success(room));
        } catch (Exception e) {
            System.err.println("Error in getRoomById: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<RoomResponse>error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 创建新房间
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(@Valid @RequestBody RoomCreateRequest request) {
        try {
            RoomResponse room = roomService.createRoom(request);
            return ResponseEntity.ok(ApiResponse.success("房间创建成功", room));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<RoomResponse>error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error in createRoom: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<RoomResponse>error("创建失败: " + e.getMessage()));
        }
    }

    /**
     * 更新房间信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomUpdateRequest request) {
        try {
            RoomResponse room = roomService.updateRoom(id, request);
            return ResponseEntity.ok(ApiResponse.success("房间信息更新成功", room));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<RoomResponse>error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error in updateRoom: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<RoomResponse>error("更新失败: " + e.getMessage()));
        }
    }

    /**
     * 删除房间
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> deleteRoom(@PathVariable Long id) {
        try {
            roomService.deleteRoom(id);
            return ResponseEntity.ok(ApiResponse.success("房间删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<String>error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error in deleteRoom: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<String>error("删除失败: " + e.getMessage()));
        }
    }
}
