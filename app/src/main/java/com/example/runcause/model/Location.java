package com.example.runcause.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Location {
    private double lat;
    private double lng;
    private float speed;
    private String id_key;

    public Location() {
    }


    public Map<String,Object> toJson(){
        Map<String, Object> json = new HashMap<>();
        json.put("lat", getLat());
        json.put("lng", getLng());
        json.put("speed", getSpeed());
        return json;
    }

    static Location fromJson(Map<String,Object> json){
        String password = (String)json.get("password");
        if (password == null){
            return null;
        }
        double lat = (double)json.get("lat");
        double lng = (double)json.get("lng");
        float speed = (float)json.get("speed");
        Location l = new Location();
        l.setLat(lat);
        l.setLng(lng);
        l.setSpeed(speed);
        return l;
    }

    public float getSpeed() {
        return speed;
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
}
