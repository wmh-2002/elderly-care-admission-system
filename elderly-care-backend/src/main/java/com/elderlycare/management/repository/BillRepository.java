package com.elderlycare.management.repository;

import com.elderlycare.management.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long>, JpaSpecificationExecutor<Bill> {

    List<Bill> findByElderId(Long elderId);

    Optional<Bill> findByElderIdAndBillMonth(Long elderId, String billMonth);

    List<Bill> findByBillMonth(String billMonth);

    List<Bill> findByStatus(Integer status);

    boolean existsByElderIdAndBillMonth(Long elderId, String billMonth);

    // 统计查询方法
    @Query("SELECT SUM(b.totalAmount) FROM Bill b")
    BigDecimal sumTotalAmount();

    @Query("SELECT SUM(b.totalAmount) FROM Bill b WHERE b.createdAt BETWEEN :startTime AND :endTime")
    BigDecimal sumTotalAmountByCreateTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT SUM(b.totalAmount - b.paidAmount) FROM Bill b WHERE b.status = 0")
    BigDecimal sumUnpaidAmount();

    @Query("SELECT b.paymentMethod, COUNT(b) FROM Bill b GROUP BY b.paymentMethod")
    List<Object[]> countByPaymentMethod();

    @Query("SELECT b FROM Bill b ORDER BY b.createdAt DESC LIMIT 10")
    List<Bill> findTop10ByOrderByCreatedAtDesc();
}

