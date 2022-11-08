package com.example.runcause.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
@Dao
public interface ProjectDao {
    @Query("select * from Project")
    List<Project> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Project... projects);

    @Delete
    void delete(Project project);

    @Query("SELECT * FROM Project WHERE name=:name")
    LiveData<List<Project>> getProjectByName(String name);
}
