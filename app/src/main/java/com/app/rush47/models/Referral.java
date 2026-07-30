package com.app.rush47.models;

/** A single person who signed up using this member's referral code. */
public class Referral {

    private final String userName;
    private final String joinedAt;

    public Referral(String userName, String joinedAt) {
        this.userName = userName;
        this.joinedAt = joinedAt;
    }

    public String getUserName() { return userName; }
    public String getJoinedAt() { return joinedAt; }
}
