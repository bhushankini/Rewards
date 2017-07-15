package com.mobilemauj.rewards.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bkini on 6/6/17.
 */

public class UserTransaction {
    private String source;

    private int points;
    private String type;
    private long timestamp;
    public static final String FIREBASE_TRANSACTION_ROOT = "transactions";

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("source", source);
        result.put("points", points);
        result.put("timestamp", ServerValue.TIMESTAMP);
        result.put("type",type);
        return result;
    }
}
