package com.elderlycare.management.dto;

import java.time.LocalDate;

public class ElderQueryRequest {

    private String elderNo;
    private String name;
    private String gender;
    private String phone;
    private String idCard;
    private Long bedId;
    private String careLevel;
    private Integer status;
    private LocalDate checkinDateFrom;
    private LocalDate checkinDateTo;
    private Integer page = 1;
    private Integer size = 10;

    public ElderQueryRequest() {
    }

    // Getters and Setters
    public String getElderNo() { return elderNo; }
    public void setElderNo(String elderNo) { this.elderNo = elderNo; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    public Long getBedId() { return bedId; }
    public void setBedId(Long bedId) { this.bedId = bedId; }
    public String getCareLevel() { return careLevel; }
    public void setCareLevel(String careLevel) { this.careLevel = careLevel; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDate getCheckinDateFrom() { return checkinDateFrom; }
    public void setCheckinDateFrom(LocalDate checkinDateFrom) { this.checkinDateFrom = checkinDateFrom; }
    public LocalDate getCheckinDateTo() { return checkinDateTo; }
    public void setCheckinDateTo(LocalDate checkinDateTo) { this.checkinDateTo = checkinDateTo; }
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
}

