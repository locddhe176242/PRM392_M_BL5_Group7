package com.example.smartalamclock.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "alarm")
public class Alarm {
    @PrimaryKey(autoGenerate = true)
    private int alarmId;

}
