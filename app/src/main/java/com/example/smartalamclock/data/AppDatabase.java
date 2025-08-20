package com.example.smartalamclock.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.smartalamclock.dao.MissionDao;
import com.example.smartalamclock.entity.Alarm;
import com.example.smartalamclock.entity.AlarmMission;
import com.example.smartalamclock.entity.Mission;

@Database(entities = {Alarm.class, Mission.class, AlarmMission.class}, version = 1)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract MissionDao missionDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "alarm_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
