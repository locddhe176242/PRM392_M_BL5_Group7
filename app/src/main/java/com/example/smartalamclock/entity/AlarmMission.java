package com.example.smartalamclock.entity;


import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "alarm_mission",
        foreignKeys = {
                @ForeignKey(entity = Alarm.class,
                        parentColumns = "id",
                        childColumns = "id",
                        onDelete = CASCADE),
                @ForeignKey(entity = Mission.class,
                        parentColumns = "missionId",
                        childColumns = "missionId",
                        onDelete = CASCADE)
        })
public class AlarmMission {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int alarmId;
    private long missionId;

    public AlarmMission() {
    }
    public AlarmMission(int id, int alarmId, long missionId) {
        this.id = id;
        this.alarmId = alarmId;
        this.missionId = missionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public long getMissionId() {
        return missionId;
    }

    public void setMissionId(int missionId) {
        this.missionId = missionId;
    }
}