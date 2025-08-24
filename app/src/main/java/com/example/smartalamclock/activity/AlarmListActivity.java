package com.example.smartalamclock.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartalamclock.R;
import com.example.smartalamclock.adapter.AlarmAdapter;
import com.example.smartalamclock.data.AppDatabase;
import com.example.smartalamclock.entity.Alarm;
import com.example.smartalamclock.alarm.AlarmSchedulder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.ArrayList;

public class AlarmListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AlarmAdapter adapter;
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(); 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }

        db = androidx.room.Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "alarm-db"
        ).build();

        recyclerView = findViewById(R.id.recyclerViewAlarms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AlarmAdapter(new ArrayList<>(), new AlarmAdapter.OnAlarmClickListener() {
            @Override
            public void onAlarmClick(Alarm alarm) {
                Intent intent = new Intent(AlarmListActivity.this, CreateAlarmActivity.class);
                intent.putExtra("ALARM_ID", alarm.getId()); 
                startActivity(intent);
            }

            @Override
            public void onSwitchChanged(Alarm alarm, boolean enabled) {
                alarm.setEnabled(enabled);
                executor.execute(() -> { 
                    db.alarmDao().update(alarm);
                    if (enabled) {
                        runOnUiThread(() -> AlarmSchedulder.scheduleAlarm(
                            AlarmListActivity.this,
                            alarm.getTimeInMillis(),
                            alarm.getId()
                        ));
                    } else {
                        runOnUiThread(() -> AlarmSchedulder.cancelAlarm(
                            AlarmListActivity.this,
                            alarm.getId()
                        ));
                    }
                });
            }

            @Override
            public void onAlarmLongClick(Alarm alarm) {
                new AlertDialog.Builder(AlarmListActivity.this)
                    .setTitle("Xoá báo thức")
                    .setMessage("Bạn có chắc muốn xoá báo thức này?")
                    .setPositiveButton("Xoá", (dialog, which) -> {
                        executor.execute(() -> { 
                            db.alarmDao().delete(alarm);
                            runOnUiThread(() -> reloadAlarms());
                        });
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
            }
        });
        recyclerView.setAdapter(adapter);

        reloadAlarms();

        FloatingActionButton fab = findViewById(R.id.fabAddAlarm);
        fab.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateAlarmActivity.class));
        });
    }
    private void reloadAlarms() {
        executor.execute(() -> { 
            List<Alarm> alarms = db.alarmDao().getAll();
            runOnUiThread(() -> adapter.setAlarms(alarms));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadAlarms();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}