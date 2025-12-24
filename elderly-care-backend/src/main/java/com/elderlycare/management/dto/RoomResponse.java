package com.elderlycare.management.dto;

import com.elderlycare.management.entity.Room;

import java.time.LocalDateTime;

public class RoomResponse {
    private Long id;
    private String roomNo;
    private String roomType;
    private Integer floor;
    private Integer maxBed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RoomResponse() {
    }

    public RoomResponse(Room room) {
        this.id = room.getId();
        this.roomNo = room.getRoomNo();
        this.roomType = room.getRoomType();
        this.floor = room.getFloor();
        this.maxBed = room.getMaxBed();
        this.createdAt = room.getCreatedAt();
        this.updatedAt = room.getUpdatedAt();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Integer getMaxBed() {
        return maxBed;
    }

    public void setMaxBed(Integer maxBed) {
        this.maxBed = maxBed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
