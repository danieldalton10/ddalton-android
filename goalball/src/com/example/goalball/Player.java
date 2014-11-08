package com.example.goalball;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class Player {
    private String name;
    private String team;
    private String number;
    private int totalThrows;
    private List<Goal> goals = new ArrayList<Goal>();
    private int saves;
    private int errors;
    private boolean allowToScore = true;

    public Player(String number, String team) {
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

    public void increaseGoals(int increase, Player team) {
        if (increase > 0) {
            Goal goal = new Goal(System.currentTimeMillis());
            goals.add(goal);
            if (team != null) {
                team.increaseGoals(increase, null);
            }
        } else { // remove a goal
            Goal goal = goals.remove(goals.size() - 1);
            if (team != null) {
                team.getGoals().remove(goal);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getTotalThrows() {
        return totalThrows;
    }

    public void setTotalThrows(int totalThrows, Player team) {
        if (team != null) {
            int difference = totalThrows - this.totalThrows;
            team.setTotalThrows(team.getTotalThrows() + difference, null);
        }
        this.totalThrows = totalThrows;
    }

    public int getSaves() {
        return saves;
    }

    public void setSaves(int saves, Player team) {
        if (team != null) {
            int difference = saves - this.saves;
            team.setSaves(team.getSaves() + difference, null);
        }
        this.saves = saves;
    }

    public int getErrors() {
        return errors;
    }

    public void setErrors(int errors, Player team) {
        if (team != null) {
            int difference = errors - this.errors;
            team.setErrors(team.getErrors() + difference, null);
        }
        this.errors = errors;
    }

    public void smartSetErrors(int errors, Player team, Player lastThrower, Player scoringTeam) {
        setErrors(errors, team);
        if (lastThrower != null) {
            lastThrower.increaseGoals(1, scoringTeam);
            lastThrower.setAllowToScore(false);
        } // else should really be illegal for now allow it
    }

    public boolean isAllowToScore() {
        return allowToScore;
    }

    public void setAllowToScore(boolean allowToScore) {
        this.allowToScore = allowToScore;
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

    public float getThrowsPerGoal() {
        if (goals.size() == 0) {
            return 0;
        }
        return ((float) totalThrows) / ((float) goals.size());
    }

    public String getScoreTimes(long startTime) {
        StringBuilder sb = new StringBuilder();
        for (Goal goal : goals) {
            sb.append(msToReadableTime(goal.getTime() - startTime)).append(" ");
        }
        if (sb.length() > 0) {
            return sb.substring(0, sb.length() - 1);
        }
        return "";
    }

    private String msToReadableTime(long time) {
        time = time / 1000;
        long minutes = time / 60;
        long seconds = time % 60;
        String minutesPrefix = "";
        String secondsPrefix = "";
        if (minutes < 10) {
            minutesPrefix = "0";
        }
        if (seconds < 10) {
            secondsPrefix = "0";
        }
        Log.d("GOALBALL", "Minutes = " + minutes);
        Log.d("GOALBALL", "Seconds = " + seconds);
        return minutesPrefix + minutes + ":" + secondsPrefix + seconds;
    }

    public int getGoalsVSErrors() {
        return goals.size() - errors;
    }
}
