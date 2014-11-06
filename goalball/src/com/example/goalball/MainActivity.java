package com.example.goalball;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

public class MainActivity extends Activity {
    private final static int REQUEST_PLAYER = 1;
    private final static String HOME = "HOME";
    private final static String AWAY = "AWAY";
    private final static String GAME_STRING = "GAME";
    private final static String GOALBALL_DIR = "Goalball";

    private Game game = new Game();
    private HashMap<String, List<String>> buttonToPlayer = new HashMap<String, List<String>>();

    private Button btnHome1;
    private Button btnHome2;
    private Button btnHome3;
    private Button btnAway1;
    private Button btnAway2;
    private Button btnAway3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnHome1 = (Button) findViewById(R.id.btn_home1);
        btnHome2 = (Button) findViewById(R.id.btn_home2);
        btnHome3 = (Button) findViewById(R.id.btn_home3);
        btnAway1 = (Button) findViewById(R.id.btn_away1);
        btnAway2 = (Button) findViewById(R.id.btn_away2);
        btnAway3 = (Button) findViewById(R.id.btn_away3);
        mapButtonsToPlayers();
        game.getTeams().put(HOME, new Team(HOME));
        game.getTeams().put(AWAY, new Team(AWAY));
        for (int i = 1; i <= 3; i++) {
            Player player = new Player(i, HOME);
            game.getTeams().get(HOME).getPlayers().put(String.valueOf(player.getNumber()), player);
            player = new Player(i, AWAY);
            game.getTeams().get(AWAY).getPlayers().put(String.valueOf(player.getNumber()), player);
        }
        if (savedInstanceState != null) {
            Log.d("GOALBALL", "From saved instance state");
            loadFromBundle(savedInstanceState);
        } else {
            Log.d("GOALBALL", "Setting the start time");
            game.setStartTime(System.currentTimeMillis());
        }
    }

    private void mapButtonsToPlayers() {
        buttonToPlayer.put(String.valueOf(btnHome1.getId()), playerDescription(HOME, "1"));
        buttonToPlayer.put(String.valueOf(btnHome2.getId()), playerDescription(HOME, "2"));
        buttonToPlayer.put(String.valueOf(btnHome3.getId()), playerDescription(HOME, "3"));
        buttonToPlayer.put(String.valueOf(btnAway1.getId()), playerDescription(AWAY, "1"));
        buttonToPlayer.put(String.valueOf(btnAway2.getId()), playerDescription(AWAY, "2"));
        buttonToPlayer.put(String.valueOf(btnAway3.getId()), playerDescription(AWAY, "3"));
    }

    private List<String> playerDescription(String team, String number) {
        List<String> list = new ArrayList<String>();
        list.add(team);
        list.add(number);
        return list;
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
                    game.getTeams().get(player.getTeam()).getPlayers()
                            .put(String.valueOf(player.getNumber()), player);
                }
            }
        }
        }
    }

    private void loadFromBundle(Bundle bundle) {
        String json = bundle.getString(GAME_STRING);
        Gson gson = new Gson();
        game = gson.fromJson(json, Game.class);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Gson gson = new Gson();
        savedInstanceState.putString(GAME_STRING, gson.toJson(game));
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onPlayer(View view) {
        Intent intent = new Intent(this, PlayerActivity.class);
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        bundle.putString(GAME_STRING, gson.toJson(game));
        List<String> player = buttonToPlayer.get(String.valueOf(view.getId()));
        String json = gson.toJson(game.getTeams().get(player.get(0)).getPlayers().get(player.get(1)));
        Log.d("GOALBALL", "Sending the following json to the player activity: " + json);
        bundle.putString("updatePlayer", json);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_PLAYER);
    }

    private File save() throws IOException {
        CsvAssembler csvAssembler = new CsvAssembler();
        String header = "Team,Number,Saves,Errors,Throws,Goals,Throws Per Goal,Score Time,Goals VS Errors";
        List<HashMap<String, String>> contents = new ArrayList<HashMap<String, String>>();
        for (Player player : game.getAllPlayers()) {
            HashMap<String, String> row = new HashMap<String, String>();
            row.put("Team", player.getTeam());
            row.put("Number", String.valueOf(player.getNumber()));
            row.put("Throws", String.valueOf(player.getTotalThrows()));
            row.put("Goals", String.valueOf(player.getGoals().size()));
            row.put("Score Time", player.getScoreTimes(game.getStartTime()));
            row.put("Throws Per Goal", String.valueOf(player.getThrowsPerGoal()));
            row.put("Saves", String.valueOf(player.getSaves()));
            row.put("Errors", String.valueOf(player.getErrors()));
            row.put("Goals VS Errors", String.valueOf(player.getGoalsVSErrors()));
            contents.add(row);
        }
        String filename = "goalball" + System.currentTimeMillis() + ".csv";
        File path = Environment.getExternalStoragePublicDirectory(GOALBALL_DIR);
        File file = new File(path, filename);
        path.mkdirs();
        FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
        BufferedWriter writer = new BufferedWriter(fileWriter);
        csvAssembler.produceCSV(writer, header, contents);
        writer.close();
        updateInFS(file);
        return file;
    }

    public void onSave (View view) {
        String text = "Saved stats";
        try {
            save ();
        } catch (IOException e) {
            Log.d("GOALBALL", "IOException: "+e.toString ());
            text = "Failed to save stats";
        }
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
    
    public void onShare(View view) {
        try {
            String path = save ().toString ();
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/csv");
            share.putExtra(Intent.EXTRA_STREAM,Uri.parse("file:///"+path));
            startActivity(Intent.createChooser(share, "Share Stats"));
        } catch (IOException e) {
            String text = "Something went wrong...";
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            Log.d("GOALBALL", "IOException: "+e.toString ());
        }
    }
    
    private void updateInFS(File file) {
        MediaScannerConnection.scanFile(this, new String[] { file.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
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
