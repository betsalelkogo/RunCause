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
public class Project implements Parcelable {

    public final static String LAST_UPDATED = "LAST_UPDATE";
    @PrimaryKey
    @NonNull
    private String id_key=null;
    private String totalDistance;
    private String runDistance;
    private String projectName;
    private String projectDetails;
    private String ttl;
    private boolean isPublic;
    private Long lastUpdated = new Long(0);

    public Project(String totalDistance,String runDistance,String ttl,boolean isPublic,String projectName,String projectDetails){
        this.totalDistance=totalDistance;
        this.runDistance=runDistance;
        this.ttl=ttl;
        this.isPublic=isPublic;
        this.projectDetails=projectDetails;
        this.projectName=projectName;
    }
    @Ignore
    public Project() {
    }

    protected Project(Parcel in) {
        id_key = in.readString();
        totalDistance = in.readString();
        runDistance = in.readString();
        projectName = in.readString();
        projectDetails = in.readString();
        ttl = in.readString();
        isPublic = in.readByte() != 0;
        if (in.readByte() == 0) {
            lastUpdated = null;
        } else {
            lastUpdated = in.readLong();
        }
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    public Map<String,Object> toJson(){
        Map<String, Object> json = new HashMap<>();
        json.put("totalDistance", getTotalDistance());
        json.put("runDistance", getRunDistance());
        json.put("ttl", getTtl());
        json.put("isPublic", isPublic());
        json.put("projectName", getProjectName());
        json.put("projectDetails", getProjectDetails());
        json.put(LAST_UPDATED, FieldValue.serverTimestamp());
        return json;
    }


    static Project fromJson(Map<String,Object> json){
        String projectName = (String)json.get("projectName");
        if (projectName == null){
            return null;
        }
        String totalDistance = (String)json.get("totalDistance");
        String runDistance = (String)json.get("runDistance");
        String ttl = (String)json.get("ttl");
        String projectDetails = (String)json.get("projectDetails");
        boolean isPublic = (boolean)json.get("isPublic");
        Project p = new Project(totalDistance,runDistance,ttl,isPublic,projectName,projectDetails);
        p.setPublic(isPublic);
        Timestamp ts = (Timestamp)json.get(LAST_UPDATED);
        p.setLastUpdated(new Long(ts.getSeconds()));
        return p;
    }
    static Long getLocalLastUpdated(){
        Long localLastUpdate = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getLong("PROJECT_LAST_UPDATE",0);
        return localLastUpdate;
    }

    static void setLocalLastUpdated(Long date){
        SharedPreferences.Editor editor = MyApplication.getContext()
                .getSharedPreferences("TAG", Context.MODE_PRIVATE).edit();
        editor.putLong("PROJECT_LAST_UPDATE",date);
        editor.commit();
    }

    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getRunDistance() {
        return runDistance;
    }

    public void setRunDistance(String runDistance) {
        this.runDistance = runDistance;
    }

    public String getTtl() {
        return ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @NonNull
    public String getId_key() {
        return id_key;
    }

    public void setId_key(@NonNull String id_key) {
        this.id_key = id_key;
    }

    public String getProjectDetails() {
        return projectDetails;
    }

    public void setProjectDetails(String projectDetails) {
        this.projectDetails = projectDetails;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id_key);
        parcel.writeString(totalDistance);
        parcel.writeString(runDistance);
        parcel.writeString(projectName);
        parcel.writeString(projectDetails);
        parcel.writeString(ttl);
        parcel.writeByte((byte) (isPublic ? 1 : 0));
        if (lastUpdated == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(lastUpdated);
        }
    }
}
