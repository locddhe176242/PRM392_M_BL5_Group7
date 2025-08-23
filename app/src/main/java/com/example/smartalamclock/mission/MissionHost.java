package com.example.smartalamclock.mission;

public interface MissionHost {
    void onMissionProgress(int progress);
    void onMissionCompleted();
    void onMissionFailed(String reason);
}