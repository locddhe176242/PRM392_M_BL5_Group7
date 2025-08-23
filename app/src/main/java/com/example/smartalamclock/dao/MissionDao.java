package com.example.smartalamclock.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.smartalamclock.entity.Mission;
import java.util.List;

@Dao
public interface MissionDao {
    @Insert
    long insert(Mission mission);

    @Query("SELECT * FROM mission WHERE missionId IN (:missionIds)")
    List<Mission> getMissionsByIds(List<Long> missionIds);

    @Query("UPDATE mission SET completed = :completed WHERE missionId = :missionId")
    void updateMissionCompleted(long missionId, boolean completed);
}