package com.example.runcause.UI;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.runcause.model.Model;
import com.example.runcause.model.Project;
import com.example.runcause.model.Run;


import java.util.List;

public class ListProjectFragmentViewModel extends ViewModel {
    LiveData<List<Project>> data = Model.instance.getAllProject();
    public LiveData<List<Project>> getData() {
        return data;
    }



}
