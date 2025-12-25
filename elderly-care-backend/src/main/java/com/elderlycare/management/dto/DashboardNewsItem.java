package com.elderlycare.management.dto;

public class DashboardNewsItem {

    private String title;
    private String content;
    private String timestamp;
    private String type;
    private String icon;

    // Constructors
    public DashboardNewsItem() {}

    public DashboardNewsItem(String title, String content, String timestamp, String type, String icon) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.type = type;
        this.icon = icon;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}