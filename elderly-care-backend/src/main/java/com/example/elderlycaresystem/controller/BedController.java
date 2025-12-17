package com.example.elderlycaresystem.controller;

import com.example.elderlycaresystem.dto.ApiResponse;
import com.example.elderlycaresystem.entity.Bed;
import com.example.elderlycaresystem.service.BedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 床位管理控制器
 * 提供床位相关的API接口
 */
@RestController
@RequestMapping("/api/bed")
@CrossOrigin(origins = "*")
public class BedController {

    @Autowired
    private BedService bedService;

    /**
     * 根据ID获取床位信息
     * 
     * @param id 床位ID
     * @return 床位信息响应
     */
    @GetMapping("/{id}")
    public ApiResponse<Bed> getBedById(@PathVariable Long id) {
        Bed bed = bedService.getBedById(id);
        return ApiResponse.success(bed, "获取床位信息成功");
    }

    /**
     * 获取所有床位列表
     * 
     * @return 床位列表响应
     */
    @GetMapping("/list")
    public ApiResponse<List<Bed>> getAllBeds() {
        List<Bed> beds = bedService.getAllBeds();
        return ApiResponse.success(beds, "获取床位列表成功");
    }

    /**
     * 根据房间ID获取床位列表
     * 
     * @param roomId 房间ID
     * @return 床位列表响应
     */
    @GetMapping("/room/{roomId}")
    public ApiResponse<List<Bed>> getBedsByRoomId(@PathVariable Long roomId) {
        List<Bed> beds = bedService.getBedsByRoomId(roomId);
        return ApiResponse.success(beds, "获取房间床位列表成功");
    }

    /**
     * 根据状态获取床位列表
     * 
     * @param status 状态
     * @return 床位列表响应
     */
    @GetMapping("/status/{status}")
    public ApiResponse<List<Bed>> getBedsByStatus(@PathVariable Integer status) {
        List<Bed> beds = bedService.getBedsByStatus(status);
        return ApiResponse.success(beds, "获取状态床位列表成功");
    }

    /**
     * 保存床位信息
     * 
     * @param bed 床位信息
     * @return 保存结果响应
     */
    @PostMapping("/save")
    public ApiResponse<Long> saveBed(@RequestBody Bed bed) {
        Long bedId = bedService.saveBed(bed);
        return ApiResponse.success(bedId, "保存床位信息成功");
    }

    /**
     * 更新床位信息
     * 
     * @param bed 床位信息
     * @return 更新结果响应
     */
    @PutMapping("/update")
    public ApiResponse<Boolean> updateBed(@RequestBody Bed bed) {
        boolean result = bedService.updateBed(bed);
        return ApiResponse.success(result, "更新床位信息" + (result ? "成功" : "失败"));
    }

    /**
     * 删除床位信息
     * 
     * @param id 床位ID
     * @return 删除结果响应
     */
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Boolean> deleteBed(@PathVariable Long id) {
        boolean result = bedService.deleteBed(id);
        return ApiResponse.success(result, "删除床位信息" + (result ? "成功" : "失败"));
    }

    /**
     * 获取空闲床位数量
     * 
     * @return 空闲床位数量响应
     */
    @GetMapping("/available-count")
    public ApiResponse<Integer> getAvailableBedCount() {
        int count = bedService.getAvailableBedCount();
        return ApiResponse.success(count, "获取空闲床位数量成功");
    }

    /**
     * 获取已用床位数量
     * 
     * @return 已用床位数量响应
     */
    @GetMapping("/used-count")
    public ApiResponse<Integer> getUsedBedCount() {
        int count = bedService.getUsedBedCount();
        return ApiResponse.success(count, "获取已用床位数量成功");
    }

    /**
     * 获取维修中床位数量
     * 
     * @return 维修中床位数量响应
     */
    @GetMapping("/repair-count")
    public ApiResponse<Integer> getUnderRepairBedCount() {
        int count = bedService.getUnderRepairBedCount();
        return ApiResponse.success(count, "获取维修中床位数量成功");
    }

    /**
     * 根据房间ID和床号获取床位信息
     * 
     * @param roomId 房间ID
     * @param bedNo 床号
     * @return 床位信息响应
     */
    @GetMapping("/by-room-bed/{roomId}/{bedNo}")
    public ApiResponse<Bed> getBedByRoomIdAndBedNo(@PathVariable Long roomId, @PathVariable String bedNo) {
        Bed bed = bedService.getBedByRoomIdAndBedNo(roomId, bedNo);
        return ApiResponse.success(bed, "获取床位信息成功");
    }
}