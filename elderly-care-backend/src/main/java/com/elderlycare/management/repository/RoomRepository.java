package com.elderlycare.management.repository;

import com.elderlycare.management.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {

    Optional<Room> findByRoomNo(String roomNo);

    boolean existsByRoomNo(String roomNo);

    boolean existsByRoomNoAndIdNot(String roomNo, Long id);
}

