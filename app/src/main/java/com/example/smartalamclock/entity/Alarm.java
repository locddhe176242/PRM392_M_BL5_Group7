package com.example.smartalamclock.entity;

import static java.lang.System.currentTimeMillis;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Calendar;

@Entity(tableName = "alarms")
public class Alarm implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String time;
    private boolean enabled;
    private String label;
    private String repeatDays;
    private String ringtone;
    private boolean vibrate;

    public Alarm() {
    }

    public Alarm(int id, String time, boolean enabled, String label, String repeatDays, String ringtone, boolean vibrate) {
        this.id = id;
        this.time = time;
        this.enabled = enabled;
        this.label = label;
        this.repeatDays = repeatDays;
        this.ringtone = ringtone;
        this.vibrate = vibrate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getRepeatDays() {
        return repeatDays;
    }

    public void setRepeatDays(String repeatDays) {
        this.repeatDays = repeatDays;
    }

    public String getRingtone() {
        return ringtone;
    }

    public void setRingtone(String ringtone) {
        this.ringtone = ringtone;
    }

    public boolean isVibrate() {
        return vibrate;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public long getTimeInMillis() {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Nếu thời gian đã qua, đặt cho ngày mai
        if (calendar.getTimeInMillis() <= currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return calendar.getTimeInMillis();
    }
}

