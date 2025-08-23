package com.example.smartalamclock.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import com.example.smartalamclock.entity.Alarm;
import java.util.List;

@Dao
public interface AlarmDao {
    @Insert
    long insert(Alarm alarm);

    @Update
    void update(Alarm alarm);

    @Delete
    void delete(Alarm alarm);

    @Query("SELECT * FROM alarms")
    List<Alarm> getAll();

    @Query("SELECT * FROM alarms WHERE id = :id LIMIT 1")
    Alarm getById(int id);

    @Query("SELECT * FROM alarms WHERE id = :id LIMIT 1")
    Alarm getAlarmById(int id);

    @Query("SELECT * FROM alarms WHERE enabled = 1 AND active = 1")
    List<Alarm> getActiveAlarms();

    @Query("UPDATE alarms SET active = 0 WHERE id = :id")
    void dismissAlarm(int id);
}