package com.example.smartalamclock.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.smartalamclock.activity.RingingActivity;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long alarmId = intent.getLongExtra("ALARM_ID", -1);
        if (alarmId == -1) return;

        Intent activityIntent = new Intent(context, RingingActivity.class);
        activityIntent.putExtra("ALARM_ID", alarmId);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(activityIntent);

        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(alarmId);
        notificationHelper.getManager().notify((int) alarmId, nb.build());

        Intent serviceIntent = new Intent(context, AlarmSoundService.class);
        serviceIntent.putExtra("ALARM_ID", alarmId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }

}