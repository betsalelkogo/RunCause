package com.example.runcause.UI;

import androidx.lifecycle.LiveData;

import com.example.runcause.model.Model;
import com.example.runcause.model.Project;
import com.example.runcause.model.Run;

import java.util.List;

public class UserRunListFragmentViewModel {
    LiveData<List<Run>> data = Model.instance.getAllRun();
    public LiveData<List<Run>> getData() {
        return data;
    }

}
