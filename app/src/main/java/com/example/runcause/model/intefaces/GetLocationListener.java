package com.example.runcause.model.intefaces;

import com.example.runcause.model.Location;
import com.example.runcause.model.Project;
import com.example.runcause.model.User;

import java.util.ArrayList;
import java.util.List;

public interface GetLocationListener {
    void onComplete(ArrayList<Location> arrLocation);
}
