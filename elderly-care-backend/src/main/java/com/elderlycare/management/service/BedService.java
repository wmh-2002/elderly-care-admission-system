package com.elderlycare.management.service;

import com.elderlycare.management.dto.*;
import com.elderlycare.management.entity.Bed;
import com.elderlycare.management.entity.Room;
import com.elderlycare.management.exception.ResourceNotFoundException;
import com.elderlycare.management.repository.BedRepository;
import com.elderlycare.management.repository.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BedService {

    private final BedRepository bedRepository;
    private final RoomRepository roomRepository;

    public BedService(BedRepository bedRepository, RoomRepository roomRepository) {
        this.bedRepository = bedRepository;
        this.roomRepository = roomRepository;
    }

    /**
     * 分页查询床位列表
     */
    public PageResponse<BedResponse> getBedList(BedQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());

        Specification<Bed> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getRoomId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("roomId"), request.getRoomId()));
            }

            if (request.getBedNo() != null && !request.getBedNo().trim().isEmpty()) {
                String searchKeyword = request.getBedNo().trim();
                predicates.add(criteriaBuilder.like(root.get("bedNo"), "%" + searchKeyword + "%"));
            }

            if (request.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }

            if (predicates.isEmpty()) {
                return null;
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Bed> bedPage = bedRepository.findAll(spec, pageable);

        List<BedResponse> bedResponses = bedPage.getContent().stream()
                .map(BedResponse::new)
                .toList();

        return new PageResponse<>(bedResponses, request.getPage(), request.getSize(),
                bedPage.getTotalElements(), bedPage.getTotalPages(), bedPage.isLast());
    }

    /**
     * 根据ID获取床位信息
     */
    public BedResponse getBedById(Long id) {
        Bed bed = bedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("床位不存在，ID: " + id));
        return new BedResponse(bed);
    }

    /**
     * 根据房间ID获取该房间的所有床位
     */
    public List<BedResponse> getBedsByRoomId(Long roomId) {
        // 验证房间是否存在
        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("房间不存在，ID: " + roomId);
        }

        List<Bed> beds = bedRepository.findByRoomId(roomId);
        return beds.stream()
                .map(BedResponse::new)
                .toList();
    }

    /**
     * 创建新床位
     */
    @Transactional
    public BedResponse createBed(BedCreateRequest request) {
        // 验证房间是否存在
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("房间不存在，ID: " + request.getRoomId()));

        // 验证房间是否还有可用床位
        List<Bed> existingBeds = bedRepository.findByRoomId(request.getRoomId());
        if (existingBeds.size() >= room.getMaxBed()) {
            throw new RuntimeException("房间已达到最大床位数，无法添加更多床位");
        }

        // 检查床位号是否已存在于该房间
        if (bedRepository.existsByRoomIdAndBedNo(request.getRoomId(), request.getBedNo())) {
            throw new RuntimeException("该房间中已存在床位号: " + request.getBedNo());
        }

        Bed bed = new Bed();
        bed.setRoomId(request.getRoomId());
        bed.setBedNo(request.getBedNo().trim());
        bed.setStatus(request.getStatus() != null ? request.getStatus() : 0);

        Bed savedBed = bedRepository.save(bed);
        return new BedResponse(savedBed);
    }

    /**
     * 更新床位信息
     */
    @Transactional
    public BedResponse updateBed(Long id, BedUpdateRequest request) {
        Bed bed = bedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("床位不存在，ID: " + id));

        // 只允许更新状态
        if (request.getStatus() != null) {
            bed.setStatus(request.getStatus());
        }

        Bed savedBed = bedRepository.save(bed);
        return new BedResponse(savedBed);
    }

    /**
     * 删除床位
     */
    @Transactional
    public void deleteBed(Long id) {
        Bed bed = bedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("床位不存在，ID: " + id));

        // 检查床位是否正在使用（已入住状态）
        if (bed.getStatus() == 1) {
            throw new RuntimeException("无法删除正在使用的床位");
        }

        bedRepository.deleteById(id);
    }

    /**
     * 更新床位状态
     */
    @Transactional
    public BedResponse updateBedStatus(Long id, Integer status) {
        Bed bed = bedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("床位不存在，ID: " + id));

        if (status < 0 || status > 2) {
            throw new RuntimeException("无效的状态值");
        }

        bed.setStatus(status);
        Bed savedBed = bedRepository.save(bed);
        return new BedResponse(savedBed);
    }

    /**
     * 获取空闲床位列表
     */
    public List<BedResponse> getAvailableBeds() {
        List<Bed> availableBeds = bedRepository.findByStatus(0);
        return availableBeds.stream()
                .map(BedResponse::new)
                .toList();
    }

    /**
     * 获取指定房间的空闲床位
     */
    public List<BedResponse> getAvailableBedsByRoom(Long roomId) {
        // 这里需要自定义查询或过滤，暂时使用简单实现
        List<Bed> allBedsInRoom = bedRepository.findByRoomId(roomId);
        return allBedsInRoom.stream()
                .filter(bed -> bed.getStatus() == 0)
                .map(BedResponse::new)
                .toList();
    }
}
