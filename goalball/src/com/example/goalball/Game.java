package com.example.goalball;

import java.util.HashMap;

public class Game {
    private HashMap<String, Team> teams = new HashMap<String, Team>();
    private long startTime;
    private long halfTime;
    private long startSecondHalf;
    private long gameEnd;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getHalfTime() {
        return halfTime;
    }

    public void setHalfTime(long halfTime) {
        this.halfTime = halfTime;
    }

    public long getStartSecondHalf() {
        return startSecondHalf;
    }

    public void setStartSecondHalf(long startSecondHalf) {
        this.startSecondHalf = startSecondHalf;
    }

    public long getGameEnd() {
        return gameEnd;
    }

    public void setGameEnd(long gameEnd) {
        this.gameEnd = gameEnd;
    }

    public HashMap<String, Team> getTeams() {
        return teams;
    }

    public void setTeams(HashMap<String, Team> teams) {
        this.teams = teams;
    }

}
