package com.example.smartalamclock.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "mission")
public class Mission {
    @PrimaryKey(autoGenerate = true)
    private int missionId;

    private String type;
    private String data;

    private boolean completed;

    public Mission(String type, String data) {
        this.type = type;
        this.data = data;
        this.completed = false;
    }

    public int getMissionId() {return missionId;}
    public void setMissionId(int missionId) {this.missionId = missionId;}

    public String getType() {return type;}
    public void setType(String type) {this.type = type;}

    public String getData() {return data;}
    public void setData(String data) {this.data = data;}

    public boolean isCompleted() {return completed;}
    public void setCompleted(boolean completed) {this.completed = completed;}
}
