package com.example.goalball;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

public class MainActivity extends Activity {
    private final static int REQUEST_PLAYER = 1;
    private HashMap<String, Player> players = new HashMap<String, Player>();
    private Button btnTeam1Player1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTeam1Player1 = (Button) findViewById(R.id.btn_team1_1);
        for (int i = 1; i <= 9; i++) {
            Player player = new Player("1", i);
            players.put(player.getPlayerDescription(), player);
            player = new Player("2", i);
            players.put(player.getPlayerDescription(), player);
        }
        if (savedInstanceState != null) {
            Log.d("GOALBALL", "From saved instance state");
            loadFromBundle(savedInstanceState);
        } 
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        case REQUEST_PLAYER: {
            if (resultCode == Activity.RESULT_OK && data.getExtras() != null) {
                Log.d("GOALBALL", "Back from intent");
                loadFromBundle(data.getExtras());
                String json = data.getExtras().getString("updatePlayer");
                if (json != null) {
                    Gson gson = new Gson();
                    Player player = gson.fromJson(json, Player.class);
                    players.put(player.getPlayerDescription(), player);
                }
            }
        }
        }
    }

    private void loadFromBundle(Bundle bundle) {
        String playersString = bundle.getString("players");
        if (playersString != null) {
            Gson gson = new Gson();
            for (String playerString : playersString.split(",")) {
                Log.d("GOALBALL", "Loading player " + playerString + " on saved state");
                String json = bundle.getString(playerString);
                // Player player = (Player) bundle.getParcelable(playerString);
                players.put(playerString, gson.fromJson(json, Player.class));
            }
        }
    }

    private Bundle saveToBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        Gson gson = new Gson();
        for (String playerDescription : players.keySet()) {
            String json = gson.toJson(players.get(playerDescription));
            bundle.putString(playerDescription, json);
            sb.append(playerDescription).append(",");
        }
        if (sb.length() > 0) {
            String playersString = sb.substring(0, sb.length() - 1);
            Log.d("GOALBALL", "Players string = " + playersString);
            bundle.putString("players", playersString);
        }
        return bundle;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState = saveToBundle(savedInstanceState);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onPlayer(View view) {
        Intent intent = new Intent(this, PlayerActivity.class);
        Bundle bundle = saveToBundle(new Bundle());
        Player player;
        if (view == btnTeam1Player1) {
            player = new Player("1", 1);
        } else {
            player = new Player("2", 1);
        }

        // bundle.putParcelable("updatePlayer",
        // players.get(player.getPlayerDescription ()));
        Gson gson = new Gson();
        String json = gson.toJson(players.get(player.getPlayerDescription()));
        Log.d("GOALBALL", "Sending the following json to the player activity: " + json);
        bundle.putString("updatePlayer", json);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_PLAYER);
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
