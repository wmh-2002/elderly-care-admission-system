package com.example.elderlycaresystem.service;

import com.example.elderlycaresystem.entity.Room;
import java.util.List;

/**
 * 房间服务接口
 */
public interface RoomService {
    
    /**
     * 根据ID获取房间信息
     * 
     * @param id 房间ID
     * @return 房间信息
     */
    Room getRoomById(Long id);
    
    /**
     * 获取所有房间列表
     * 
     * @return 房间列表
     */
    List<Room> getAllRooms();
    
    /**
     * 根据条件查询房间列表
     * 
     * @param roomType 房间类型（可选）
     * @param floor 楼层（可选）
     * @return 符合条件的房间列表
     */
    List<Room> getRoomsByCondition(String roomType, Integer floor);
    
    /**
     * 保存房间信息
     * 
     * @param room 房间信息
     * @return 保存后的房间ID
     */
    Long saveRoom(Room room);
    
    /**
     * 更新房间信息
     * 
     * @param room 房间信息
     * @return 更新是否成功
     */
    boolean updateRoom(Room room);
    
    /**
     * 删除房间信息
     * 
     * @param id 房间ID
     * @return 删除是否成功
     */
    boolean deleteRoom(Long id);
    
    /**
     * 获取房间总数
     * 
     * @return 房间总数
     */
    int getRoomCount();
    
    /**
     * 获取空闲房间数量
     * 
     * @return 空闲房间数量
     */
    int getAvailableRoomCount();
    
    /**
     * 获取已用房间数量
     * 
     * @return 已用房间数量
     */
    int getUsedRoomCount();
}