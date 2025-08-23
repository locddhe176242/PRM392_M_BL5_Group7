package com.example.smartalamclock.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.smartalamclock.R;
import com.example.smartalamclock.alarm.AlarmSoundService;
import com.example.smartalamclock.entity.Mission;
import com.example.smartalamclock.fragment.MathMissionFragment;
import com.example.smartalamclock.fragment.ShakeMissionFragment;
import com.example.smartalamclock.fragment.TicTacToeMissionFragment;
import com.example.smartalamclock.mission.MissionCompletionListener;
import com.example.smartalamclock.mission.MissionType;
import com.example.smartalamclock.repository.AlarmRepository;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RingingActivity extends AppCompatActivity implements MissionCompletionListener {

    private TextView tvTime;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private long alarmId;
    private AlarmRepository alarmRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );
        setContentView(R.layout.activity_ringing);

        tvTime = findViewById(R.id.tvTime);
        updateTime();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateTime();
                handler.postDelayed(this, 60000); // 60 seconds
            }
        }, 60000);

        requestAudioPermissions();

        // Initialize alarmId and repository first
        alarmId = getIntent().getLongExtra("ALARM_ID", -1);
        if (alarmId == -1) {
            finish(); // Invalid alarm, exit
            return;
        }
        alarmRepository = new AlarmRepository(getApplication());

        // Start alarm sound after initializing alarmId
        startAlarmSound();

        // Load missions
        loadMissions();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        alarmId = getIntent().getLongExtra("ALARM_ID", -1);
        Log.d("RingingActivity", "onNewIntent - Alarm ID: " + alarmId);
        if (alarmId != -1) {
            loadMissions();
        }
    }

    private void loadMissions() {
        alarmRepository.getMissionsForAlarm((int) alarmId, new AlarmRepository.MissionCallback() {
            @Override
            public void onMissionsLoaded(List<Mission> missions) {
                if (missions == null || missions.isEmpty()) {
                    stopAlarmSound();
                    finish();
                    return;
                }

                Random random = new Random();
                Mission selectedMission = missions.get(random.nextInt(missions.size()));
                Log.d("RingingActivity", "Loading mission: " + selectedMission.getType());
                loadMissionFragment(selectedMission);
            }
        });
    }

    private void updateTime() {
        tvTime.setText(timeFormat.format(new Date()));
    }

    private void startAlarmSound() {
        Intent serviceIntent = new Intent(this, AlarmSoundService.class);
        serviceIntent.putExtra("ALARM_ID", alarmId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    private void stopAlarmSound() {
        stopService(new Intent(this, AlarmSoundService.class));
    }

    private void loadMissionFragment(Mission mission) {
        Fragment fragment = null;
        MissionType type = mission.getType();

        switch (type) {
            case MATH:
                fragment = new MathMissionFragment();
                break;
            case SHAKE:
                fragment = new ShakeMissionFragment();
                break;
            case TIC_TAC_TOE:
                fragment = new TicTacToeMissionFragment();
                break;
            default:
                Log.e("RingingActivity", "Unknown mission type: " + type);
                return;
        }

        if (fragment != null) {
            Bundle args = new Bundle();
            args.putLong("MISSION_ID", mission.getMissionId());
            fragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.mission_container, fragment);
            transaction.commitAllowingStateLoss();
            Log.d("RingingActivity", "Fragment committed: " + type);
        }
    }


    private void requestAudioPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS}, 1);
            }
        }
    }

    @Override
    public void onMissionCompleted() {
        stopAlarmSound();
        alarmRepository.dismissAlarm(alarmId);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        stopAlarmSound();
    }
}