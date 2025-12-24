package com.elderlycare.management.service;

import com.elderlycare.management.dto.*;
import com.elderlycare.management.entity.CareLevel;
import com.elderlycare.management.exception.ResourceNotFoundException;
import com.elderlycare.management.repository.CareLevelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CareLevelService {

    private final CareLevelRepository careLevelRepository;

    public CareLevelService(CareLevelRepository careLevelRepository) {
        this.careLevelRepository = careLevelRepository;
    }

    /**
     * 分页查询护理等级列表
     */
    public PageResponse<CareLevelResponse> getCareLevelList(CareLevelQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());

        Specification<CareLevel> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getLevelCode() != null && !request.getLevelCode().trim().isEmpty()) {
                String searchKeyword = request.getLevelCode().trim();
                predicates.add(criteriaBuilder.like(root.get("levelCode"), "%" + searchKeyword + "%"));
            }

            if (request.getLevelName() != null && !request.getLevelName().trim().isEmpty()) {
                String searchKeyword = request.getLevelName().trim();
                predicates.add(criteriaBuilder.like(root.get("levelName"), "%" + searchKeyword + "%"));
            }

            if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("description"), "%" + request.getDescription().trim() + "%"));
            }

            if (request.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dailyPrice"), request.getMinPrice()));
            }

            if (request.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dailyPrice"), request.getMaxPrice()));
            }

            if (predicates.isEmpty()) {
                return null;
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<CareLevel> careLevelPage = careLevelRepository.findAll(spec, pageable);

        List<CareLevelResponse> careLevelResponses = careLevelPage.getContent().stream()
                .map(CareLevelResponse::new)
                .toList();

        return new PageResponse<>(careLevelResponses, request.getPage(), request.getSize(),
                careLevelPage.getTotalElements(), careLevelPage.getTotalPages(), careLevelPage.isLast());
    }

    /**
     * 根据编码获取护理等级信息
     */
    public CareLevelResponse getCareLevelByCode(String levelCode) {
        CareLevel careLevel = careLevelRepository.findById(levelCode)
                .orElseThrow(() -> new ResourceNotFoundException("护理等级不存在，编码: " + levelCode));
        return new CareLevelResponse(careLevel);
    }

    /**
     * 创建新护理等级
     */
    @Transactional
    public CareLevelResponse createCareLevel(CareLevelCreateRequest request) {
        // 检查等级编码是否已存在
        if (careLevelRepository.existsByLevelCode(request.getLevelCode())) {
            throw new RuntimeException("等级编码已存在: " + request.getLevelCode());
        }

        // 检查等级名称是否已存在
        if (careLevelRepository.findByLevelName(request.getLevelName()).isPresent()) {
            throw new RuntimeException("等级名称已存在: " + request.getLevelName());
        }

        CareLevel careLevel = new CareLevel();
        careLevel.setLevelCode(request.getLevelCode().trim());
        careLevel.setLevelName(request.getLevelName().trim());
        careLevel.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        careLevel.setDailyPrice(request.getDailyPrice());

        CareLevel savedCareLevel = careLevelRepository.save(careLevel);
        return new CareLevelResponse(savedCareLevel);
    }

    /**
     * 更新护理等级信息
     */
    @Transactional
    public CareLevelResponse updateCareLevel(String levelCode, CareLevelUpdateRequest request) {
        CareLevel careLevel = careLevelRepository.findById(levelCode)
                .orElseThrow(() -> new ResourceNotFoundException("护理等级不存在，编码: " + levelCode));

        // 检查等级名称是否已存在（排除自身）
        if (request.getLevelName() != null && !request.getLevelName().trim().equals(careLevel.getLevelName())) {
            if (careLevelRepository.findByLevelName(request.getLevelName()).isPresent()) {
                throw new RuntimeException("等级名称已存在: " + request.getLevelName());
            }
            careLevel.setLevelName(request.getLevelName().trim());
        }

        // 更新其他字段
        if (request.getDescription() != null) {
            careLevel.setDescription(request.getDescription().trim());
        }

        if (request.getDailyPrice() != null) {
            careLevel.setDailyPrice(request.getDailyPrice());
        }

        CareLevel savedCareLevel = careLevelRepository.save(careLevel);
        return new CareLevelResponse(savedCareLevel);
    }

    /**
     * 删除护理等级
     */
    @Transactional
    public void deleteCareLevel(String levelCode) {
        CareLevel careLevel = careLevelRepository.findById(levelCode)
                .orElseThrow(() -> new ResourceNotFoundException("护理等级不存在，编码: " + levelCode));

        // 这里可以添加业务逻辑，比如检查是否有老人正在使用此等级
        // 暂时直接删除
        careLevelRepository.deleteById(levelCode);
    }

    /**
     * 获取所有护理等级（不分页）
     */
    public List<CareLevelResponse> getAllCareLevels() {
        List<CareLevel> careLevels = careLevelRepository.findAll();
        return careLevels.stream()
                .map(CareLevelResponse::new)
                .toList();
    }

    /**
     * 根据价格范围获取护理等级
     */
    public List<CareLevelResponse> getCareLevelsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        // 这里需要自定义查询或过滤，暂时使用简单实现
        List<CareLevel> allCareLevels = careLevelRepository.findAll();
        return allCareLevels.stream()
                .filter(level -> {
                    BigDecimal price = level.getDailyPrice();
                    boolean meetsMin = minPrice == null || price.compareTo(minPrice) >= 0;
                    boolean meetsMax = maxPrice == null || price.compareTo(maxPrice) <= 0;
                    return meetsMin && meetsMax;
                })
                .map(CareLevelResponse::new)
                .toList();
    }
}
