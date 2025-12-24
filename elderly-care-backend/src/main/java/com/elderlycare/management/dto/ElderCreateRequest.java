package com.elderlycare.management.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ElderCreateRequest {

    @NotBlank(message = "老人编号不能为空")
    @Size(max = 30, message = "老人编号长度不能超过30个字符")
    private String elderNo;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String name;

    @NotBlank(message = "性别不能为空")
    @Pattern(regexp = "[MF]", message = "性别必须为M或F")
    private String gender;

    private LocalDate birthDate;

    @Size(max = 18, message = "身份证号长度不能超过18位")
    private String idCard;

    @Size(max = 20, message = "民族长度不能超过20个字符")
    private String nation;

    @Size(max = 100, message = "籍贯长度不能超过100个字符")
    private String nativePlace;

    @Size(max = 20, message = "电话长度不能超过20个字符")
    private String phone;

    @Size(max = 200, message = "地址长度不能超过200个字符")
    private String address;

    @Size(max = 10, message = "血型长度不能超过10个字符")
    private String bloodType;

    @Size(max = 500, message = "过敏史长度不能超过500个字符")
    private String allergy;

    @Size(max = 1000, message = "病史长度不能超过1000个字符")
    private String medicalHistory;

    @Size(max = 500, message = "健康状况长度不能超过500个字符")
    private String healthStatus;

    @Size(max = 50, message = "紧急联系人姓名长度不能超过50个字符")
    private String contactName;

    @Size(max = 20, message = "紧急联系人电话长度不能超过20个字符")
    private String contactPhone;

    @Size(max = 50, message = "与联系人的关系长度不能超过50个字符")
    private String contactRelation;

    private String photo;

    private LocalDate checkinDate;

    @NotNull(message = "床位ID不能为空")
    private Long bedId;

    @NotBlank(message = "护理等级不能为空")
    @Size(max = 20, message = "护理等级编码长度不能超过20个字符")
    private String careLevel;

    private BigDecimal feeStandard;

    @Size(max = 20, message = "支付方式长度不能超过20个字符")
    private String payType;

    private Integer status = 1; // 默认在院

    // Getters and Setters
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
    public String getCareLevel() { return careLevel; }
    public void setCareLevel(String careLevel) { this.careLevel = careLevel; }
    public BigDecimal getFeeStandard() { return feeStandard; }
    public void setFeeStandard(BigDecimal feeStandard) { this.feeStandard = feeStandard; }
    public String getPayType() { return payType; }
    public void setPayType(String payType) { this.payType = payType; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}

