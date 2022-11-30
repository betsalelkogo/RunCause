package com.example.runcause.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Location  implements Parcelable {
    private double lat;
    private double lng;
    private float speed;
    private String id_key;

    public Location() {
    }

    protected Location(Parcel in) {
        lat = in.readDouble();
        lng = in.readDouble();
        speed = in.readFloat();
        id_key = in.readString();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

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

    public void setId_key(String id) {
        this.id_key=id;
    }
     public Map<String,Object> toJson(){
        Map<String, Object> json = new HashMap<>();
        json.put("lat", getLat());
        json.put("lng", getLng());
        json.put("speed", getSpeed());
        return json;
    }

    static public Location fromJson(Map<String,Object> json){
        if(json==null) return null;
        double lat = (double)json.get("lat");
        double lng = (double)json.get("lng");
        float speed = (float)json.get("speed");
        Location location = new Location();
        location.setLat(lat);
        location.setLng(lng);
        location.setSpeed(speed);
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
        dest.writeFloat(speed);
        dest.writeString(id_key);
    }
}
