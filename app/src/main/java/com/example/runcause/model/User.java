package com.example.runcause.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.HashMap;
import java.util.Map;

@Entity
public class User implements Parcelable {
    @PrimaryKey
    @NonNull
    private String name;
    private String password;
    private String email;
    private String imageUrl;
    private String weight;
    private String height;
    protected String bYear;


    public User(String name,String password,String email,String bYear,String weight,String height){
        this.name=name;
        this.password=password;
        this.email=email;
        this.bYear=bYear;
        this.weight=weight;
        this.height=height;
        this.imageUrl=null;
    }

    protected User(Parcel in) {
        name = in.readString();
        password = in.readString();
        email = in.readString();
        imageUrl = in.readString();
        bYear = in.readString();
        weight = in.readString();
        height = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getName(){return this.name;}

    public void setName(String name){this.name=name;}

    public String getEmail() {
        return this.email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email=email;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getbYear() {
        return bYear;
    }

    public void setbYear(String bYear) {
        this.bYear = bYear;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public Map<String,Object> toJson(){
        Map<String, Object> json = new HashMap<>();
        json.put("name", getName());
        json.put("password", getPassword());
        json.put("email", getEmail());
        json.put("imageUrl", getImageUrl());
        json.put("height", getHeight());
        json.put("bYear", getbYear());
        json.put("weight", getWeight());
        return json;
    }

    static User fromJson(Map<String,Object> json){
        String password = (String)json.get("password");
        if (password == null){
            return null;
        }
        String name = (String)json.get("name");
        String email = (String)json.get("email");
        String imageUrl = (String)json.get("imageUrl");
        String height = (String)json.get("height");
        String bYear = (String)json.get("bYear");
        String weight = (String)json.get("weight");
        User u = new User(name,password,email,bYear,weight,height);
        u.setImageUrl(imageUrl);
        return u;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(password);
        parcel.writeString(email);
        parcel.writeString(imageUrl);
        parcel.writeString(bYear);
        parcel.writeString(weight);
        parcel.writeString(height);
    }
}
