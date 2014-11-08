package com.example.goalball;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

public class PlayerActivity extends Activity {
    private final static String GAME_STRING = "GAME";
    private Player updatePlayer;
    private Player lastThrower = null;
    private Game game;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Intent intent = getIntent();
        bundle = getIntent().getExtras();
        String jsonPlayer = intent.getExtras().getString("updatePlayer");
        String jsonGame = intent.getExtras().getString(GAME_STRING);
        String jsonLastThrower = intent.getExtras().getString("lastThrower");
        if (savedInstanceState != null) {
            jsonPlayer = savedInstanceState.getString("updatePlayer");
            jsonGame = savedInstanceState.getString(GAME_STRING);
            jsonLastThrower = savedInstanceState.getString("lastThrower");
            bundle = savedInstanceState.getBundle("bundle");
        }

        Gson gson = new Gson();
        if (jsonPlayer != null) {
            updatePlayer = gson.fromJson(jsonPlayer, Player.class);
            Log.d("GOALBALL", "Loaded the player");
        }
        if (jsonGame != null) {
            game = gson.fromJson(jsonGame, Game.class);
        }
        if (jsonLastThrower != null) {
            lastThrower = gson.fromJson(jsonLastThrower, Player.class);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Gson gson = new Gson();
        savedInstanceState.putString("updatePlayer", gson.toJson(updatePlayer));
        if (lastThrower != null) {
            savedInstanceState.putString("lastThrower", gson.toJson(lastThrower));
        }
        savedInstanceState.putString(GAME_STRING, gson.toJson(game));
        savedInstanceState.putBundle("bundle", bundle);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onThrow(View view) {
        lastThrower = updatePlayer;
        if (updatePlayer != null) {
            updatePlayer.setTotalThrows(updatePlayer.getTotalThrows() + 1,
                    game.getTeams().get(updatePlayer.getTeam()).getTotalPlayer());
            Log.d("GOALBALL", "Total throws for " + updatePlayer.getPlayerDescription() + " is: "
                    + updatePlayer.getTotalThrows());
            String message = updatePlayer.getPlayerDescription() + " has had "
                    + updatePlayer.getTotalThrows() + " throws";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        } else {
            Log.d("GOALBALL", "Couldn't update the player is null");
        }
        backToMain();
    }

    public void onGoal(View view) {
        String message = "An error occurred";
        Log.d("GOALBALL", "is allowed to score: "+updatePlayer.isAllowToScore());
        if (!updatePlayer.isAllowToScore()) {
            updatePlayer.setAllowToScore(true);
            Log.d("GOALBALL", "Setting is allowed to score: "+updatePlayer.isAllowToScore());
            message = "Goal automatically added - tap back here again if you really meant to do this.";
        } else {
            if (updatePlayer != null) {
                updatePlayer.increaseGoals(1, game.getTeams().get(updatePlayer.getTeam()).getTotalPlayer());
                message = updatePlayer.getPlayerDescription() + " has scored "
                        + updatePlayer.getGoals().size() + " goals";
            }
        }
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        backToMain();
    }

    public void onBlock(View view) {
        if (updatePlayer != null) {
            updatePlayer.setSaves(updatePlayer.getSaves() + 1, game.getTeams().get(updatePlayer.getTeam())
                    .getTotalPlayer());
            Log.d("GOALBALL",
                    "Saves for " + updatePlayer.getPlayerDescription() + " is: " + updatePlayer.getSaves());
            String message = updatePlayer.getPlayerDescription() + " has had " + updatePlayer.getSaves()
                    + " saves";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        } else {
            Log.d("GOALBALL", "Couldn't update the player is null");
        }
        backToMain();
    }

    public void onError(View view) {
        if (updatePlayer != null) {
            Player throwerTeam = null;
            if (lastThrower != null) {
                throwerTeam = game.getTeams().get(lastThrower.getTeam()).getTotalPlayer();
            }
            updatePlayer.smartSetErrors(updatePlayer.getErrors() + 1,
                    game.getTeams().get(updatePlayer.getTeam()).getTotalPlayer(), lastThrower, throwerTeam);

            String message = updatePlayer.getPlayerDescription() + " has had " + updatePlayer.getErrors()
                    + " errors";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        } else {
            Log.d("GOALBALL", "Couldn't update the player is null");
        }
        backToMain();
    }

    private void backToMain() {
        if (lastThrower != null) {
            game.getTeams().get(lastThrower.getTeam()).getPlayers().put(lastThrower.getNumber(), lastThrower);
        }
        game.getTeams().get(updatePlayer.getTeam()).getPlayers().put(updatePlayer.getNumber(), updatePlayer);
        Intent intent = new Intent();
        Gson gson = new Gson();
        // String json = gson.toJson(updatePlayer);
        // bundle.putString("updatePlayer", json);
        bundle.putString(GAME_STRING, gson.toJson(game));
        if (lastThrower != null) {
            bundle.putString("lastThrower", gson.toJson(lastThrower));
        }
        // Log.d("GOALBALL", "Json back to main = " + json);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
