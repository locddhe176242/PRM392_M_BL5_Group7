package com.example.smartalamclock.entity;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "alarmmission")
public class AlarmMission implements Serializable{
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int alarmId;
    private int missionId;
    public AlarmMission() {
    }
    public AlarmMission(int id, int alarmId, int missionId) {
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

    public int getMissionId() {
        return missionId;
    }

    public void setMissionId(int missionId) {
        this.missionId = missionId;
    }
}