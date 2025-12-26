package com.elderlycare.management.repository;

import com.elderlycare.management.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {

    Optional<Room> findByRoomNo(String roomNo);

    boolean existsByRoomNo(String roomNo);

    boolean existsByRoomNoAndIdNot(String roomNo, Long id);

    // 统计查询方法
    @Query("SELECT r.roomType, COUNT(r) FROM Room r GROUP BY r.roomType")
    List<Object[]> countByRoomType();

    @Query("SELECT r.floor, COUNT(r) FROM Room r GROUP BY r.floor")
    List<Object[]> countByFloor();
}

