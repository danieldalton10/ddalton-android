package com.example.goalball;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Team {
    private String id;
    private String name;
    private HashMap<String, Player> players = new HashMap<String, Player>();
    private int ownGoal;
    private Player totalPlayer;

    public Team(String id, String name) {
        this.id = id;
        this.name = name;
        this.totalPlayer = new Player("Total", name);
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

    public int getOwnGoal() {
        return ownGoal;
    }

    public void setOwnGoal(int ownGoal) {
        this.ownGoal = ownGoal;
    }

    public Player getTotalPlayer() {
        return totalPlayer;
    }

    public void setTotalPlayer(Player totalPlayer) {
        this.totalPlayer = totalPlayer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // TODO re visit this won't work if we have a team of 3 - players number 6,8,9 
    public List<Player> getPlayersAsList() {
        List<Player> playersList = new ArrayList<Player>();
        for (int number = 1; number <= players.size(); number++) {
            Player player = players.get(String.valueOf(number));
            if (player != null) {
                playersList.add(player);
            }
        }
        playersList.add(totalPlayer);
        return playersList;
    }
}
