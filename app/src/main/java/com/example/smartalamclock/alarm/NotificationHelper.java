package com.example.smartalamclock.alarm;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.smartalamclock.R;
import com.example.smartalamclock.activity.RingingActivity;

public class NotificationHelper extends ContextWrapper {
    public static final String channelID = "alarm_channel";
    public static final String channelName = "Báo thức";
    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(
            channelID, 
            channelName, 
            NotificationManager.IMPORTANCE_HIGH
        );
        // Cấu hình cần thiết cho channel báo thức
        channel.setDescription("Kênh thông báo báo thức");
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification() {
        // Tạo Intent với action cụ thể
        Intent intent = new Intent(this, RingingActivity.class);
        intent.setAction("OPEN_RINGING_ACTIVITY");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Tạo PendingIntent với requestCode cố định
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this,
            0,  // Fixed request code
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle("Báo thức!")
                .setContentText("Đã đến giờ báo thức")
                .setSmallIcon(R.drawable.gold)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
                .setFullScreenIntent(pendingIntent, true);
    }
}

