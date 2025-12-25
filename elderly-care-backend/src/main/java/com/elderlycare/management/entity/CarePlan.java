package com.elderlycare.management.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "care_plan")
public class CarePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "elder_id", nullable = false)
    private Long elderId;

    @Column(name = "care_level_id", length = 20) // Reference to care_level.level_code which is VARCHAR(20)
    private String careLevelId;

    @Column(name = "plan_content", nullable = false, columnDefinition = "TEXT")
    private String planContent;

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    @Column(name = "status", length = 20) // 状态字段
    private String status;

    @Column(name = "start_date") // 开始日期
    private LocalDateTime startDate;

    @Column(name = "end_date") // 结束日期
    private LocalDateTime endDate;

    @Column(name = "assigned_nurse_id") // 分配的护理员ID
    private Long assignedNurseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "elder_id", insertable = false, updatable = false)
    private Elder elder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "care_level_id", insertable = false, updatable = false, referencedColumnName = "level_code")
    private CareLevel careLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_nurse_id", insertable = false, updatable = false, referencedColumnName = "id")
    private SysUser assignedNurse; // 关联分配的护理员

    public CarePlan() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getElderId() {
        return elderId;
    }

    public void setElderId(Long elderId) {
        this.elderId = elderId;
    }

    public String getCareLevelId() {
        return careLevelId;
    }

    public void setCareLevelId(String careLevelId) {
        this.careLevelId = careLevelId;
    }

    public String getPlanContent() {
        return planContent;
    }

    public void setPlanContent(String planContent) {
        this.planContent = planContent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Long getAssignedNurseId() {
        return assignedNurseId;
    }

    public void setAssignedNurseId(Long assignedNurseId) {
        this.assignedNurseId = assignedNurseId;
    }

    public SysUser getAssignedNurse() {
        return assignedNurse;
    }

    public void setAssignedNurse(SysUser assignedNurse) {
        this.assignedNurse = assignedNurse;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Elder getElder() {
        return elder;
    }

    public void setElder(Elder elder) {
        this.elder = elder;
    }

    public CareLevel getCareLevel() {
        return careLevel;
    }

    public void setCareLevel(CareLevel careLevel) {
        this.careLevel = careLevel;
    }
}

