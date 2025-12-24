package com.elderlycare.management.repository;

import com.elderlycare.management.entity.Bed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BedRepository extends JpaRepository<Bed, Long>, JpaSpecificationExecutor<Bed> {

    List<Bed> findByRoomId(Long roomId);

    List<Bed> findByStatus(Integer status);

    Optional<Bed> findByRoomIdAndBedNo(Long roomId, String bedNo);

    boolean existsByRoomIdAndBedNo(Long roomId, String bedNo);

    boolean existsByRoomIdAndBedNoAndIdNot(Long roomId, String bedNo, Long id);
}

