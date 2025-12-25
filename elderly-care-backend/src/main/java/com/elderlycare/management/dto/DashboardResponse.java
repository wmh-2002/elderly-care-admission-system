package com.elderlycare.management.dto;

import java.util.List;

public class DashboardResponse {

    private DashboardStats stats;
    private List<DashboardStatItem> additionalStats;
    private List<DashboardChartItem> careLevelDistribution;
    private List<DashboardChartItem> roomOccupancy;
    private List<DashboardNewsItem> newsList;
    private double occupancyRate;
    private int notificationCount;
    private String username;
    private String currentDate;

    // Constructors
    public DashboardResponse() {}

    public DashboardResponse(DashboardStats stats, List<DashboardStatItem> additionalStats,
                            List<DashboardChartItem> careLevelDistribution, List<DashboardChartItem> roomOccupancy,
                            List<DashboardNewsItem> newsList, double occupancyRate, int notificationCount,
                            String username, String currentDate) {
        this.stats = stats;
        this.additionalStats = additionalStats;
        this.careLevelDistribution = careLevelDistribution;
        this.roomOccupancy = roomOccupancy;
        this.newsList = newsList;
        this.occupancyRate = occupancyRate;
        this.notificationCount = notificationCount;
        this.username = username;
        this.currentDate = currentDate;
    }

    // Getters and Setters
    public DashboardStats getStats() {
        return stats;
    }

    public void setStats(DashboardStats stats) {
        this.stats = stats;
    }

    public List<DashboardStatItem> getAdditionalStats() {
        return additionalStats;
    }

    public void setAdditionalStats(List<DashboardStatItem> additionalStats) {
        this.additionalStats = additionalStats;
    }

    public List<DashboardChartItem> getCareLevelDistribution() {
        return careLevelDistribution;
    }

    public void setCareLevelDistribution(List<DashboardChartItem> careLevelDistribution) {
        this.careLevelDistribution = careLevelDistribution;
    }

    public List<DashboardChartItem> getRoomOccupancy() {
        return roomOccupancy;
    }

    public void setRoomOccupancy(List<DashboardChartItem> roomOccupancy) {
        this.roomOccupancy = roomOccupancy;
    }

    public List<DashboardNewsItem> getNewsList() {
        return newsList;
    }

    public void setNewsList(List<DashboardNewsItem> newsList) {
        this.newsList = newsList;
    }

    public double getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(double occupancyRate) {
        this.occupancyRate = occupancyRate;
    }

    public int getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(int notificationCount) {
        this.notificationCount = notificationCount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
}