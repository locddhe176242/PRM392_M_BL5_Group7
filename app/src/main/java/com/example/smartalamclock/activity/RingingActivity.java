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

import com.example.smartalamclock.R;
import com.example.smartalamclock.alarm.AlarmSoundService;
import com.example.smartalamclock.entity.Mission;
import com.example.smartalamclock.mission.MissionCompletionListener;
import com.example.smartalamclock.repository.AlarmRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RingingActivity extends AppCompatActivity implements MissionCompletionListener {
    private static final String TAG = "RingingActivity";

    private TextView tvTime;
    private Button btnStop;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private int alarmId = -1;
    private AlarmRepository alarmRepository;

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
        alarmRepository = new AlarmRepository(getApplication());

        if (btnStop == null) {
            Log.e(TAG, "btnStop is null — check activity_ringing.xml for @+id/btnStop and that this layout is used.");
        } else {
            btnStop.setOnClickListener(v -> {
                try {
                    Intent serviceIntent = new Intent(this, AlarmSoundService.class);
                    stopService(serviceIntent);
                    Toast.makeText(this, "Báo thức đã dừng.", Toast.LENGTH_SHORT).show();
                    alarmRepository.dismissAlarm(alarmId);
                    finish();
                } catch (Exception e) {
                    Log.e(TAG, "Error stopping alarm on btnStop", e);
                }
            });

            btnStop.setOnLongClickListener(v -> {
                Toast.makeText(this, "Long press: mở nhiệm vụ (chưa cài).", Toast.LENGTH_SHORT).show();
                return true;
            });
        }

        alarmId = getIntent() != null ? getIntent().getIntExtra("ALARM_ID", -1) : -1;
        updateTimeDisplay();
        Log.d(TAG, "onCreate alarmId=" + alarmId);
        if (alarmId == -1) {
            Log.w(TAG, "No alarmId received; continuing without finishing (music controlled by service)");
        }

        startAlarmSound();
        loadMissions();
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


    private void loadMissions() {
        alarmRepository.getMissionsForAlarm(alarmId, new AlarmRepository.MissionCallback() {
            @Override
            public void onMissionsLoaded(List<Mission> missions) {
                if (missions == null || missions.isEmpty()) {
                    Log.w(TAG, "No missions for alarmId=" + alarmId + " — keeping activity visible and service running");
                    runOnUiThread(() -> Toast.makeText(RingingActivity.this, "Không có nhiệm vụ cho báo thức này.", Toast.LENGTH_LONG).show());
                    return;
                }
                Random r = new Random();
                Mission selected = missions.get(r.nextInt(missions.size()));
                Log.d(TAG, "Selected mission: " + selected.getType());
                loadMissionFragment(selected);
            }
        });
    }

    private void loadMissionFragment(Mission mission) {
        if (mission == null) return;
        try {
            androidx.fragment.app.Fragment fragment = null;
            switch (mission.getType()) {
                case MATH:
                    fragment = new com.example.smartalamclock.fragment.MathMissionFragment();
                    break;
                case SHAKE:
                    fragment = new com.example.smartalamclock.fragment.ShakeMissionFragment();
                    break;
                case TIC_TAC_TOE:
                    fragment = new com.example.smartalamclock.fragment.TicTacToeMissionFragment();
                    break;
                default:
                    Log.w(TAG, "Unknown mission type: " + mission.getType());
            }

            if (fragment != null) {
                Bundle args = new Bundle();
                args.putInt("MISSION_ID", (int) mission.getMissionId());
                fragment.setArguments(args);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mission_container, fragment)
                        .commitAllowingStateLoss();
                Log.d(TAG, "Mission fragment loaded: " + mission.getType());
            } else {
                Log.w(TAG, "No fragment for mission, showing notice and keeping music playing");
                runOnUiThread(() -> Toast.makeText(RingingActivity.this, "Không thể tải nhiệm vụ.", Toast.LENGTH_SHORT).show());
            }
        } catch (Exception e) {
            Log.e(TAG, "loadMissionFragment error", e);
            runOnUiThread(() -> Toast.makeText(RingingActivity.this, "Lỗi khi tải nhiệm vụ.", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onMissionCompleted() {
        alarmRepository.dismissAlarm(alarmId);
        Log.d(TAG, "Mission completed for alarmId=" + alarmId + " — alarm dismissed in DB, music still playing until user stops it.");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy (activity) — NOT stopping service");
    }
}