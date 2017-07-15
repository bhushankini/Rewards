package com.mobilemauj.rewards.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    private String name;
    private String userId;
    private int points;
    private long lastopen;
    private String email;
    private String photoUrl;
    private String referalId;

    public static final String FIREBASE_USER_ROOT = "users";
    private String country;
    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String userId, String name, int points, long lastopen, String photoUrl, String referalId) {
        this.name = name;
        this.userId = userId;
        this.points = points;
        this.lastopen = lastopen;
        this.photoUrl = photoUrl;
        this.referalId = referalId;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setLastopen(long lastopen) {
        this.lastopen = lastopen;
    }

    public long getLastopen() {
        return lastopen;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setReferalId(String referalId) {
        this.referalId = referalId;
    }

    public String getReferalId() {
        return referalId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}