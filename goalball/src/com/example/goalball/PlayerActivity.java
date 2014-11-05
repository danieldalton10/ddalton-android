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
    private Player updatePlayer;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Intent intent = getIntent();
        bundle = getIntent().getExtras();
        String json = intent.getExtras().getString("updatePlayer");
        if (savedInstanceState != null) {
            json = savedInstanceState.getString("updatePlayer");
            bundle = savedInstanceState.getBundle("bundle");
        }

        if (json != null) {
            Gson gson = new Gson();
            updatePlayer = gson.fromJson(json, Player.class);
            Log.d("GOALBALL", "Loaded the player");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Gson gson = new Gson();
        savedInstanceState.putString("updatePlayer", gson.toJson(updatePlayer));
        savedInstanceState.putBundle("bundle", bundle);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onThrow(View view) {
        if (updatePlayer != null) {
            updatePlayer.setTotalThrows(updatePlayer.getTotalThrows() + 1);
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
        if (updatePlayer != null) {
            updatePlayer.getGoals().add(new Player.Goal(System.currentTimeMillis()));
            String message = updatePlayer.getPlayerDescription() + " has scored "
                    + updatePlayer.getGoals().size() + " goals";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
        backToMain();
    }

    private void backToMain() {
        Intent intent = new Intent ();
        Gson gson = new Gson();
        String json = gson.toJson(updatePlayer);
        bundle.putString("updatePlayer", json);
        Log.d("GOALBALL", "Json back to main = " + json);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish ();
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
