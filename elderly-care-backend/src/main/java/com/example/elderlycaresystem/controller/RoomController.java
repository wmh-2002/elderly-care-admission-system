package com.example.elderlycaresystem.controller;

import com.example.elderlycaresystem.dto.ApiResponse;
import com.example.elderlycaresystem.entity.Room;
import com.example.elderlycaresystem.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房间管理控制器
 * 提供房间相关的API接口
 */
@RestController
@RequestMapping("/api/room")
@CrossOrigin(origins = "*")
public class RoomController {

    @Autowired
    private RoomService roomService;

    /**
     * 根据ID获取房间信息
     * 
     * @param id 房间ID
     * @return 房间信息响应
     */
    @GetMapping("/{id}")
    public ApiResponse<Room> getRoomById(@PathVariable Long id) {
        Room room = roomService.getRoomById(id);
        return ApiResponse.success(room, "获取房间信息成功");
    }

    /**
     * 获取所有房间列表
     * 
     * @return 房间列表响应
     */
    @GetMapping("/list")
    public ApiResponse<List<Room>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        return ApiResponse.success(rooms, "获取房间列表成功");
    }

    /**
     * 根据条件查询房间列表
     * 
     * @param roomType 房间类型（可选）
     * @param floor 楼层（可选）
     * @return 符合条件的房间列表响应
     */
    @GetMapping("/search")
    public ApiResponse<List<Room>> getRoomsByCondition(
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) Integer floor) {
        List<Room> rooms = roomService.getRoomsByCondition(roomType, floor);
        return ApiResponse.success(rooms, "查询房间列表成功");
    }

    /**
     * 保存房间信息
     * 
     * @param room 房间信息
     * @return 保存结果响应
     */
    @PostMapping("/save")
    public ApiResponse<Long> saveRoom(@RequestBody Room room) {
        Long roomId = roomService.saveRoom(room);
        return ApiResponse.success(roomId, "保存房间信息成功");
    }

    /**
     * 更新房间信息
     * 
     * @param room 房间信息
     * @return 更新结果响应
     */
    @PutMapping("/update")
    public ApiResponse<Boolean> updateRoom(@RequestBody Room room) {
        boolean result = roomService.updateRoom(room);
        return ApiResponse.success(result, "更新房间信息" + (result ? "成功" : "失败"));
    }

    /**
     * 删除房间信息
     * 
     * @param id 房间ID
     * @return 删除结果响应
     */
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Boolean> deleteRoom(@PathVariable Long id) {
        boolean result = roomService.deleteRoom(id);
        return ApiResponse.success(result, "删除房间信息" + (result ? "成功" : "失败"));
    }

    /**
     * 获取房间总数
     * 
     * @return 房间总数响应
     */
    @GetMapping("/count")
    public ApiResponse<Integer> getRoomCount() {
        int count = roomService.getRoomCount();
        return ApiResponse.success(count, "获取房间总数成功");
    }

    /**
     * 获取空闲房间数量
     * 
     * @return 空闲房间数量响应
     */
    @GetMapping("/available-count")
    public ApiResponse<Integer> getAvailableRoomCount() {
        int count = roomService.getAvailableRoomCount();
        return ApiResponse.success(count, "获取空闲房间数量成功");
    }

    /**
     * 获取已用房间数量
     * 
     * @return 已用房间数量响应
     */
    @GetMapping("/used-count")
    public ApiResponse<Integer> getUsedRoomCount() {
        int count = roomService.getUsedRoomCount();
        return ApiResponse.success(count, "获取已用房间数量成功");
    }
}