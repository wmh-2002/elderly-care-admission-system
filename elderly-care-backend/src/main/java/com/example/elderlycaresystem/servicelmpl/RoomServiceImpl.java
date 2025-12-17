package com.example.elderlycaresystem.servicelmpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.elderlycaresystem.entity.Room;
import com.example.elderlycaresystem.exception.BusinessException;
import com.example.elderlycaresystem.repository.RoomRepository;
import com.example.elderlycaresystem.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 房间服务实现类
 */
@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public Room getRoomById(Long id) {
        if (id == null) {
            throw new BusinessException("房间ID不能为空");
        }
        return roomRepository.selectById(id);
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.selectList(null);
    }

    @Override
    public List<Room> getRoomsByCondition(String roomType, Integer floor) {
        QueryWrapper<Room> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(roomType)) {
            wrapper.eq("room_type", roomType);
        }
        if (floor != null) {
            wrapper.eq("floor", floor);
        }
        return roomRepository.selectList(wrapper);
    }

    @Override
    public Long saveRoom(Room room) {
        if (room == null) {
            throw new BusinessException("房间信息不能为空");
        }
        
        // 检查房间编号是否已存在
        QueryWrapper<Room> wrapper = new QueryWrapper<>();
        wrapper.eq("room_no", room.getRoomNo());
        if (room.getId() != null) {
            // 更新操作，排除当前房间
            wrapper.ne("id", room.getId());
        }
        int count = roomRepository.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("房间编号已存在");
        }
        
        if (room.getId() == null) {
            // 新增房间
            roomRepository.insert(room);
            return room.getId();
        } else {
            // 更新房间
            roomRepository.updateById(room);
            return room.getId();
        }
    }

    @Override
    public boolean updateRoom(Room room) {
        if (room == null || room.getId() == null) {
            throw new BusinessException("房间信息不能为空");
        }
        
        // 检查房间编号是否已存在
        QueryWrapper<Room> wrapper = new QueryWrapper<>();
        wrapper.eq("room_no", room.getRoomNo()).ne("id", room.getId());
        int count = roomRepository.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("房间编号已存在");
        }
        
        int result = roomRepository.updateById(room);
        return result > 0;
    }

    @Override
    public boolean deleteRoom(Long id) {
        if (id == null) {
            throw new BusinessException("房间ID不能为空");
        }
        int result = roomRepository.deleteById(id);
        return result > 0;
    }

    @Override
    public int getRoomCount() {
        return roomRepository.selectCount(null).intValue();
    }

    @Override
    public int getAvailableRoomCount() {
        // 这里需要根据实际业务逻辑判断房间是否可用
        // 暂时返回总数，实际实现需要查询床位状态
        return roomRepository.selectCount(null).intValue();
    }

    @Override
    public int getUsedRoomCount() {
        // 这里需要根据实际业务逻辑判断房间是否被使用
        // 暂时返回0，实际实现需要查询床位状态
        return 0;
    }
}