package com.elderlycare.management.dto;

import com.elderlycare.management.entity.Bed;

import java.time.LocalDateTime;

public class BedResponse {
    private Long id;
    private Long roomId;
    private String roomNo;
    private String bedNo;
    private Integer status;
    private String statusText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BedResponse() {
    }

    public BedResponse(Bed bed) {
        this.id = bed.getId();
        this.roomId = bed.getRoomId();
        this.bedNo = bed.getBedNo();
        this.status = bed.getStatus();
        this.statusText = getStatusText(bed.getStatus());
        this.createdAt = bed.getCreatedAt();
        this.updatedAt = bed.getUpdatedAt();

        // 如果有关联的房间信息，设置房间号
        if (bed.getRoom() != null) {
            this.roomNo = bed.getRoom().getRoomNo();
        }
    }

    private String getStatusText(Integer status) {
        return switch (status) {
            case 0 -> "空闲";
            case 1 -> "已入住";
            case 2 -> "维修";
            default -> "未知";
        };
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getBedNo() {
        return bedNo;
    }

    public void setBedNo(String bedNo) {
        this.bedNo = bedNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
        this.statusText = getStatusText(status);
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
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
