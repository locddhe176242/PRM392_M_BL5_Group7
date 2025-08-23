package com.example.smartalamclock.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.example.smartalamclock.mission.MissionType;

@Entity(tableName = "mission")
public class Mission {
    @PrimaryKey(autoGenerate = true)
    private long missionId;

    private MissionType type;
    private String data;

    private boolean completed;

    public Mission(MissionType type, String data) {
        this.type = type;
        this.data = data;
        this.completed = false;
    }

    public long getMissionId() { return missionId; }
    public void setMissionId(long missionId) { this.missionId = missionId; }

    public MissionType getType() { return type; }
    public void setType(MissionType type) { this.type = type; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}