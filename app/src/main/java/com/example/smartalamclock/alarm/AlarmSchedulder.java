package com.example.smartalamclock.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

public class AlarmSchedulder {
    private static final String TAG = "AlarmSchedulder";

    public static void scheduleAlarm(Context context, long timeInMillis, int alarmId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Log.d(TAG, "scheduleAlarm id=" + alarmId + " time=" + timeInMillis);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("ALARM_ID", alarmId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                    Log.w(TAG, "App cannot schedule exact alarms. Requesting user permission and falling back to inexact alarm.");
                    try {
                        Intent req = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                        req.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(req);
                    } catch (Exception e) {
                        Log.w(TAG, "Failed to launch request exact alarm settings", e);
                    }
                    if (alarmManager != null) {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                    }
                    return;
                }
                if (alarmManager != null) {
                    alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(timeInMillis, pendingIntent), pendingIntent);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (alarmManager != null) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                }
            } else {
                if (alarmManager != null) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                }
            }
            Log.d(TAG, "Alarm scheduled (id=" + alarmId + ")");
        } catch (SecurityException se) {
            Log.e(TAG, "SecurityException scheduling exact alarm, falling back to inexact set()", se);
            if (alarmManager != null) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to schedule alarm", e);
        }
    }

    public static void cancelAlarm(Context context, int alarmId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("ALARM_ID", alarmId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
        pendingIntent.cancel();
        Log.d(TAG, "cancelAlarm id=" + alarmId);
    }
}