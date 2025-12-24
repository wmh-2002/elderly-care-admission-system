package com.elderlycare.management.repository;

import com.elderlycare.management.entity.Elder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ElderRepository extends JpaRepository<Elder, Long>, JpaSpecificationExecutor<Elder> {

    Optional<Elder> findByElderNo(String elderNo);

    Optional<Elder> findByIdCard(String idCard);

    Optional<Elder> findByBedId(Long bedId);

    boolean existsByElderNo(String elderNo);

    boolean existsByIdCard(String idCard);

    boolean existsByBedId(Long bedId);

    List<Elder> findByStatus(Integer status);

    List<Elder> findByCareLevel(String careLevel);
}

