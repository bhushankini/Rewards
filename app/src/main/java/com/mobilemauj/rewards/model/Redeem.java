package com.mobilemauj.rewards.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bkini on 7/2/17.
 */

public class Redeem {
    private int value;
    private String recipient;
    private String display;
    private String details;
    private String brand;
    private int status;
    private long timestamp;
    private long processDate;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public long getProcessDate() {
        return processDate;
    }

    public void setProcessDate(long processDate) {
        this.processDate = processDate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("brand", brand);
        result.put("display", display);
        result.put("value", value);
        result.put("recipient",recipient);
        result.put("details",details);
        result.put("timestamp", ServerValue.TIMESTAMP);
        result.put("status",0);
        return result;
    }
}
