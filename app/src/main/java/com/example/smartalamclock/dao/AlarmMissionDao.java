package com.example.smartalamclock.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import com.example.smartalamclock.entity.AlarmMission;
import java.util.List;

@Dao
public interface AlarmMissionDao {
    @Insert
    long insert(AlarmMission alarmMission);

    @Update
    void update(AlarmMission alarmMission);

    @Delete
    void delete(AlarmMission alarmMission);

    @Query("SELECT * FROM alarm_mission")
    List<AlarmMission> getAll();

    @Query("SELECT * FROM alarm_mission WHERE alarmId = :alarmId")
    List<AlarmMission> getByAlarmId(int alarmId);

    @Query("SELECT * FROM alarm_mission WHERE missionId = :missionId")
    List<AlarmMission> getByMissionId(int missionId);

    @Query("SELECT missionId FROM alarm_mission WHERE alarmId = :alarmId")
    List<Long> getMissionIdsForAlarm(int alarmId);
}
