package com.elderlycare.management.service;

import com.elderlycare.management.dto.*;
import com.elderlycare.management.entity.Bed;
import com.elderlycare.management.entity.CareLevel;
import com.elderlycare.management.entity.Elder;
import com.elderlycare.management.exception.ResourceNotFoundException;
import com.elderlycare.management.repository.BedRepository;
import com.elderlycare.management.repository.CareLevelRepository;
import com.elderlycare.management.repository.ElderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ElderService {

    private final ElderRepository elderRepository;
    private final BedRepository bedRepository;
    private final CareLevelRepository careLevelRepository;

    public ElderService(ElderRepository elderRepository, BedRepository bedRepository, CareLevelRepository careLevelRepository) {
        this.elderRepository = elderRepository;
        this.bedRepository = bedRepository;
        this.careLevelRepository = careLevelRepository;
    }

    /**
     * 分页查询老人列表
     */
    public PageResponse<ElderResponse> getElderList(ElderQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());

        Specification<Elder> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (notBlank(request.getElderNo())) {
                predicates.add(cb.like(root.get("elderNo"), "%" + request.getElderNo().trim() + "%"));
            }
            if (notBlank(request.getName())) {
                predicates.add(cb.like(root.get("name"), "%" + request.getName().trim() + "%"));
            }
            if (notBlank(request.getGender())) {
                predicates.add(cb.equal(root.get("gender"), request.getGender().trim()));
            }
            if (notBlank(request.getPhone())) {
                predicates.add(cb.like(root.get("phone"), "%" + request.getPhone().trim() + "%"));
            }
            if (notBlank(request.getIdCard())) {
                predicates.add(cb.equal(root.get("idCard"), request.getIdCard().trim()));
            }
            if (request.getBedId() != null) {
                predicates.add(cb.equal(root.get("bedId"), request.getBedId()));
            }
            if (notBlank(request.getCareLevel())) {
                predicates.add(cb.equal(root.get("careLevel"), request.getCareLevel().trim()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }
            if (request.getCheckinDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("checkinDate"), request.getCheckinDateFrom()));
            }
            if (request.getCheckinDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("checkinDate"), request.getCheckinDateTo()));
            }

            if (predicates.isEmpty()) {
                return null;
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Elder> page = elderRepository.findAll(spec, pageable);
        List<ElderResponse> responses = page.getContent().stream()
                .map(ElderResponse::new)
                .toList();

        return new PageResponse<>(responses, request.getPage(), request.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    /**
     * 根据ID获取老人信息
     */
    public ElderResponse getElderById(Long id) {
        Elder elder = elderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("老人不存在，ID: " + id));
        return new ElderResponse(elder);
    }

    /**
     * 创建老人档案
     */
    @Transactional
    public ElderResponse createElder(ElderCreateRequest request) {
        // 编号与身份证唯一性校验
        if (elderRepository.existsByElderNo(request.getElderNo())) {
            throw new RuntimeException("老人编号已存在: " + request.getElderNo());
        }
        if (notBlank(request.getIdCard()) && elderRepository.existsByIdCard(request.getIdCard())) {
            throw new RuntimeException("身份证号已存在: " + request.getIdCard());
        }

        // 护理等级校验
        CareLevel careLevel = careLevelRepository.findById(request.getCareLevel())
                .orElseThrow(() -> new ResourceNotFoundException("护理等级不存在: " + request.getCareLevel()));

        // 床位校验：存在且空闲
        Bed bed = bedRepository.findById(request.getBedId())
                .orElseThrow(() -> new ResourceNotFoundException("床位不存在，ID: " + request.getBedId()));
        if (bed.getStatus() != null && bed.getStatus() != 0) {
            throw new RuntimeException("该床位当前不可用，状态: " + bed.getStatus());
        }
        if (elderRepository.existsByBedId(request.getBedId())) {
            throw new RuntimeException("该床位已被占用");
        }

        Elder elder = new Elder();
        copyCreateRequest(request, elder);
        // 默认费用标准：如果未传，则取护理等级日价格
        elder.setFeeStandard(request.getFeeStandard() != null ? request.getFeeStandard() : careLevel.getDailyPrice());
        elder.setStatus(request.getStatus() != null ? request.getStatus() : 1);

        Elder saved = elderRepository.save(elder);
        // 占用床位
        bed.setStatus(1);
        bedRepository.save(bed);

        return new ElderResponse(saved);
    }

    /**
     * 更新老人档案
     */
    @Transactional
    public ElderResponse updateElder(Long id, ElderUpdateRequest request) {
        Elder elder = elderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("老人不存在，ID: " + id));

        // 身份证唯一性校验
        if (notBlank(request.getIdCard()) && !request.getIdCard().equals(elder.getIdCard())) {
            if (elderRepository.existsByIdCard(request.getIdCard())) {
                throw new RuntimeException("身份证号已存在: " + request.getIdCard());
            }
        }

        // 处理床位变更
        if (request.getBedId() != null && !request.getBedId().equals(elder.getBedId())) {
            // 释放旧床位
            if (elder.getBedId() != null) {
                bedRepository.findById(elder.getBedId()).ifPresent(oldBed -> {
                    oldBed.setStatus(0);
                    bedRepository.save(oldBed);
                });
            }
            Bed newBed = bedRepository.findById(request.getBedId())
                    .orElseThrow(() -> new ResourceNotFoundException("床位不存在，ID: " + request.getBedId()));
            if (newBed.getStatus() != null && newBed.getStatus() != 0) {
                throw new RuntimeException("该床位当前不可用，状态: " + newBed.getStatus());
            }
            if (elderRepository.existsByBedId(request.getBedId())) {
                throw new RuntimeException("该床位已被占用");
            }
            newBed.setStatus(1);
            bedRepository.save(newBed);
            elder.setBedId(request.getBedId());
        }

        // 护理等级变更
        if (notBlank(request.getCareLevel()) && !request.getCareLevel().equals(elder.getCareLevel())) {
            CareLevel careLevel = careLevelRepository.findById(request.getCareLevel())
                    .orElseThrow(() -> new ResourceNotFoundException("护理等级不存在: " + request.getCareLevel()));
            elder.setCareLevel(request.getCareLevel());
            if (request.getFeeStandard() == null) {
                elder.setFeeStandard(careLevel.getDailyPrice());
            }
        }

        // 基本信息更新
        copyUpdateRequest(request, elder);

        // 费用标准
        if (request.getFeeStandard() != null) {
            elder.setFeeStandard(request.getFeeStandard());
        }

        // 状态处理：若退住(status=0)释放床位
        if (request.getStatus() != null) {
            elder.setStatus(request.getStatus());
            if (request.getStatus() == 0 && elder.getBedId() != null) {
                bedRepository.findById(elder.getBedId()).ifPresent(bed -> {
                    bed.setStatus(0);
                    bedRepository.save(bed);
                });
            }
        }

        Elder saved = elderRepository.save(elder);
        return new ElderResponse(saved);
    }

    /**
     * 删除老人档案
     */
    @Transactional
    public void deleteElder(Long id) {
        Elder elder = elderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("老人不存在，ID: " + id));

        // 释放床位
        if (elder.getBedId() != null) {
            bedRepository.findById(elder.getBedId()).ifPresent(bed -> {
                bed.setStatus(0);
                bedRepository.save(bed);
            });
        }

        elderRepository.deleteById(id);
    }

    private boolean notBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }

    private void copyCreateRequest(ElderCreateRequest request, Elder elder) {
        elder.setElderNo(request.getElderNo().trim());
        elder.setName(request.getName().trim());
        elder.setGender(request.getGender());
        elder.setBirthDate(request.getBirthDate());
        elder.setIdCard(request.getIdCard());
        elder.setNation(request.getNation());
        elder.setNativePlace(request.getNativePlace());
        elder.setPhone(request.getPhone());
        elder.setAddress(request.getAddress());
        elder.setBloodType(request.getBloodType());
        elder.setAllergy(request.getAllergy());
        elder.setMedicalHistory(request.getMedicalHistory());
        elder.setHealthStatus(request.getHealthStatus());
        elder.setContactName(request.getContactName());
        elder.setContactPhone(request.getContactPhone());
        elder.setContactRelation(request.getContactRelation());
        elder.setPhoto(request.getPhoto());
        elder.setCheckinDate(request.getCheckinDate() != null ? request.getCheckinDate() : LocalDate.now());
        elder.setBedId(request.getBedId());
        elder.setCareLevel(request.getCareLevel());
        elder.setPayType(request.getPayType());
        elder.setStatus(request.getStatus() != null ? request.getStatus() : 1);
    }

    private void copyUpdateRequest(ElderUpdateRequest request, Elder elder) {
        if (notBlank(request.getName())) elder.setName(request.getName().trim());
        if (notBlank(request.getGender())) elder.setGender(request.getGender().trim());
        if (request.getBirthDate() != null) elder.setBirthDate(request.getBirthDate());
        if (notBlank(request.getIdCard())) elder.setIdCard(request.getIdCard().trim());
        if (notBlank(request.getNation())) elder.setNation(request.getNation().trim());
        if (notBlank(request.getNativePlace())) elder.setNativePlace(request.getNativePlace().trim());
        if (notBlank(request.getPhone())) elder.setPhone(request.getPhone().trim());
        if (notBlank(request.getAddress())) elder.setAddress(request.getAddress().trim());
        if (notBlank(request.getBloodType())) elder.setBloodType(request.getBloodType().trim());
        if (notBlank(request.getAllergy())) elder.setAllergy(request.getAllergy().trim());
        if (notBlank(request.getMedicalHistory())) elder.setMedicalHistory(request.getMedicalHistory().trim());
        if (notBlank(request.getHealthStatus())) elder.setHealthStatus(request.getHealthStatus().trim());
        if (notBlank(request.getContactName())) elder.setContactName(request.getContactName().trim());
        if (notBlank(request.getContactPhone())) elder.setContactPhone(request.getContactPhone().trim());
        if (notBlank(request.getContactRelation())) elder.setContactRelation(request.getContactRelation().trim());
        if (request.getPhoto() != null) elder.setPhoto(request.getPhoto());
        if (request.getCheckinDate() != null) elder.setCheckinDate(request.getCheckinDate());
        if (request.getPayType() != null) elder.setPayType(request.getPayType());
    }
}

