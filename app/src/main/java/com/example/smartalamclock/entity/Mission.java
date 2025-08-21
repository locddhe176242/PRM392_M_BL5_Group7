package com.example.smartalamclock.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "mission")
public class Mission implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String type;
    private String description;
    private int difficulty;

    public Mission() {
    }

    public Mission(int id, String type, String description, int difficulty) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.difficulty = difficulty;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}