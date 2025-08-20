package com.example.smartalamclock.data;

import androidx.room.TypeConverter;

import com.example.smartalamclock.mission.MissionType;

public class Converters {
    @TypeConverter
    public static String fromMissionType(MissionType missionType) {
        return missionType == null ? null : missionType.name();
    }

    @TypeConverter
    public static MissionType toMissionType(String value) {
        try {
            return value == null ? null : MissionType.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null; // Or default to a specific MissionType, e.g., MissionType.MATH
        }
    }
}
