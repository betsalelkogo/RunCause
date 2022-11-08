package com.example.runcause.UI;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.runcause.model.Model;
import com.example.runcause.model.Project;
import com.example.runcause.model.Run;

import java.util.List;

public class UserRunListFragmentViewModel extends ViewModel {
    LiveData<List<Run>> data = Model.instance.getAllRun();
    public LiveData<List<Run>> getData() {
        return data;
    }

}
