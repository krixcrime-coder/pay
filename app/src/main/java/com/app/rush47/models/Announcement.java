package com.app.rush47.models;

/** A single announcement shown on the Account tab's Announcement page. */
public class Announcement {

    private final String message;
    private final String createdAt;

    public Announcement(String message, String createdAt) {
        this.message = message;
        this.createdAt = createdAt;
    }

    public String getMessage() { return message; }
    public String getCreatedAt() { return createdAt; }
}
