package com.elderlycare.management.service;

import com.elderlycare.management.dto.*;
import com.elderlycare.management.entity.Room;
import com.elderlycare.management.exception.ResourceNotFoundException;
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
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    /**
     * 分页查询房间列表
     */
    public PageResponse<RoomResponse> getRoomList(RoomQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());

        Specification<Room> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getRoomNo() != null && !request.getRoomNo().trim().isEmpty()) {
                String searchKeyword = request.getRoomNo().trim();
                predicates.add(criteriaBuilder.like(root.get("roomNo"), "%" + searchKeyword + "%"));
            }

            if (request.getRoomType() != null && !request.getRoomType().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("roomType"), "%" + request.getRoomType().trim() + "%"));
            }

            if (request.getFloor() != null) {
                predicates.add(criteriaBuilder.equal(root.get("floor"), request.getFloor()));
            }

            if (request.getMaxBed() != null) {
                predicates.add(criteriaBuilder.equal(root.get("maxBed"), request.getMaxBed()));
            }

            if (predicates.isEmpty()) {
                return null;
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Room> roomPage = roomRepository.findAll(spec, pageable);

        List<RoomResponse> roomResponses = roomPage.getContent().stream()
                .map(RoomResponse::new)
                .toList();

        return new PageResponse<>(roomResponses, request.getPage(), request.getSize(),
                roomPage.getTotalElements(), roomPage.getTotalPages(), roomPage.isLast());
    }

    /**
     * 根据ID获取房间信息
     */
    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("房间不存在，ID: " + id));
        return new RoomResponse(room);
    }

    /**
     * 创建新房间
     */
    @Transactional
    public RoomResponse createRoom(RoomCreateRequest request) {
        // 检查房间号是否已存在
        if (roomRepository.existsByRoomNo(request.getRoomNo())) {
            throw new RuntimeException("房间号已存在");
        }

        Room room = new Room();
        room.setRoomNo(request.getRoomNo().trim());
        room.setRoomType(request.getRoomType().trim());
        room.setFloor(request.getFloor());
        room.setMaxBed(request.getMaxBed());

        Room savedRoom = roomRepository.save(room);
        return new RoomResponse(savedRoom);
    }

    /**
     * 更新房间信息
     */
    @Transactional
    public RoomResponse updateRoom(Long id, RoomUpdateRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("房间不存在，ID: " + id));

        // 检查房间号是否已存在（排除自身）
        if (request.getRoomNo() != null && !request.getRoomNo().trim().equals(room.getRoomNo())) {
            if (roomRepository.existsByRoomNoAndIdNot(request.getRoomNo(), id)) {
                throw new RuntimeException("房间号已存在");
            }
            room.setRoomNo(request.getRoomNo().trim());
        }

        // 更新其他字段
        if (request.getRoomType() != null && !request.getRoomType().trim().isEmpty()) {
            room.setRoomType(request.getRoomType().trim());
        }

        if (request.getFloor() != null) {
            room.setFloor(request.getFloor());
        }

        if (request.getMaxBed() != null) {
            room.setMaxBed(request.getMaxBed());
        }

        Room savedRoom = roomRepository.save(room);
        return new RoomResponse(savedRoom);
    }

    /**
     * 删除房间
     */
    @Transactional
    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("房间不存在，ID: " + id));

        // 这里可以添加业务逻辑，比如检查房间是否有入住的老人等
        // 暂时直接删除
        roomRepository.deleteById(id);
    }
}
