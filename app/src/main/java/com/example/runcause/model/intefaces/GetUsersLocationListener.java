package com.example.runcause.model.intefaces;

import com.example.runcause.model.Location;
import com.example.runcause.model.UsersLocation;

import java.util.ArrayList;

public interface GetUsersLocationListener {
    void onComplete(ArrayList<UsersLocation> arrLocation);
}
