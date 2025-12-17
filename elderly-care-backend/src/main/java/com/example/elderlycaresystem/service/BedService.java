package com.example.elderlycaresystem.service;

import com.example.elderlycaresystem.entity.Bed;
import java.util.List;

/**
 * 床位服务接口
 */
public interface BedService {
    
    /**
     * 根据ID获取床位信息
     * 
     * @param id 床位ID
     * @return 床位信息
     */
    Bed getBedById(Long id);
    
    /**
     * 获取所有床位列表
     * 
     * @return 床位列表
     */
    List<Bed> getAllBeds();
    
    /**
     * 根据房间ID获取床位列表
     * 
     * @param roomId 房间ID
     * @return 床位列表
     */
    List<Bed> getBedsByRoomId(Long roomId);
    
    /**
     * 根据状态获取床位列表
     * 
     * @param status 状态
     * @return 床位列表
     */
    List<Bed> getBedsByStatus(Integer status);
    
    /**
     * 保存床位信息
     * 
     * @param bed 床位信息
     * @return 保存后的床位ID
     */
    Long saveBed(Bed bed);
    
    /**
     * 更新床位信息
     * 
     * @param bed 床位信息
     * @return 更新是否成功
     */
    boolean updateBed(Bed bed);
    
    /**
     * 删除床位信息
     * 
     * @param id 床位ID
     * @return 删除是否成功
     */
    boolean deleteBed(Long id);
    
    /**
     * 获取空闲床位数量
     * 
     * @return 空闲床位数量
     */
    int getAvailableBedCount();
    
    /**
     * 获取已用床位数量
     * 
     * @return 已用床位数量
     */
    int getUsedBedCount();
    
    /**
     * 获取维修中床位数量
     * 
     * @return 维修中床位数量
     */
    int getUnderRepairBedCount();
    
    /**
     * 根据房间ID和床号获取床位信息
     * 
     * @param roomId 房间ID
     * @param bedNo 床号
     * @return 床位信息
     */
    Bed getBedByRoomIdAndBedNo(Long roomId, String bedNo);
}