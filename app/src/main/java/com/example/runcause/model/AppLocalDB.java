package com.example.runcause.model;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.runcause.MyApplication;


@Database(entities = {Run.class,Project.class,User.class}, version = 2)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract RunDao runDao();
    public abstract UserDao userDao();
    public abstract ProjectDao projectDao();
}


public class AppLocalDB {
    static public final AppLocalDbRepository db =
            Room.databaseBuilder(MyApplication.getContext(),
                    AppLocalDbRepository.class,
                    "dbRuns.db")
                    .fallbackToDestructiveMigration()
                    .build();
    private AppLocalDB(){}
}