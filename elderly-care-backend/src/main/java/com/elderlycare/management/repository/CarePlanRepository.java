package com.elderlycare.management.repository;

import com.elderlycare.management.entity.CarePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarePlanRepository extends JpaRepository<CarePlan, Long> {

    Optional<CarePlan> findByElderId(Long elderId);

    List<CarePlan> findByElderIdOrderByUpdateTimeDesc(Long elderId);
}

