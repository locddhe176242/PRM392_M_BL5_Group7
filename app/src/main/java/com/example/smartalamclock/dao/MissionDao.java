package com.example.smartalamclock.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import com.example.smartalamclock.entity.Mission;
import java.util.List;
@Dao
public interface MissionDao {
    @Insert
    long insert(Mission mission);

    @Update
    void update(Mission mission);

    @Delete
    void delete(Mission mission);

    @Query("SELECT * FROM mission")
    List<Mission> getAll();

    @Query("SELECT * FROM mission WHERE id = :id LIMIT 1")
    Mission getById(int id);
}
