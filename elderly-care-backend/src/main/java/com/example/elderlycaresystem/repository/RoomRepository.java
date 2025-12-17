package com.example.elderlycaresystem.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.elderlycaresystem.entity.Room;
import org.apache.ibatis.annotations.Mapper;

/**
 * 房间档案数据访问接口
 */
@Mapper
public interface RoomRepository extends BaseMapper<Room> {
}