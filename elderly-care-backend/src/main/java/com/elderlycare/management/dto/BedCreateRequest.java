package com.elderlycare.management.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BedCreateRequest {

    @NotNull(message = "房间ID不能为空")
    private Long roomId;

    @NotBlank(message = "床位号不能为空")
    private String bedNo;

    @Min(value = 0, message = "状态值不能小于0")
    @Max(value = 2, message = "状态值不能大于2")
    private Integer status = 0; // 默认空闲

    public BedCreateRequest() {
    }

    public BedCreateRequest(Long roomId, String bedNo, Integer status) {
        this.roomId = roomId;
        this.bedNo = bedNo;
        this.status = status;
    }

    // Getters and Setters
    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
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
    }
}
