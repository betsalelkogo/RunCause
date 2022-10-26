package com.example.runcause.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.runcause.MyApplication;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Run {
    public final static String LAST_UPDATED = "LAST_UPDATE";
    @PrimaryKey
    @NonNull
    private String id_key=null;
    private String distance;
    private String projectId;
    private String date;
    private String time;
    private String calories;
    private double startLang;
    private double startLant;
    //private double finishLang;
    //private double finishLant;
    private boolean isDeleted;
    private Long lastUpdated = new Long(0);

    public Run(String distance,String projectId,String date, String time,String calories,double startLang,double startLant,boolean isDeleted){
        this.calories=calories;
        this.distance=distance;
        this.date=date;
        this.projectId=projectId;
        this.time=time;
        this.startLang=startLang;
        this.startLant=startLant;
        this.isDeleted=isDeleted;
    }
    public Run() {
    }

    public Map<String,Object> toJson(){
        Map<String, Object> json = new HashMap<>();
        json.put("calories", getCalories());
        json.put("distance", getDistance());
        json.put("projectId", getProjectId());
        json.put("time", getTime());
        json.put("date", getDate());
        json.put("startLang", getStartLang());
        json.put("startLant", getStartLant());
        json.put("isDeleted", isDeleted());
        json.put(LAST_UPDATED, FieldValue.serverTimestamp());
        return json;
    }


    static Run fromJson(Map<String,Object> json){
        String projectId = (String)json.get("projectId");
        if (projectId == null){
            return null;
        }
        String distance = (String)json.get("distance");
        String calories = (String)json.get("calories");
        String time = (String)json.get("time");
        String date = (String)json.get("date");
        double startLang = (double)json.get("startLang");
        double startLant = (double)json.get("startLant");
        boolean isDeleted = (boolean)json.get("isDeleted");
        Run r = new Run(distance,projectId,date,time,calories,startLang,startLant,isDeleted);
        r.setDeleted(isDeleted);
        Timestamp ts = (Timestamp)json.get(LAST_UPDATED);
        r.setLastUpdated(new Long(ts.getSeconds()));
        return r;
    }
    static Long getLocalLastUpdated(){
        Long localLastUpdate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getLong("RUN_LAST_UPDATE",0);
        return localLastUpdate;
    }

    static void setLocalLastUpdated(Long date){
        SharedPreferences.Editor editor = MyApplication.getContext()
                .getSharedPreferences("TAG", Context.MODE_PRIVATE).edit();
        editor.putLong("RUN_LAST_UPDATE",date);
        editor.commit();
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public double getStartLang() {
        return startLang;
    }

    public void setStartLang(double startLang) {
        this.startLang = startLang;
    }

    public double getStartLant() {
        return startLant;
    }

    public void setStartLant(double startLant) {
        this.startLant = startLant;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setId_key(String id) {
        this.id_key=id;
    }

    public String getId_key() {
        return this.id_key;
    }
}
