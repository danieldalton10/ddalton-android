package com.example.ontrack;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final float STABILITY_MARGIN = 3;
    private static final float COURSE_MARGIN = 10;
    private static final long UPDATE_OFFSET = 3000;
    private static final long STABILITY_TIME_OFFSET = 300;

    private long lastUpdate = 0;
    private Vibrator vibe;
    private Button setButton;
    private long lastStableHeadingTime = 0;
    private float lastStableHeading = 0;
    private float currentHeading;
    private float direction;
    private boolean trackingDirection;
    private boolean alreadyBackOnTrack;
    
    private SensorManager sensorManager;
    /* TODO: remove warnings use modern compass method */
    private final SensorListener mListener = new SensorListener() {
        public void onSensorChanged(int sensor, float[] values) {
            currentHeading = values[0]; // Values are yaw (heading), pitch, and
            // roll.
            handleDirection();
        }

        public void onAccuracyChanged(int arg0, int arg1) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(mListener, SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_GAME);
        setButton = (Button) findViewById(R.id.btn_set_compass);
        setButton.setEnabled(false);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (savedInstanceState != null) {
            direction = savedInstanceState.getFloat("direction");
            trackingDirection = savedInstanceState.getBoolean("trackingDirection");
            vibrateOffCourse();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("trackingDirection", trackingDirection);
        savedInstanceState.putFloat("direction", direction);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        vibe.cancel();
        sensorManager.unregisterListener(mListener);
        super.onDestroy();
    }

    public void handleDirection() {
        boolean reliableReading = false;
        if (Math.abs(currentHeading - lastStableHeading) < STABILITY_MARGIN) {
            if (lastStableHeadingTime + STABILITY_TIME_OFFSET < System.currentTimeMillis()) {
                reliableReading = true;
                setButton.setEnabled(true);
                setButton.setText("Set " + Math.round(currentHeading) + " degrees");
            }
        } else { // compass moved again start from scratch
            lastStableHeading = currentHeading;
            lastStableHeadingTime = System.currentTimeMillis();
        }

        if (trackingDirection && reliableReading) {
            vibrateOffCourse();
        }
    }

    public void setDirection(View view) {
        this.direction = currentHeading;
        this.trackingDirection = true;
        String message = "Set your direction to: " + Math.round(currentHeading) + " degrees";
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        vibe.cancel();
    }

    private int getDegreeOffCourse() {
        int left;
        int right;
        // the case when our current heading is > than the value of direction we
        // want to go
        if (currentHeading > direction) {
            left = Math.round(currentHeading - direction);
            right = Math.round((360 - currentHeading) + direction);
        } else {
            left = Math.round((currentHeading + 360) - direction);
            right = Math.round(Math.abs(currentHeading - direction));
        }
        if (left < right) { // We could go either left or right one is shorter
                            // return the shorter distance
            return -1 * left; // Make a left turn returned as a negative value
        } else {
            return right; // right is positive
        }
    }

    private void vibrateOffCourse() {
        if (System.currentTimeMillis() < lastUpdate + UPDATE_OFFSET) {
            return;
        }

        int offCourse = getDegreeOffCourse();
        long duration = 10 * Math.abs(offCourse);
        lastUpdate = System.currentTimeMillis() + duration;
        vibe.cancel();
        if (Math.abs(offCourse) < COURSE_MARGIN) {
            if (!alreadyBackOnTrack) {
                alreadyBackOnTrack = true;
                long[] pattern = { 0, 300, 300, 300, 300, 300, 0 };
                vibe.vibrate(pattern, -1);
            }
        } else if (offCourse < 0) {
            alreadyBackOnTrack = false;
            long[] pattern = { 0, 300, 300, duration, 0 };
            vibe.vibrate(pattern, -1);
        } else {
            alreadyBackOnTrack = false;
            long[] pattern = { 0, duration, 0 };
            vibe.vibrate(pattern, -1);
        }
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
