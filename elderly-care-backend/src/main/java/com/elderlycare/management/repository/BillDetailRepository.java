package com.elderlycare.management.repository;

import com.elderlycare.management.entity.BillDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillDetailRepository extends JpaRepository<BillDetail, Long> {

    List<BillDetail> findByBillId(Long billId);

    void deleteByBillId(Long billId);

    // 统计查询方法
    @Query("SELECT bd.itemCode, SUM(bd.amount) FROM BillDetail bd GROUP BY bd.itemCode")
    List<Object[]> sumAmountByItemCode();
}

