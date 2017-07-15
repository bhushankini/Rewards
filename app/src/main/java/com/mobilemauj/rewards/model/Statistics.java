package com.mobilemauj.rewards.model;

public class Statistics {
    private int dice;
    private int tictactoe;
    public static final String FIREBASE_STATISTICS_ROOT = "statistics";
    public static final String FIREBASE_STATISTICS_CHILD_DICE = "dice";
    public static final String FIREBASE_STATISTICS_CHILD_TICTACTOE = "tictactoe";

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
}
