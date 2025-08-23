package com.example.smartalamclock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartalamclock.R;
import com.example.smartalamclock.data.AppDatabase;
import com.example.smartalamclock.entity.Alarm;
import com.example.smartalamclock.alarm.AlarmSchedulder;

import java.util.Calendar;
import java.util.concurrent.Executors;

public class CreateAlarmActivity extends AppCompatActivity {
    private TimePicker timePicker;
    private EditText etLabel;
    private Button btnSave, btnCancel, btnDelete;
    private AppDatabase db;
    private Alarm existingAlarm; // Để lưu báo thức đang sửa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alarm);

        // Ánh xạ views
        timePicker = findViewById(R.id.timePicker);
        etLabel = findViewById(R.id.etLabel);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnDelete = findViewById(R.id.btnDelete);

        db = androidx.room.Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "alarm-db"
        ).build();

        // Kiểm tra có phải đang sửa báo thức không
        Intent intent = getIntent();
        if (intent.hasExtra("alarm_id")) {
            int alarmId = intent.getIntExtra("alarm_id", -1);
            loadExistingAlarm(alarmId);
        } else {
            btnDelete.setVisibility(View.GONE);
        }

        btnSave.setOnClickListener(v -> saveAlarm());
        btnCancel.setOnClickListener(v -> finish());
        btnDelete.setOnClickListener(v -> deleteAlarm());
    }

    private void loadExistingAlarm(int alarmId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            existingAlarm = db.alarmDao().getById(alarmId);
            if (existingAlarm != null) {
                runOnUiThread(() -> {
                    // Hiển thị dữ liệu cũ
                    String[] timeParts = existingAlarm.getTime().split(":");
                    timePicker.setHour(Integer.parseInt(timeParts[0]));
                    timePicker.setMinute(Integer.parseInt(timeParts[1]));
                    etLabel.setText(existingAlarm.getLabel());
                    btnDelete.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    private void saveAlarm() {
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        String label = etLabel.getText().toString();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Nếu thời gian đã qua, đặt cho ngày mai
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            if (existingAlarm != null) {
                // Cập nhật báo thức cũ
                AlarmSchedulder.cancelAlarm(this, existingAlarm.getId()); // Hủy báo thức cũ
                existingAlarm.setTime(String.format("%02d:%02d", hour, minute));
                existingAlarm.setLabel(label);
                db.alarmDao().update(existingAlarm);
                AlarmSchedulder.scheduleAlarm(this, calendar.getTimeInMillis(), existingAlarm.getId());
            } else {
                // Tạo báo thức mới
                Alarm alarm = new Alarm();
                alarm.setTime(String.format("%02d:%02d", hour, minute));
                alarm.setLabel(label);
                alarm.setEnabled(true);
                long id = db.alarmDao().insert(alarm);
                AlarmSchedulder.scheduleAlarm(this, calendar.getTimeInMillis(), (int)id);
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Đã lưu báo thức!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void deleteAlarm() {
        if (existingAlarm != null) {
            Executors.newSingleThreadExecutor().execute(() -> {
                AlarmSchedulder.cancelAlarm(this, existingAlarm.getId());
                db.alarmDao().delete(existingAlarm);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Đã xóa báo thức!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        }
    }
}