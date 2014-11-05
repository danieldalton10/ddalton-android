package com.example.goalball;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<Player>();
        for (String team : teams.keySet()) {
            for (String number : teams.get(team).getPlayers().keySet()) {
                players.add(teams.get(team).getPlayers().get(number));
            }
        }
        return players;
    }
}
