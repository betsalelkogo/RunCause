package com.example.runcause.model;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RunDao {
    @Query("select * from Run")
    List<Run> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Run... runs);

    @Delete
    void delete(Run runs);

    @Query("SELECT * FROM Run WHERE projectId=:projectId ")
    LiveData<List<Run>> getRunByProject(String projectId);

}
