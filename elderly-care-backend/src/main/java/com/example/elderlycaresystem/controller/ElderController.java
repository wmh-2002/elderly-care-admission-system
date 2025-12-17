package com.example.elderlycaresystem.controller;

import com.example.elderlycaresystem.dto.ApiResponse;
import com.example.elderlycaresystem.entity.Elder;
import com.example.elderlycaresystem.service.ElderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 老人档案管理控制器
 * 提供老人档案相关的API接口
 */
@RestController
@RequestMapping("/api/elder")
@CrossOrigin(origins = "*")
public class ElderController {

    @Autowired
    private ElderService elderService;

    /**
     * 根据ID获取老人信息
     * 
     * @param id 老人ID
     * @return 老人信息响应
     */
    @GetMapping("/{id}")
    public ApiResponse<Elder> getElderById(@PathVariable Long id) {
        Elder elder = elderService.getElderById(id);
        return ApiResponse.success(elder, "获取老人信息成功");
    }

    /**
     * 根据老人编号获取老人信息
     * 
     * @param elderNo 老人编号
     * @return 老人信息响应
     */
    @GetMapping("/no/{elderNo}")
    public ApiResponse<Elder> getElderByNo(@PathVariable String elderNo) {
        Elder elder = elderService.getElderByNo(elderNo);
        return ApiResponse.success(elder, "获取老人信息成功");
    }

    /**
     * 获取所有老人列表
     * 
     * @return 老人列表响应
     */
    @GetMapping("/list")
    public ApiResponse<List<Elder>> getAllElders() {
        List<Elder> elders = elderService.getAllElders();
        return ApiResponse.success(elders, "获取老人列表成功");
    }

    /**
     * 根据条件查询老人列表
     * 
     * @param name 姓名（可选）
     * @param status 状态（可选）
     * @param careLevel 护理等级（可选）
     * @return 符合条件的老人列表响应
     */
    @GetMapping("/search")
    public ApiResponse<List<Elder>> getEldersByCondition(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String careLevel) {
        List<Elder> elders = elderService.getEldersByCondition(name, status, careLevel);
        return ApiResponse.success(elders, "查询老人列表成功");
    }

    /**
     * 保存老人信息
     * 
     * @param elder 老人信息
     * @return 保存结果响应
     */
    @PostMapping("/save")
    public ApiResponse<Long> saveElder(@RequestBody Elder elder) {
        Long elderId = elderService.saveElder(elder);
        return ApiResponse.success(elderId, "保存老人信息成功");
    }

    /**
     * 更新老人信息
     * 
     * @param elder 老人信息
     * @return 更新结果响应
     */
    @PutMapping("/update")
    public ApiResponse<Boolean> updateElder(@RequestBody Elder elder) {
        boolean result = elderService.updateElder(elder);
        return ApiResponse.success(result, "更新老人信息" + (result ? "成功" : "失败"));
    }

    /**
     * 删除老人信息
     * 
     * @param id 老人ID
     * @return 删除结果响应
     */
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Boolean> deleteElder(@PathVariable Long id) {
        boolean result = elderService.deleteElder(id);
        return ApiResponse.success(result, "删除老人信息" + (result ? "成功" : "失败"));
    }

    /**
     * 获取在院老人数量
     * 
     * @return 在院老人数量响应
     */
    @GetMapping("/in院-count")
    public ApiResponse<Integer> getIn院ElderCount() {
        int count = elderService.getIn院ElderCount();
        return ApiResponse.success(count, "获取在院老人数量成功");
    }

    /**
     * 根据房间ID获取老人列表
     * 
     * @param roomId 房间ID
     * @return 房间内的老人列表响应
     */
    @GetMapping("/by-room/{roomId}")
    public ApiResponse<List<Elder>> getEldersByRoomId(@PathVariable Long roomId) {
        List<Elder> elders = elderService.getEldersByRoomId(roomId);
        return ApiResponse.success(elders, "获取房间内老人列表成功");
    }
}