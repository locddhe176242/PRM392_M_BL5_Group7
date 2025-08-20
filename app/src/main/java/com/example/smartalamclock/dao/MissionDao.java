package com.example.smartalamclock.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.smartalamclock.entity.Mission;

import java.util.List;

@Dao
public interface MissionDao {
    @Query("SELECT m.* FROM mission m " +
            "INNER JOIN alarm_mission am ON m.missionId = am.missionId " +
            "WHERE am.alarmId = :alarmId")
    List<Mission> getMissionsForAlarm(int alarmId);

    @Query("UPDATE mission SET completed = :completed WHERE missionId = :missionId")
    void updateMissionCompleted(int missionId, boolean completed);
}