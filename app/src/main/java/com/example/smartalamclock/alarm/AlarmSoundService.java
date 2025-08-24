package com.example.smartalamclock.alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.smartalamclock.R;
import com.example.smartalamclock.activity.RingingActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AlarmSoundService extends Service {
    private static final String TAG = "AlarmSoundService";
    private static final String CHANNEL_ID = "alarm_channel";
    private static final int NOTIFICATION_ID = 1001;

    private MediaPlayer mediaPlayer;
    private final Random random = new Random();
    private List<Integer> trackRes = null;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand intent=" + intent);
        int alarmId = -1;
        if (intent != null) alarmId = intent.getIntExtra("ALARM_ID", -1);
        Intent contentIntent = new Intent(this, RingingActivity.class);
        contentIntent.putExtra("ALARM_ID", alarmId);
        contentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(
                this,
                alarmId == -1 ? 0 : alarmId,
                contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder nb = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Báo thức đang kêu")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setContentIntent(contentPendingIntent);

        Notification notification = nb.build();

        try {
            startForeground(NOTIFICATION_ID, notification);
            Log.d(TAG, "startForeground done");
        } catch (Exception e) {
            Log.e(TAG, "startForeground failed", e);
        }

        // --- Random track playback logic ---
        // Lazily build list of raw resource IDs (reflection) if you don't want to hardcode filenames.
        if (trackRes == null) {
            trackRes = loadAllRawTracks();
        }

        // Start playing random sequence (will keep selecting next when current completes)
        playRandomTrack();

        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm == null) {
                Log.w(TAG, "NotificationManager is null");
                return;
            }
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alarm channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for alarm foreground service");
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setSound(null, null);
            channel.setBypassDnd(true);
            nm.createNotificationChannel(channel);
            Log.d(TAG, "Notification channel created");
        }
    }

    private List<Integer> loadAllRawTracks() {
        List<Integer> list = new ArrayList<>();
        try {
            // reflect R.raw to get all raw resource ids
            Class<?> rawClass = com.example.smartalamclock.R.raw.class;
            Field[] fields = rawClass.getFields();
            for (Field f : fields) {
                try {
                    int resId = f.getInt(null);
                    list.add(resId);
                } catch (Exception ignore) { }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to load raw resources via reflection", e);
        }

        // Optionally: if you prefer to use an explicit list, replace above with:
        // list = Arrays.asList(R.raw.track1, R.raw.track2, R.raw.track3);

        if (list.isEmpty()) {
            Log.w(TAG, "No tracks found in res/raw. Add audio files or hardcode list.");
        } else {
            Log.d(TAG, "Loaded " + list.size() + " tracks for random playback");
        }
        return list;
    }

    private void playRandomTrack() {
        if (trackRes == null || trackRes.isEmpty()) return;

        int idx = random.nextInt(trackRes.size());
        int resId = trackRes.get(idx);

        try {
            // release old player if exists
            if (mediaPlayer != null) {
                try { mediaPlayer.reset(); } catch (Exception ignored) {}
                try { mediaPlayer.release(); } catch (Exception ignored) {}
                mediaPlayer = null;
            }

            mediaPlayer = MediaPlayer.create(this, resId);
            if (mediaPlayer == null) {
                Log.e(TAG, "MediaPlayer.create returned null for resId=" + resId);
                return;
            }

            mediaPlayer.setLooping(false); // false so we can pick a new random track on completion
            mediaPlayer.setOnCompletionListener(mp -> {
                // choose next random track when current finishes
                playRandomTrack();
            });
            mediaPlayer.start();
            Log.d(TAG, "Playing random track resId=" + resId);
        } catch (Exception e) {
            Log.e(TAG, "Error playing random track", e);
        }
    }

    private void stopAndReleasePlayer() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error stopping/releasing mediaPlayer", e);
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        stopAndReleasePlayer();
        try { stopForeground(true); } catch (Exception ignored) {}
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}