package com.elderlycare.management.service;

import com.elderlycare.management.dto.*;
import com.elderlycare.management.entity.CarePlan;
import com.elderlycare.management.entity.CareLevel;
import com.elderlycare.management.entity.Elder;
import com.elderlycare.management.entity.SysUser;
import com.elderlycare.management.exception.ResourceNotFoundException;
import com.elderlycare.management.repository.CarePlanRepository;
import com.elderlycare.management.repository.CareLevelRepository;
import com.elderlycare.management.repository.ElderRepository;
import com.elderlycare.management.repository.SysUserRepository;
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
    private final CareLevelRepository careLevelRepository;
    private final SysUserRepository sysUserRepository;

    public CarePlanService(CarePlanRepository carePlanRepository, ElderRepository elderRepository, CareLevelRepository careLevelRepository, SysUserRepository sysUserRepository) {
        this.carePlanRepository = carePlanRepository;
        this.elderRepository = elderRepository;
        this.careLevelRepository = careLevelRepository;
        this.sysUserRepository = sysUserRepository;
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

            if (request.getCareLevelId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("careLevelId"), request.getCareLevelId()));
            }

            if (request.getPlanContent() != null && !request.getPlanContent().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("planContent"), "%" + request.getPlanContent().trim() + "%"));
            }

            return predicates.isEmpty() ? null : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
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
                .orElseThrow(() -> new ResourceNotFoundException("护理计划不存在，ID: " + String.valueOf(id)));
        return convertToCarePlanResponse(carePlan);
    }

    /**
     * 创建护理计划
     */
    @Transactional
    public CarePlanResponse createCarePlan(CarePlanCreateRequest request) {
        // 检查老人是否存在
        Elder elder = elderRepository.findById(request.getElderId())
                .orElseThrow(() -> new ResourceNotFoundException("老人不存在，ID: " + String.valueOf(request.getElderId())));

        // 检查护理等级是否存在（如果提供了careLevelId）
        CareLevel careLevel = null;
        if (request.getCareLevelId() != null) {
            careLevel = careLevelRepository.findById(request.getCareLevelId())
                    .orElseThrow(() -> new ResourceNotFoundException("护理等级不存在，ID: " + request.getCareLevelId()));
        }

        // 检查分配的护理员是否存在（如果提供了assignedNurseId）
        if (request.getAssignedNurseId() != null) {
            // Check if the user exists
            if (!sysUserRepository.existsById(request.getAssignedNurseId())) {
                throw new ResourceNotFoundException("分配的护理员不存在，ID: " + request.getAssignedNurseId());
            }
        }

        CarePlan carePlan = new CarePlan();
        carePlan.setElderId(request.getElderId());
        carePlan.setCareLevelId(request.getCareLevelId()); // 设置护理等级ID
        carePlan.setStatus(request.getStatus()); // 设置状态
        carePlan.setStartDate(request.getStartDate()); // 设置开始日期
        carePlan.setEndDate(request.getEndDate()); // 设置结束日期
        carePlan.setAssignedNurseId(request.getAssignedNurseId()); // 设置分配的护理员ID
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
                .orElseThrow(() -> new ResourceNotFoundException("护理计划不存在，ID: " + String.valueOf(id)));

        // 检查老人是否存在
        Elder elder = elderRepository.findById(request.getElderId())
                .orElseThrow(() -> new ResourceNotFoundException("老人不存在，ID: " + String.valueOf(request.getElderId())));

        // 检查护理等级是否存在（如果提供了careLevelId）
        if (request.getCareLevelId() != null) {
            careLevelRepository.findById(request.getCareLevelId())
                    .orElseThrow(() -> new ResourceNotFoundException("护理等级不存在，ID: " + request.getCareLevelId()));
        }

        // 检查分配的护理员是否存在（如果提供了assignedNurseId）
        if (request.getAssignedNurseId() != null) {
            if (!sysUserRepository.existsById(request.getAssignedNurseId())) {
                throw new ResourceNotFoundException("分配的护理员不存在，ID: " + request.getAssignedNurseId());
            }
        }

        carePlan.setElderId(request.getElderId());
        carePlan.setCareLevelId(request.getCareLevelId()); // 更新护理等级ID
        carePlan.setStatus(request.getStatus()); // 更新状态
        carePlan.setStartDate(request.getStartDate()); // 更新开始日期
        carePlan.setEndDate(request.getEndDate()); // 更新结束日期
        carePlan.setAssignedNurseId(request.getAssignedNurseId()); // 更新分配的护理员ID
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
            throw new ResourceNotFoundException("护理计划不存在，ID: " + String.valueOf(id));
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
        response.setCareLevelId(carePlan.getCareLevelId()); // 设置护理等级ID
        response.setStatus(carePlan.getStatus()); // 设置状态
        response.setStartDate(carePlan.getStartDate()); // 设置开始日期
        response.setEndDate(carePlan.getEndDate()); // 设置结束日期
        response.setAssignedNurseId(carePlan.getAssignedNurseId()); // 设置分配的护理员ID
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

        // 获取关联的护理等级信息
        if (carePlan.getCareLevel() != null) {
            response.setCareLevelName(carePlan.getCareLevel().getLevelName());
        } else if (carePlan.getCareLevelId() != null) {
            // 从数据库获取护理等级名称
            CareLevel careLevel = careLevelRepository.findById(carePlan.getCareLevelId()).orElse(null);
            if (careLevel != null) {
                response.setCareLevelName(careLevel.getLevelName());
            }
        }

        // 获取关联的护理员信息
        if (carePlan.getAssignedNurse() != null) {
            response.setAssignedNurseName(carePlan.getAssignedNurse().getRealName());
        } else if (carePlan.getAssignedNurseId() != null) {
            // 从数据库获取护理员姓名
            SysUser nurse = sysUserRepository.findById(carePlan.getAssignedNurseId()).orElse(null);
            if (nurse != null) {
                response.setAssignedNurseName(nurse.getRealName());
            }
        }

        return response;
    }
}