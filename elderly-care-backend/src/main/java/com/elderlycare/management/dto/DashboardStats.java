package com.elderlycare.management.dto;

public class DashboardStats {

    private int totalElders;
    private int totalRooms;
    private int vacantBeds;
    private int totalStaff;

    // Constructors
    public DashboardStats() {}

    public DashboardStats(int totalElders, int totalRooms, int vacantBeds, int totalStaff) {
        this.totalElders = totalElders;
        this.totalRooms = totalRooms;
        this.vacantBeds = vacantBeds;
        this.totalStaff = totalStaff;
    }

    // Getters and Setters
    public int getTotalElders() {
        return totalElders;
    }

    public void setTotalElders(int totalElders) {
        this.totalElders = totalElders;
    }

    public int getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(int totalRooms) {
        this.totalRooms = totalRooms;
    }

    public int getVacantBeds() {
        return vacantBeds;
    }

    public void setVacantBeds(int vacantBeds) {
        this.vacantBeds = vacantBeds;
    }

    public int getTotalStaff() {
        return totalStaff;
    }

    public void setTotalStaff(int totalStaff) {
        this.totalStaff = totalStaff;
    }
}