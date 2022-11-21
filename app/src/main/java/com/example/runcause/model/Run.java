package com.example.runcause.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.runcause.MyApplication;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

@Entity

public class Run implements Parcelable {
    public final static String LAST_UPDATED = "LAST_UPDATE";
    @PrimaryKey
    @NonNull
    private String id_key=null;
    private String distance;
    private String projectId;
    private String date;
    private String time;
    private String calories;
    private String user;
    private boolean isDeleted;
    private Long lastUpdated = new Long(0);

    public Run(String distance,String projectId,String date, String time,String calories,String user,boolean isDeleted){
        this.calories=calories;
        this.distance=distance;
        this.date=date;
        this.projectId=projectId;
        this.time=time;
        this.user=user;
        this.isDeleted=isDeleted;
    }
    @Ignore
    public Run() {
    }

    protected Run(Parcel in) {
        id_key = in.readString();
        distance = in.readString();
        projectId = in.readString();
        date = in.readString();
        time = in.readString();
        calories = in.readString();
        isDeleted = in.readByte() != 0;
        user=in.readString();
        if (in.readByte() == 0) {
            lastUpdated = null;
        } else {
            lastUpdated = in.readLong();
        }
    }

    public static final Creator<Run> CREATOR = new Creator<Run>() {
        @Override
        public Run createFromParcel(Parcel in) {
            return new Run(in);
        }

        @Override
        public Run[] newArray(int size) {
            return new Run[size];
        }
    };

    public Map<String,Object> toJson(){
        Map<String, Object> json = new HashMap<>();
        json.put("calories", getCalories());
        json.put("distance", getDistance());
        json.put("projectId", getProjectId());
        json.put("time", getTime());
        json.put("date", getDate());
        json.put("user", getUser());
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
        String user = (String)json.get("user");
        boolean isDeleted = (boolean)json.get("isDeleted");
        Run r = new Run(distance,projectId,date,time,calories,user,isDeleted);
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


    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setId_key(String id) {
        this.id_key=id;
    }

    public String getId_key() {
        return this.id_key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id_key);
        parcel.writeString(distance);
        parcel.writeString(projectId);
        parcel.writeString(date);
        parcel.writeString(time);
        parcel.writeString(calories);
        parcel.writeString(user);
        parcel.writeByte((byte) (isDeleted ? 1 : 0));
        if (lastUpdated == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(lastUpdated);
        }
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
