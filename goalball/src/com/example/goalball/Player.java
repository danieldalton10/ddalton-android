package com.example.goalball;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private String team;
    private int number;
    private int totalThrows;
    private List<Goal> goals = new ArrayList<Goal>();

    public Player(int number, String team) {
        this.number = number;
        this.team = team;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getTotalThrows() {
        return totalThrows;
    }

    public void setTotalThrows(int totalThrows) {
        this.totalThrows = totalThrows;
    }

    public static class Goal {
        private long time;

        public Goal(long time) {
            this.time = time;
        }

        public Goal() {
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }
    }

    public String getPlayerDescription() {
        return "Team: " + team + " number: " + number;
    }
}
