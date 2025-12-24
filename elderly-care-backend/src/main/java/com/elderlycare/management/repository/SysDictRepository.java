package com.elderlycare.management.repository;

import com.elderlycare.management.entity.SysDict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SysDictRepository extends JpaRepository<SysDict, Long> {

    List<SysDict> findByDictTypeOrderBySortNoAsc(String dictType);

    Optional<SysDict> findByDictTypeAndDictKey(String dictType, String dictKey);

    boolean existsByDictTypeAndDictKey(String dictType, String dictKey);
}

