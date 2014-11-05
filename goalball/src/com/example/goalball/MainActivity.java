package com.example.goalball;

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
    private final static String HOME = "HOME";
    private final static String AWAY = "AWAY";
    private final static String GAME_STRING = "GAME";
    
    private Game game = new Game ();
    private Button btnTeam1Player1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTeam1Player1 = (Button) findViewById(R.id.btn_team1_1);
        game.getTeams ().put(HOME, new Team (HOME));
        game.getTeams ().put(AWAY, new Team (AWAY));
        for (int i = 1; i <= 9; i++) {
            Player player = new Player(i, HOME);
            game.getTeams().get(HOME).getPlayers ().put (String.valueOf(player.getNumber ()), player);
            player = new Player(i, AWAY);
            game.getTeams().get(AWAY).getPlayers ().put (String.valueOf(player.getNumber ()), player);
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
                    game.getTeams ().get(player.getTeam ()).getPlayers().put(String.valueOf(player.getNumber()), player);
                }
            }
        }
        }
    }

    private void loadFromBundle(Bundle bundle) {
        /*String playersString = bundle.getString("players");
        if (playersString != null) {
            Gson gson = new Gson();
            for (String playerString : playersString.split(",")) {
                Log.d("GOALBALL", "Loading player " + playerString + " on saved state");
                String json = bundle.getString(playerString);
                // Player player = (Player) bundle.getParcelable(playerString);
                players.put(playerString, gson.fromJson(json, Player.class));
            }
        }*/
        String json = bundle.getString(GAME_STRING);
        Gson gson = new Gson ();
        game = gson.fromJson(json, Game.class);
    }

    /*private Bundle saveToBundle(Bundle bundle) {
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
    }*/

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //savedInstanceState = saveToBundle(savedInstanceState);
        Gson gson = new Gson ();
        savedInstanceState.putString(GAME_STRING, gson.toJson (game));
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onPlayer(View view) {
        Intent intent = new Intent(this, PlayerActivity.class);
        //Bundle bundle = saveToBundle(new Bundle());
        Bundle bundle = new Bundle ();
        Gson gson = new Gson ();
        bundle.putString(GAME_STRING, gson.toJson (game));
        String team;
        String number;
        if (view == btnTeam1Player1) {
            team = HOME;
            number = "1";
        } else {
            team = AWAY;
            number = "1";
        }
        String json = gson.toJson(game.getTeams ().get(team).getPlayers ().get (number));
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
