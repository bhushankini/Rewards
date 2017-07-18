package com.mobilemauj.rewards.model;

public class Statistics {
    private int dice;
    private int tictactoe;
    private long lastplayed;
    public static final String FIREBASE_STATISTICS_ROOT = "statistics";
    public static final String FIREBASE_STATISTICS_CHILD_DICE = "dice";
    public static final String FIREBASE_STATISTICS_CHILD_TICTACTOE = "tictactoe";
    public static final String FIREBASE_STATISTICS_CHILD_LAST_PLAYED = "lastplayed";

    public Statistics(int dice, int tictactoe){
        this.dice = dice;
        this.tictactoe = tictactoe;

    }
    public Statistics(){}

    public int getDice() {
        return dice;
    }

    public int getTictactoe() {
        return tictactoe;
    }

    public void setDice(int dice) {
        this.dice = dice;
    }

    public void setTictactoe(int tictactoe) {
        this.tictactoe = tictactoe;
    }

    public long getLastplayed() {
        return lastplayed;
    }
    public void setLastplayed(long lastplayed) {
        this.lastplayed = lastplayed;
    }
}
