package com.example.demo.model;

import java.util.List;

public class NotificationRequest {

    private String userId;
    private List<String> userIds; // Assuming this is the correct type
    private String title;
    private String body;

    public NotificationRequest() {
        // Default constructor
    }

    public NotificationRequest(String title, String body) {
        this.title = title;
        this.body = body;
    }

    // Getters and Setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
