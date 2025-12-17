package com.example.elderlycaresystem.service;

import com.example.elderlycaresystem.entity.Elder;
import java.util.List;

/**
 * 老人档案服务接口
 */
public interface ElderService {
    
    /**
     * 根据ID获取老人信息
     * 
     * @param id 老人ID
     * @return 老人信息
     */
    Elder getElderById(Long id);
    
    /**
     * 根据老人编号获取老人信息
     * 
     * @param elderNo 老人编号
     * @return 老人信息
     */
    Elder getElderByNo(String elderNo);
    
    /**
     * 获取所有老人列表
     * 
     * @return 老人列表
     */
    List<Elder> getAllElders();
    
    /**
     * 根据条件查询老人列表
     * 
     * @param name 姓名（可选）
     * @param status 状态（可选）
     * @param careLevel 护理等级（可选）
     * @return 符合条件的老人列表
     */
    List<Elder> getEldersByCondition(String name, Integer status, String careLevel);
    
    /**
     * 保存老人信息
     * 
     * @param elder 老人信息
     * @return 保存后的老人ID
     */
    Long saveElder(Elder elder);
    
    /**
     * 更新老人信息
     * 
     * @param elder 老人信息
     * @return 更新是否成功
     */
    boolean updateElder(Elder elder);
    
    /**
     * 删除老人信息
     * 
     * @param id 老人ID
     * @return 删除是否成功
     */
    boolean deleteElder(Long id);
    
    /**
     * 获取在院老人数量
     * 
     * @return 在院老人数量
     */
    int getIn院ElderCount();
    
    /**
     * 根据房间ID获取老人列表
     * 
     * @param roomId 房间ID
     * @return 房间内的老人列表
     */
    List<Elder> getEldersByRoomId(Long roomId);
}