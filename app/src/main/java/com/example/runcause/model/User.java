package com.example.runcause.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private boolean haveProject;
    protected String bYear;
    private List<String> myList=new ArrayList<>();

    public User(String name, String password, String email, String bYear, String weight, String height, boolean haveProject){
        this.name=name;
        this.password=password;
        this.email=email;
        this.bYear=bYear;
        this.weight=weight;
        this.height=height;
        this.haveProject = haveProject;
        this.imageUrl=null;
    }


    protected User(Parcel in) {
        name = in.readString();
        password = in.readString();
        email = in.readString();
        imageUrl = in.readString();
        weight = in.readString();
        height = in.readString();
        haveProject = in.readByte() != 0;
        bYear = in.readString();
        myList = in.createStringArrayList();
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
        json.put("myListProject", getMyList());
        json.put("haveProject", isHaveProject());
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
        boolean haveProject=(boolean)json.get("haveProject");
        List<String> myListProject = (List<String>) json.get("myListProject");
        User u = new User(name,password,email,bYear,weight,height, haveProject);
        u.setMyList(myListProject);
        u.setImageUrl(imageUrl);
        return u;
    }





    public List<String> getMyList() {
        return myList;
    }

    public void setMyList(List<String> myList) {
        this.myList = myList;
    }

    public boolean isHaveProject() {
        return haveProject;
    }

    public void setHaveProject(boolean haveProject) {
        this.haveProject = haveProject;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(password);
        dest.writeString(email);
        dest.writeString(imageUrl);
        dest.writeString(weight);
        dest.writeString(height);
        dest.writeByte((byte) (haveProject ? 1 : 0));
        dest.writeString(bYear);
        dest.writeStringList(myList);
    }
}
