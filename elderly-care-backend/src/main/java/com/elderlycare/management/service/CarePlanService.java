package com.elderlycare.management.service;

import com.elderlycare.management.dto.*;
import com.elderlycare.management.entity.CarePlan;
import com.elderlycare.management.entity.Elder;
import com.elderlycare.management.exception.ResourceNotFoundException;
import com.elderlycare.management.repository.CarePlanRepository;
import com.elderlycare.management.repository.ElderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CarePlanService {

    private final CarePlanRepository carePlanRepository;
    private final ElderRepository elderRepository;

    public CarePlanService(CarePlanRepository carePlanRepository, ElderRepository elderRepository) {
        this.carePlanRepository = carePlanRepository;
        this.elderRepository = elderRepository;
    }

    /**
     * 分页查询护理计划列表
     */
    public PageResponse<CarePlanResponse> getCarePlanList(CarePlanQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());

        Specification<CarePlan> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getElderId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("elderId"), request.getElderId()));
            }

            if (request.getPlanContent() != null && !request.getPlanContent().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("planContent"), "%" + request.getPlanContent().trim() + "%"));
            }

            if (predicates.isEmpty()) {
                return null;
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<CarePlan> carePlanPage = carePlanRepository.findAll(spec, pageable);

        List<CarePlanResponse> carePlanResponses = carePlanPage.getContent().stream()
                .map(this::convertToCarePlanResponse)
                .toList();

        return new PageResponse<>(carePlanResponses, request.getPage(), request.getSize(),
                carePlanPage.getTotalElements(), carePlanPage.getTotalPages(), carePlanPage.isLast());
    }

    /**
     * 根据ID获取护理计划信息
     */
    public CarePlanResponse getCarePlanById(Long id) {
        CarePlan carePlan = carePlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("护理计划不存在，ID: " + id));
        return convertToCarePlanResponse(carePlan);
    }

    /**
     * 创建护理计划
     */
    @Transactional
    public CarePlanResponse createCarePlan(CarePlanCreateRequest request) {
        // 检查老人是否存在
        Elder elder = elderRepository.findById(request.getElderId())
                .orElseThrow(() -> new ResourceNotFoundException("老人不存在，ID: " + request.getElderId()));

        CarePlan carePlan = new CarePlan();
        carePlan.setElderId(request.getElderId());
        carePlan.setPlanContent(request.getPlanContent());

        CarePlan savedCarePlan = carePlanRepository.save(carePlan);
        return convertToCarePlanResponse(savedCarePlan);
    }

    /**
     * 更新护理计划
     */
    @Transactional
    public CarePlanResponse updateCarePlan(Long id, CarePlanUpdateRequest request) {
        CarePlan carePlan = carePlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("护理计划不存在，ID: " + id));

        // 检查老人是否存在
        Elder elder = elderRepository.findById(request.getElderId())
                .orElseThrow(() -> new ResourceNotFoundException("老人不存在，ID: " + request.getElderId()));

        carePlan.setElderId(request.getElderId());
        carePlan.setPlanContent(request.getPlanContent());

        CarePlan savedCarePlan = carePlanRepository.save(carePlan);
        return convertToCarePlanResponse(savedCarePlan);
    }

    /**
     * 删除护理计划
     */
    @Transactional
    public void deleteCarePlan(Long id) {
        if (!carePlanRepository.existsById(id)) {
            throw new ResourceNotFoundException("护理计划不存在，ID: " + id);
        }
        carePlanRepository.deleteById(id);
    }

    /**
     * 根据老人ID获取最新的护理计划
     */
    public CarePlanResponse getLatestCarePlanByElderId(Long elderId) {
        Optional<CarePlan> latestCarePlan = carePlanRepository.findByElderIdOrderByUpdateTimeDesc(elderId)
                .stream()
                .findFirst();

        return latestCarePlan.map(this::convertToCarePlanResponse)
                .orElse(null);
    }

    private CarePlanResponse convertToCarePlanResponse(CarePlan carePlan) {
        CarePlanResponse response = new CarePlanResponse();
        response.setId(carePlan.getId());
        response.setElderId(carePlan.getElderId());
        response.setPlanContent(carePlan.getPlanContent());
        response.setCreateTime(carePlan.getCreateTime());
        response.setUpdateTime(carePlan.getUpdateTime());

        // 获取关联的老人信息
        if (carePlan.getElder() != null) {
            response.setElderName(carePlan.getElder().getName());
        } else {
            // 从数据库获取老人姓名
            Elder elder = elderRepository.findById(carePlan.getElderId()).orElse(null);
            if (elder != null) {
                response.setElderName(elder.getName());
            }
        }

        return response;
    }
}