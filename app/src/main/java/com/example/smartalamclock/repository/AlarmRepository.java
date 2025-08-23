package com.example.smartalamclock.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.example.smartalamclock.dao.AlarmDao;
import com.example.smartalamclock.dao.AlarmMissionDao;
import com.example.smartalamclock.dao.MissionDao;
import com.example.smartalamclock.data.AppDatabase;
import com.example.smartalamclock.entity.Alarm;
import com.example.smartalamclock.entity.Mission;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlarmRepository {

    private final MissionDao missionDao;
    private final AlarmDao alarmDao;
    private final AlarmMissionDao alarmMissionDao;
    private final ExecutorService executorService;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public AlarmRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        missionDao = db.missionDao();
        alarmDao = db.alarmDao();
        alarmMissionDao = db.alarmMissionDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void getMissionsForAlarm(int alarmId, MissionCallback callback) {
        executorService.execute(() -> {
            List<Long> missionIds = alarmMissionDao.getMissionIdsForAlarm(alarmId);
            List<Mission> missions = missionDao.getMissionsByIds(missionIds);
            mainHandler.post(() -> callback.onMissionsLoaded(missions));
        });
    }

    public void dismissAlarm(long alarmId) {
        executorService.execute(() -> alarmDao.dismissAlarm((int) alarmId));
    }

    public interface MissionCallback {
        void onMissionsLoaded(List<Mission> missions);
    }
}