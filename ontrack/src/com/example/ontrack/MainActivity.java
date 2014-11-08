package com.example.ontrack;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static int MINIMUM_STABILITY_COUNT = 0;
    private static float STABILITY_MARGIN = 3;
    private static float COURSE_MARGIN = 10;
    private static long SPOKEN_TIME_OFFSET = 5000;
    private static boolean SPEECH_ENABLED = true;
    private static long STABILITY_TIME_OFFSET = 500;
    private static final long UPDATE_OFFSET = 3000;
    
    private long lastUpdate = 0;
    // private static long[] VIBE_PATTERN = { 0, 500, 0 };

    private enum ClockDirection {
        ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10), ELEVEN(11), TWELVE(
                12);

        private final int value;

        ClockDirection(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }

        public static ClockDirection valueOf(int value) {
            if (value == 0) {
                return ClockDirection.TWELVE;
            }
            for (ClockDirection clockDirection : values()) {
                if (clockDirection.value() == value) {
                    Log.d("ONTRACK", "Setting the direction to be " + clockDirection.value());
                    return clockDirection;
                }
            }
            throw new RuntimeException("Invalid value " + value);
        }
    }

    private Vibrator vibe;
    private TextToSpeech tts;
    private Button setButton;
    private boolean onCourse;
    private int stabilityCount = 0;
    private long lastSpokenUpdate = 0;
    private long lastStableHeadingTime = 0;
    private boolean newDirection;
    private float lastStableHeading = 0;
    private float currentHeading;
    private float direction;
    private boolean trackingDirection;
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
        tts = new TextToSpeech(getApplicationContext(), null);
        setButton = (Button) findViewById(R.id.btn_set_compass);
        setButton.setEnabled(false);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (savedInstanceState != null) {
            direction = savedInstanceState.getFloat("direction");
            trackingDirection = savedInstanceState.getBoolean("trackingDirection");
            onCourse = savedInstanceState.getBoolean("onCourse");
            if (!onCourse) {
                // vibe.vibrate(VIBE_PATTERN, 0);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("trackingDirection", trackingDirection);
        savedInstanceState.putBoolean("onCourse", onCourse);
        savedInstanceState.putFloat("direction", direction);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        vibe.cancel();
        tts.shutdown();
        sensorManager.unregisterListener(mListener);
        super.onDestroy();
    }

    public void handleDirection() {
        boolean reliableReading = false;
        if (Math.abs(currentHeading - lastStableHeading) < STABILITY_MARGIN) {
            if (lastStableHeadingTime + STABILITY_TIME_OFFSET > System.currentTimeMillis()) {
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

    public void processDirection() {
        if (Math.abs(currentHeading - lastStableHeading) <= STABILITY_MARGIN) {
            ++stabilityCount; // The stability reading increases each time
        } else { // reset reading - unstable
            lastStableHeading = currentHeading; // Reset for new readings
            stabilityCount = 0;
            newDirection = true;
            if (trackingDirection && onCourse && Math.abs(currentHeading - direction) > COURSE_MARGIN) {
                // moved off course
                onCourse = false;
                // vibe.vibrate(VIBE_PATTERN, 0);
            }
        }

        // Check that stability is stable and that we have a new direction i.e.
        // don't want to keep speaking stuff if the compass is remaining still
        if (newDirection && stabilityCount >= MINIMUM_STABILITY_COUNT) {
            newDirection = false;
            Log.d("COMPASS", "heading is: " + currentHeading);
            setButton.setEnabled(true);
            setButton.setText("Set " + Math.round(currentHeading) + " degrees");
            if (trackingDirection && Math.abs(currentHeading - direction) > COURSE_MARGIN) {
                // stable reading - we are still off course
                directBackOnCourse();
            } else if (trackingDirection && !onCourse
                    && Math.abs(currentHeading - direction) <= COURSE_MARGIN) {
                // We have just moved back on course
                onCourse = true;
                vibe.cancel();
                Log.d("COMPASS", "Back on course!");
                tts.speak("Back on course!", 0, null);
                lastSpokenUpdate = 0;
            }
        }
    }

    public void setDirection(View view) {
        this.direction = currentHeading;
        this.trackingDirection = true;
        onCourse = true;
        String message = "Set your direction to: " + Math.round(currentHeading) + " degrees";
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        vibe.cancel();
    }

    private void directBackOnCourse() {
        long currentTime = System.currentTimeMillis();
        if (!SPEECH_ENABLED || lastSpokenUpdate + SPOKEN_TIME_OFFSET > currentTime) {
            return; // Don't want to constantly speak wait some time since last
                    // utterance
        }
        lastSpokenUpdate = currentTime;
        int toHead = getDegreeOffCourse();
        // vibrateOffCourse(getClockDirectionToHeading());
        // values < 0 are left of the direction we want to go values > 0 are to
        // the right of direction we want to go
        /*
         * if (toHead < 0) { tts.speak("Go left " + Math.abs(toHead) +
         * " degrees", 0, null); } else { tts.speak("Go right " + toHead +
         * " degrees", 0, null); }
         */
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

    private ClockDirection getClockDirectionToHeading() {
        int direction = Math.round((this.direction - currentHeading) / 30);
        if (direction < 0) {
            direction = 12 - Math.abs(direction);
        }
        return ClockDirection.valueOf(direction);
    }

    private void vibrateOffCourse() {
        if (System.currentTimeMillis() < lastUpdate + UPDATE_OFFSET) {
            return;
        }
        lastUpdate = System.currentTimeMillis();
        int offCourse = getDegreeOffCourse();
        long duration = 10 * Math.abs(offCourse);
        vibe.cancel ();
        if (offCourse < 0) {
            long[] pattern = {0, 300, 300, duration, 0};
            vibe.vibrate(pattern, -1);
        } else {
            long[] pattern = {0, duration, 0};
            vibe.vibrate(pattern, -1);
        }
    }

    private int buildPattern(long[] pattern, int start, long startDelay, long on, long off, int repeats) {
        pattern[start] = startDelay;
        for (int i = start + 1; i < start + 2 * repeats; i += 2) {
            pattern[i] = on;
            pattern[i + 1] = off;
        }
        StringBuilder sb = new StringBuilder("{");
        for (long p : pattern) {
            sb.append(String.valueOf(p)).append(",");
        }
        Log.d("ONTRACK", sb.substring(0, sb.length() - 1) + "}");
        return start + 2 * repeats;
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
