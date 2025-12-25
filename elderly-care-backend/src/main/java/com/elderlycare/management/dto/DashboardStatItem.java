package com.elderlycare.management.dto;

public class DashboardStatItem {

    private int value;
    private String label;

    // Constructors
    public DashboardStatItem() {}

    public DashboardStatItem(int value, String label) {
        this.value = value;
        this.label = label;
    }

    // Getters and Setters
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}