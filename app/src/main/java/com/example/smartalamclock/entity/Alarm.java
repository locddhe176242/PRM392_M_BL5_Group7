package com.example.smartalamclock.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Calendar;
import java.util.StringTokenizer;

@Entity(tableName = "alarms")
public class Alarm implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String time;
    private boolean enabled; // Whether the alarm is scheduled
    private boolean active; // Whether the alarm is currently ringing or active
    private String label;
    private String repeatDays; // e.g., "1010100" for Mon-Wed-Fri (0 = off, 1 = on)
    private String ringtone;
    private boolean vibrate;

    public Alarm() {
    }

    public Alarm(int id, String time, boolean enabled, boolean active, String label, String repeatDays, String ringtone, boolean vibrate) {
        this.id = id;
        this.time = time;
        this.enabled = enabled;
        this.active = active;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
        calendar.setTimeInMillis(System.currentTimeMillis()); // Set to current time
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Check if the time has passed today
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            if (repeatDays != null && !repeatDays.isEmpty()) {
                // Handle repeat days (e.g., "1010100" for Mon-Wed-Fri)
                int currentDay = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 0 = Sunday, 6 = Saturday
                if (currentDay < 0) currentDay = 6; // Adjust for Sunday
                StringTokenizer tokenizer = new StringTokenizer(repeatDays, "");
                int daysToAdd = 0;

                while (tokenizer.hasMoreTokens()) {
                    String dayBit = tokenizer.nextToken();
                    if (currentDay >= repeatDays.length()) currentDay = 0; // Loop back to start
                    if ("1".equals(dayBit)) {
                        if (currentDay == calendar.get(Calendar.DAY_OF_WEEK) - 1) {
                            break; // Found today's repeat day
                        }
                    }
                    currentDay = (currentDay + 1) % 7;
                    daysToAdd++;
                }

                calendar.add(Calendar.DAY_OF_YEAR, daysToAdd);
            } else {
                // No repeat, set for tomorrow
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
        }

        return calendar.getTimeInMillis();
    }
}