package com.example.smartalamclock.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartalamclock.R;
import com.example.smartalamclock.alarm.AlarmSoundService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.os.Handler;
import android.os.Looper;

public class RingingActivity extends AppCompatActivity {
    private TextView tvTime;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Thêm flags cho window TRƯỚC khi setContentView
        getWindow().addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );

        setContentView(R.layout.activity_ringing);

        tvTime = findViewById(R.id.tvTime);
        updateTime(); // Cập nhật thời gian ngay lập tức

        // Cập nhật thời gian mỗi phút
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateTime();
                handler.postDelayed(this, 60000); // 60 giây
            }
        }, 60000);

        // Bắt đầu phát nhạc
        startAlarmSound();

        // Thêm nút tắt báo thức
        Button btnDismiss = findViewById(R.id.btnDismiss);
        btnDismiss.setOnClickListener(v -> {
            // Dừng nhạc
            stopService(new Intent(this, AlarmSoundService.class));
            // Đóng activity
            finish();
        });
    }

    private void updateTime() {
        tvTime.setText(timeFormat.format(new Date()));
    }

    private void startAlarmSound() {
        Intent serviceIntent = new Intent(this, AlarmSoundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // Dừng cập nhật thời gian
        stopService(new Intent(this, AlarmSoundService.class));
    }
}