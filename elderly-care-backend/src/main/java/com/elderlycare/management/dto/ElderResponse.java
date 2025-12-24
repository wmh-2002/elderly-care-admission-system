package com.elderlycare.management.dto;

import com.elderlycare.management.entity.Elder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ElderResponse {
    private Long id;
    private String elderNo;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String idCard;
    private String nation;
    private String nativePlace;
    private String phone;
    private String address;
    private String bloodType;
    private String allergy;
    private String medicalHistory;
    private String healthStatus;
    private String contactName;
    private String contactPhone;
    private String contactRelation;
    private String photo;
    private LocalDate checkinDate;
    private Long bedId;
    private String bedNo;
    private String careLevel;
    private String careLevelName;
    private BigDecimal feeStandard;
    private String payType;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ElderResponse() {
    }

    public ElderResponse(Elder elder) {
        this.id = elder.getId();
        this.elderNo = elder.getElderNo();
        this.name = elder.getName();
        this.gender = elder.getGender();
        this.birthDate = elder.getBirthDate();
        this.idCard = elder.getIdCard();
        this.nation = elder.getNation();
        this.nativePlace = elder.getNativePlace();
        this.phone = elder.getPhone();
        this.address = elder.getAddress();
        this.bloodType = elder.getBloodType();
        this.allergy = elder.getAllergy();
        this.medicalHistory = elder.getMedicalHistory();
        this.healthStatus = elder.getHealthStatus();
        this.contactName = elder.getContactName();
        this.contactPhone = elder.getContactPhone();
        this.contactRelation = elder.getContactRelation();
        this.photo = elder.getPhoto();
        this.checkinDate = elder.getCheckinDate();
        this.bedId = elder.getBedId();
        this.careLevel = elder.getCareLevel();
        this.feeStandard = elder.getFeeStandard();
        this.payType = elder.getPayType();
        this.status = elder.getStatus();
        this.createdAt = elder.getCreatedAt();
        this.updatedAt = elder.getUpdatedAt();

        if (elder.getBed() != null) {
            this.bedNo = elder.getBed().getBedNo();
        }
        if (elder.getCareLevelEntity() != null) {
            this.careLevelName = elder.getCareLevelEntity().getLevelName();
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getElderNo() { return elderNo; }
    public void setElderNo(String elderNo) { this.elderNo = elderNo; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    public String getNation() { return nation; }
    public void setNation(String nation) { this.nation = nation; }
    public String getNativePlace() { return nativePlace; }
    public void setNativePlace(String nativePlace) { this.nativePlace = nativePlace; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public String getAllergy() { return allergy; }
    public void setAllergy(String allergy) { this.allergy = allergy; }
    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }
    public String getHealthStatus() { return healthStatus; }
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getContactRelation() { return contactRelation; }
    public void setContactRelation(String contactRelation) { this.contactRelation = contactRelation; }
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    public LocalDate getCheckinDate() { return checkinDate; }
    public void setCheckinDate(LocalDate checkinDate) { this.checkinDate = checkinDate; }
    public Long getBedId() { return bedId; }
    public void setBedId(Long bedId) { this.bedId = bedId; }
    public String getBedNo() { return bedNo; }
    public void setBedNo(String bedNo) { this.bedNo = bedNo; }
    public String getCareLevel() { return careLevel; }
    public void setCareLevel(String careLevel) { this.careLevel = careLevel; }
    public String getCareLevelName() { return careLevelName; }
    public void setCareLevelName(String careLevelName) { this.careLevelName = careLevelName; }
    public BigDecimal getFeeStandard() { return feeStandard; }
    public void setFeeStandard(BigDecimal feeStandard) { this.feeStandard = feeStandard; }
    public String getPayType() { return payType; }
    public void setPayType(String payType) { this.payType = payType; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

