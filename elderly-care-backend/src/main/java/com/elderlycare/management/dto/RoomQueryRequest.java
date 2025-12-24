package com.elderlycare.management.dto;

public class RoomQueryRequest {

    private String roomNo;
    private String roomType;
    private Integer floor;
    private Integer maxBed;
    private Integer page = 1;
    private Integer size = 10;

    public RoomQueryRequest() {
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

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
