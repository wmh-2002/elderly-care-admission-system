package com.elderlycare.management.repository;

import com.elderlycare.management.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long>, JpaSpecificationExecutor<Bill> {

    List<Bill> findByElderId(Long elderId);

    Optional<Bill> findByElderIdAndBillMonth(Long elderId, String billMonth);

    List<Bill> findByBillMonth(String billMonth);

    List<Bill> findByStatus(Integer status);

    boolean existsByElderIdAndBillMonth(Long elderId, String billMonth);
}

