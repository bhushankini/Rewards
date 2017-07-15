package com.mobilemauj.rewards.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Rewards implements Serializable {

    private String display;
    private int icon;
    private int value;
    private String brand;
    private String worth;
    private int type;
    private String bgcolor;
    private String textcolor;
    private String country;

    public static final String FIREBASE_REWARDS_ROOT = "rewards";
    public static final String FIREBASE_REDEEM_ROOT = "redeem";

    public static final String REWARD_EXTRAS = "rewards_extras";
    public static final String IMAGES_BASE_URL ="http://mobilemauj.com/rewards/images/";

    public Rewards() {
    }

    public Rewards(String display, int icon, int value) {
       this.display = display;
        this.icon = icon;
        this.value = value;

    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getBrand() {
        return brand;
    }

    public void setWorth(String worth) {
        this.worth = worth;
    }

    public String getWorth() {
        return worth;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getBgcolor() {
        return bgcolor;
    }

    public void setBgcolor(String bgcolor) {
        this.bgcolor = bgcolor;
    }

    public String getTextcolor() {
        return textcolor;
    }

    public void setTextcolor(String textcolor) {
        this.textcolor = textcolor;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}