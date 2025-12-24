package com.elderlycare.management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class RoomUpdateRequest {

    @Size(max = 20, message = "房间号长度不能超过20个字符")
    private String roomNo;

    @Size(max = 20, message = "房间类型长度不能超过20个字符")
    private String roomType;

    @Min(value = 1, message = "楼层必须大于0")
    private Integer floor;

    @Min(value = 1, message = "最大床位数必须大于0")
    private Integer maxBed;

    public RoomUpdateRequest() {
    }

    // Getters and Setters
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
}
