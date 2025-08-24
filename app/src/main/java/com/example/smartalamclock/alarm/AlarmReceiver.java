package com.example.smartalamclock.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive intent=" + intent);
        if (intent == null) return;

        int alarmId = intent.getIntExtra("ALARM_ID", -1);
        Log.d(TAG, "onReceive alarmId=" + alarmId);
        if (alarmId == -1) {
            Log.w(TAG, "onReceive: invalid alarmId");
            return;
        }

        try {
            Intent serviceIntent = new Intent(context, AlarmSoundService.class);
            serviceIntent.putExtra("ALARM_ID", alarmId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
            Log.d(TAG, "Requested AlarmSoundService start for alarmId=" + alarmId);
        } catch (Exception e) {
            Log.e(TAG, "Failed to start AlarmSoundService", e);
        }

    }
}