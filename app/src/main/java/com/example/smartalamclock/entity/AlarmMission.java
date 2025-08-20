package com.example.smartalamclock.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "alarm_mission",
        foreignKeys = {
                @ForeignKey(entity = Alarm.class,
                        parentColumns = "alarmId",
                        childColumns = "alarmId",
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
    private int missionId;

    public AlarmMission(int alarmId, int missionId) {
        this.alarmId = alarmId;
        this.missionId = missionId;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public int getAlarmId() {return alarmId;}
    public void setAlarmId(int alarmId) {this.alarmId = alarmId;}

    public int getMissionId() {return missionId;}
    public void setMissionId(int missionId) {this.missionId = missionId;}
}
