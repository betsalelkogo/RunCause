package com.example.runcause.UI;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.runcause.model.Location;

import java.util.ArrayList;
import java.util.List;

public class RunLocationViewModel extends ViewModel {
    ArrayList<Location> data;
        public void setData(ArrayList<Location> data) {
            this.data=data;
        }
        public ArrayList<Location> getData( ) { return data; }
}
