package com.elderlycare.management.repository;

import com.elderlycare.management.entity.BillDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillDetailRepository extends JpaRepository<BillDetail, Long> {

    List<BillDetail> findByBillId(Long billId);

    void deleteByBillId(Long billId);
}

