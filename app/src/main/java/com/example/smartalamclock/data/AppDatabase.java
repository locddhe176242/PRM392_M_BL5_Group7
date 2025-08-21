package com.example.smartalamclock.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.smartalamclock.entity.Alarm;
import com.example.smartalamclock.entity.Mission;
import com.example.smartalamclock.entity.AlarmMission;
import com.example.smartalamclock.dao.AlarmDao;
import com.example.smartalamclock.dao.MissionDao;

@Database(
        entities = {Alarm.class, Mission.class, AlarmMission.class},
        version = 1
)

public abstract class AppDatabase extends RoomDatabase {
    public abstract AlarmDao alarmDao();
    public abstract MissionDao missionDao();
}
