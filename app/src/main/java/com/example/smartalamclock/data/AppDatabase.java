package com.example.smartalamclock.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.example.smartalamclock.entity.Alarm;
import com.example.smartalamclock.entity.Mission;
import com.example.smartalamclock.entity.AlarmMission;
import com.example.smartalamclock.dao.AlarmDao;
import com.example.smartalamclock.dao.MissionDao;
import com.example.smartalamclock.dao.AlarmMissionDao;
import com.example.smartalamclock.mission.MissionType;

@Database(
        entities = {Alarm.class, Mission.class, AlarmMission.class},
        version = 3, // Tăng version lên 3
        exportSchema = false)
@TypeConverters(AppDatabase.MissionTypeConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract AlarmDao alarmDao();
    public abstract MissionDao missionDao();
    public abstract AlarmMissionDao alarmMissionDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "smartalarm_db")
                            .fallbackToDestructiveMigration() // Cho phép xóa và tạo lại DB
                            .allowMainThreadQueries() // Tạm thời cho phép query trên main thread
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static class MissionTypeConverter {
        @TypeConverter
        public static MissionType toMissionType(String value) {
            return value == null ? null : MissionType.valueOf(value);
        }

        @TypeConverter
        public static String fromMissionType(MissionType type) {
            return type == null ? null : type.name();
        }
    }
}