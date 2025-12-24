package com.elderlycare.management.repository;

import com.elderlycare.management.entity.CareLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CareLevelRepository extends JpaRepository<CareLevel, String>, JpaSpecificationExecutor<CareLevel> {

    Optional<CareLevel> findByLevelCode(String levelCode);

    Optional<CareLevel> findByLevelName(String levelName);

    boolean existsByLevelCode(String levelCode);
}

