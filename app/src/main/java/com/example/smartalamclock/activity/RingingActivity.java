package com.example.smartalamclock.activity;

import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.smartalamclock.R;
import com.example.smartalamclock.alarm.AlarmSoundService;
import com.example.smartalamclock.mission.MissionHost;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class RingingActivity extends AppCompatActivity implements MissionHost {
    private static final String TAG = "RingingActivity";

    private TextView tvTime;
    private Button btnStop;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private int alarmId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            final KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (km != null && km.isKeyguardLocked()) {
                km.requestDismissKeyguard(this, null);
            }
        }

        setContentView(R.layout.activity_ringing);

        tvTime = findViewById(R.id.tvTime);
        btnStop = findViewById(R.id.btnStop);

        if (btnStop == null) {
            Log.e(TAG, "btnStop is null — check activity_ringing.xml for @+id/btnStop");
        } else {
            // Ban đầu ẩn đi, chỉ hiện khi mission hoàn thành
            btnStop.setVisibility(Button.GONE);
            btnStop.setOnClickListener(v -> stopAlarm());
        }

        alarmId = getIntent() != null ? getIntent().getIntExtra("ALARM_ID", -1) : -1;
        updateTimeDisplay();
        Log.d(TAG, "onCreate alarmId=" + alarmId);

        startAlarmSound();
        loadRandomMission(); // random đúng 1 mission khi mở activity
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        int newAlarmId = intent != null ? intent.getIntExtra("ALARM_ID", -1) : -1;
        if (newAlarmId != -1) {
            alarmId = newAlarmId;
            updateTimeDisplay();
            Log.d(TAG, "onNewIntent updated alarmId=" + alarmId);
        }
    }

    private void updateTimeDisplay() {
        if (tvTime != null) tvTime.setText(timeFormat.format(new Date()));
    }

    private void startAlarmSound() {
        Intent serviceIntent = new Intent(this, AlarmSoundService.class);
        serviceIntent.putExtra("ALARM_ID", alarmId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        Log.d(TAG, "Requested AlarmSoundService start");
    }

    /** Random và load fragment nhiệm vụ duy nhất */
    private void loadRandomMission() {
        try {
            Fragment fragment = null;
            int randomIndex = new Random().nextInt(3); // 0,1,2

            switch (randomIndex) {
                case 0:
                    fragment = new com.example.smartalamclock.fragment.MathMissionFragment();
                    Log.d(TAG, "Random mission: Math");
                    break;
                case 1:
                    fragment = new com.example.smartalamclock.fragment.ShakeMissionFragment();
                    Log.d(TAG, "Random mission: Shake");
                    break;
                case 2:
                    fragment = new com.example.smartalamclock.fragment.TicTacToeMissionFragment();
                    Log.d(TAG, "Random mission: TicTacToe");
                    break;
            }

            if (fragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mission_container, fragment)
                        .commitAllowingStateLoss();
            } else {
                Toast.makeText(this, "Can not load random mission.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "loadRandomMission error", e);
            Toast.makeText(this, "Have error when loading random mission.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMissionProgress(int progress) {
        runOnUiThread(() -> {
            Log.d(TAG, "Mission progress: " + progress + "%");
        });
    }

    @Override
    public void onMissionCompleted() {
        runOnUiThread(() -> {
            btnStop.setVisibility(Button.VISIBLE);
            Toast.makeText(this, "Mission Completed! Click to stop.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onMissionFailed(String reason) {
        runOnUiThread(() -> {
            Toast.makeText(this, "Mission Failed: " + reason, Toast.LENGTH_SHORT).show();
        });
    }

    private void stopAlarm() {
        try {
            // Dừng nhạc chuông
            stopService(new Intent(this, AlarmSoundService.class));

            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(getSupportFragmentManager().findFragmentById(R.id.mission_container))
                    .commitAllowingStateLoss();

            Toast.makeText(this, "Alarm stopped", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error stopping alarm", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy (activity)");
    }
}