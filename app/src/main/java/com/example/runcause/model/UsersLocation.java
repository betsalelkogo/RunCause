package com.example.runcause.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class UsersLocation implements Parcelable {
    private double lat;
    private double lng;
    private String name;
    private String ttl;
    public UsersLocation() {
    }


    protected UsersLocation(Parcel in) {
        lat = in.readDouble();
        lng = in.readDouble();
        name = in.readString();
        ttl = in.readString();
    }

    public static final Creator<UsersLocation> CREATOR = new Creator<UsersLocation>() {
        @Override
        public UsersLocation createFromParcel(Parcel in) {
            return new UsersLocation(in);
        }

        @Override
        public UsersLocation[] newArray(int size) {
            return new UsersLocation[size];
        }
    };

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }


    static public UsersLocation fromJson(Map<String,Object> json){
        if(json==null) return null;
        double lat = (double)json.get("lat");
        double lng = (double)json.get("lng");
        String userName = (String) json.get("userName");
        String ttl = (String)json.get("userName");
        UsersLocation location = new UsersLocation();
        location.setLat(lat);
        location.setLng(lng);
        location.setName(userName);
        location.setTtl(ttl);
        return location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(name);
        dest.writeString(ttl);
    }


    public String getTtl() {
        return ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
