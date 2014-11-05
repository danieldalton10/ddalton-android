package com.example.goalball;

import java.util.HashMap;

public class Team {
    private String id;
    private HashMap<String, Player> players = new HashMap<String, Player>();
    private int score;
    private int ownGoal;

    public Team(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, Player> getPlayers() {
        return players;
    }

    public void setPlayers(HashMap<String, Player> players) {
        this.players = players;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getOwnGoal() {
        return ownGoal;
    }

    public void setOwnGoal(int ownGoal) {
        this.ownGoal = ownGoal;
    }

}
