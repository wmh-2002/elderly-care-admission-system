package com.elderlycare.management.repository;

import com.elderlycare.management.entity.CareRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CareRecordRepository extends JpaRepository<CareRecord, Long>, JpaSpecificationExecutor<CareRecord> {

    List<CareRecord> findByElderId(Long elderId);

    List<CareRecord> findByElderIdOrderByRecordDateDesc(Long elderId);

    List<CareRecord> findByRecordDateBetween(LocalDate startDate, LocalDate endDate);

    List<CareRecord> findByNurseId(Long nurseId);

    Optional<CareRecord> findByElderIdAndRecordDate(Long elderId, LocalDate recordDate);
}

