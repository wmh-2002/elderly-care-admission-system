package com.elderlycare.management.repository;

import com.elderlycare.management.entity.FeeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeItemRepository extends JpaRepository<FeeItem, String>, JpaSpecificationExecutor<FeeItem> {

    Optional<FeeItem> findByItemCode(String itemCode);

    List<FeeItem> findByItemType(String itemType);

    boolean existsByItemCode(String itemCode);
}

