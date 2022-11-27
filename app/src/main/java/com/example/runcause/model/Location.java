package com.example.runcause.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Location  {
    private double lat;
    private double lng;
    private float speed;
    private String id_key;

    public Location() {
    }
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
        double lat = (double)json.get("lat");
        double lng = (double)json.get("lng");
        float speed = (float)json.get("speed");
        Location location = new Location();
        location.setLat(lat);
        location.setLng(lng);
        location.setSpeed(speed);
        return location;
    }

}
