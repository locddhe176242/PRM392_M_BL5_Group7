package com.example.smartalamclock.repository;

import android.app.Application;

import com.example.smartalamclock.dao.MissionDao;
import com.example.smartalamclock.data.AppDatabase;
import com.example.smartalamclock.entity.Mission;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlarmRepository {

    private final MissionDao missionDao;
    private final ExecutorService executorService;

    public AlarmRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        missionDao = db.missionDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void getMissionsForAlarm(int alarmId, MissionCallback callback) {
        executorService.execute(() -> {
            List<Mission> missions = missionDao.getMissionsForAlarm(alarmId);
            callback.onMissionsLoaded(missions);
        });
    }

    public void updateMissionCompleted(int missionId, boolean completed) {
        executorService.execute(() -> missionDao.updateMissionCompleted(missionId, completed));
    }

    public interface MissionCallback {
        void onMissionsLoaded(List<Mission> missions);
    }
}
