//package com.example.runcause.model;
//
//
//import androidx.room.Database;
//import androidx.room.Room;
//import androidx.room.RoomDatabase;
//
//import com.example.runcause.MyApplication;
//
//
//@Database(entities = {Run.class,Project.class}, version = 1)
//abstract class AppLocalDbRepository extends RoomDatabase {
//    //RoomDatabasepublic abstract RunDao runDao();
//    //public abstract UserDao userDao();
//    public abstract ProjectDao projectDao();
//}
//
//
//public class AppLocalDB {
//    static public final AppLocalDbRepository db =
//            Room.databaseBuilder(MyApplication.getContext(),
//                    AppLocalDbRepository.class,
//                    "dbRuns.db")
//                    .fallbackToDestructiveMigration()
//                    .build();
//    private AppLocalDB(){}
//}