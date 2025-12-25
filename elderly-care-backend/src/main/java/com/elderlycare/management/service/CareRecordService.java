package com.elderlycare.management.service;

import com.elderlycare.management.dto.*;
import com.elderlycare.management.entity.CareRecord;
import com.elderlycare.management.entity.Elder;
import com.elderlycare.management.entity.SysUser;
import com.elderlycare.management.exception.ResourceNotFoundException;
import com.elderlycare.management.repository.CareRecordRepository;
import com.elderlycare.management.repository.ElderRepository;
import com.elderlycare.management.repository.SysUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CareRecordService {

    private final CareRecordRepository careRecordRepository;
    private final ElderRepository elderRepository;
    private final SysUserRepository sysUserRepository;

    public CareRecordService(CareRecordRepository careRecordRepository, 
                           ElderRepository elderRepository,
                           SysUserRepository sysUserRepository) {
        this.careRecordRepository = careRecordRepository;
        this.elderRepository = elderRepository;
        this.sysUserRepository = sysUserRepository;
    }

    /**
     * 分页查询护理记录列表
     */
    public PageResponse<CareRecordResponse> getCareRecordList(CareRecordQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());

        Specification<CareRecord> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getElderId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("elderId"), request.getElderId()));
            }

            if (request.getNurseId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("nurseId"), request.getNurseId()));
            }

            if (request.getRecordDate() != null) {
                predicates.add(criteriaBuilder.equal(root.get("recordDate"), request.getRecordDate()));
            }

            if (request.getStartDate() != null && request.getEndDate() != null) {
                predicates.add(criteriaBuilder.between(root.get("recordDate"), 
                        request.getStartDate(), request.getEndDate()));
            } else if (request.getStartDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("recordDate"), 
                        request.getStartDate()));
            } else if (request.getEndDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("recordDate"), 
                        request.getEndDate()));
            }

            if (predicates.isEmpty()) {
                return null;
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<CareRecord> careRecordPage = careRecordRepository.findAll(spec, pageable);

        List<CareRecordResponse> careRecordResponses = careRecordPage.getContent().stream()
                .map(this::convertToCareRecordResponse)
                .toList();

        return new PageResponse<>(careRecordResponses, request.getPage(), request.getSize(),
                careRecordPage.getTotalElements(), careRecordPage.getTotalPages(), careRecordPage.isLast());
    }

    /**
     * 根据ID获取护理记录信息
     */
    public CareRecordResponse getCareRecordById(Long id) {
        CareRecord careRecord = careRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("护理记录不存在，ID: " + id));
        return convertToCareRecordResponse(careRecord);
    }

    /**
     * 创建护理记录
     */
    @Transactional
    public CareRecordResponse createCareRecord(CareRecordCreateRequest request) {
        // 检查老人是否存在
        Elder elder = elderRepository.findById(request.getElderId())
                .orElseThrow(() -> new ResourceNotFoundException("老人不存在，ID: " + request.getElderId()));

        // 检查护理员是否存在
        SysUser nurse = sysUserRepository.findById(request.getNurseId())
                .orElseThrow(() -> new ResourceNotFoundException("护理员不存在，ID: " + request.getNurseId()));

        // 检查当天是否已经有记录
        Optional<CareRecord> existingRecord = careRecordRepository
                .findByElderIdAndRecordDate(request.getElderId(), request.getRecordDate());
        if (existingRecord.isPresent()) {
            throw new RuntimeException("该老人在 " + request.getRecordDate() + " 已有护理记录");
        }

        CareRecord careRecord = new CareRecord();
        careRecord.setElderId(request.getElderId());
        careRecord.setRecordDate(request.getRecordDate());
        careRecord.setTemperature(request.getTemperature());
        careRecord.setPulse(request.getPulse());
        careRecord.setBreath(request.getBreath());
        careRecord.setBloodPressure(request.getBloodPressure());
        careRecord.setDiet(request.getDiet());
        careRecord.setExcrete(request.getExcrete());
        careRecord.setSleep(request.getSleep());
        careRecord.setMedicine(request.getMedicine());
        careRecord.setSpecial(request.getSpecial());
        careRecord.setNurseId(request.getNurseId());

        CareRecord savedCareRecord = careRecordRepository.save(careRecord);
        return convertToCareRecordResponse(savedCareRecord);
    }

    /**
     * 更新护理记录
     */
    @Transactional
    public CareRecordResponse updateCareRecord(Long id, CareRecordUpdateRequest request) {
        CareRecord careRecord = careRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("护理记录不存在，ID: " + id));

        // 检查老人是否存在
        Elder elder = elderRepository.findById(request.getElderId())
                .orElseThrow(() -> new ResourceNotFoundException("老人不存在，ID: " + request.getElderId()));

        // 检查护理员是否存在
        SysUser nurse = sysUserRepository.findById(request.getNurseId())
                .orElseThrow(() -> new ResourceNotFoundException("护理员不存在，ID: " + request.getNurseId()));

        // 检查更新日期是否与其他记录冲突
        List<CareRecord> existingRecords = careRecordRepository.findByElderId(request.getElderId());
        for (CareRecord existing : existingRecords) {
            if (!existing.getId().equals(id) && existing.getRecordDate().equals(request.getRecordDate())) {
                throw new RuntimeException("该老人在 " + request.getRecordDate() + " 已有护理记录");
            }
        }

        careRecord.setElderId(request.getElderId());
        careRecord.setRecordDate(request.getRecordDate());
        careRecord.setTemperature(request.getTemperature());
        careRecord.setPulse(request.getPulse());
        careRecord.setBreath(request.getBreath());
        careRecord.setBloodPressure(request.getBloodPressure());
        careRecord.setDiet(request.getDiet());
        careRecord.setExcrete(request.getExcrete());
        careRecord.setSleep(request.getSleep());
        careRecord.setMedicine(request.getMedicine());
        careRecord.setSpecial(request.getSpecial());
        careRecord.setNurseId(request.getNurseId());

        CareRecord savedCareRecord = careRecordRepository.save(careRecord);
        return convertToCareRecordResponse(savedCareRecord);
    }

    /**
     * 删除护理记录
     */
    @Transactional
    public void deleteCareRecord(Long id) {
        if (!careRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("护理记录不存在，ID: " + id);
        }
        careRecordRepository.deleteById(id);
    }

    /**
     * 根据老人ID获取护理记录列表
     */
    public List<CareRecordResponse> getCareRecordsByElderId(Long elderId) {
        List<CareRecord> careRecords = careRecordRepository.findByElderIdOrderByRecordDateDesc(elderId);
        return careRecords.stream()
                .map(this::convertToCareRecordResponse)
                .toList();
    }

    private CareRecordResponse convertToCareRecordResponse(CareRecord careRecord) {
        CareRecordResponse response = new CareRecordResponse();
        response.setId(careRecord.getId());
        response.setElderId(careRecord.getElderId());
        response.setRecordDate(careRecord.getRecordDate());
        response.setTemperature(careRecord.getTemperature());
        response.setPulse(careRecord.getPulse());
        response.setBreath(careRecord.getBreath());
        response.setBloodPressure(careRecord.getBloodPressure());
        response.setDiet(careRecord.getDiet());
        response.setExcrete(careRecord.getExcrete());
        response.setSleep(careRecord.getSleep());
        response.setMedicine(careRecord.getMedicine());
        response.setSpecial(careRecord.getSpecial());
        response.setNurseId(careRecord.getNurseId());
        response.setCreatedAt(careRecord.getCreatedAt());
        response.setUpdatedAt(careRecord.getUpdatedAt());

        // 获取关联的老人信息
        if (careRecord.getElder() != null) {
            response.setElderName(careRecord.getElder().getName());
        } else {
            // 从数据库获取老人姓名
            Elder elder = elderRepository.findById(careRecord.getElderId()).orElse(null);
            if (elder != null) {
                response.setElderName(elder.getName());
            }
        }

        // 获取关联的护理员信息
        if (careRecord.getNurse() != null) {
            response.setNurseName(careRecord.getNurse().getRealName());
        } else {
            // 从数据库获取护理员姓名
            SysUser nurse = sysUserRepository.findById(careRecord.getNurseId()).orElse(null);
            if (nurse != null) {
                response.setNurseName(nurse.getRealName());
            }
        }

        return response;
    }
}