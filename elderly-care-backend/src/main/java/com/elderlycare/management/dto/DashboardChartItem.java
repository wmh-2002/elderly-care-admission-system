package com.elderlycare.management.dto;

public class DashboardChartItem {

    private String name;
    private int value;
    private String color;

    // Constructors
    public DashboardChartItem() {}

    public DashboardChartItem(String name, int value, String color) {
        this.name = name;
        this.value = value;
        this.color = color;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}