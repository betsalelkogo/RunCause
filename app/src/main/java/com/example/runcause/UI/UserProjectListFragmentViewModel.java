package com.example.runcause.UI;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.runcause.model.Model;
import com.example.runcause.model.Project;
import com.example.runcause.model.Run;
import com.example.runcause.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserProjectListFragmentViewModel extends ViewModel {
    LiveData<List<Project>> data = Model.instance.getProject();
    public LiveData<List<Project>> getData() {
        return data;
    }
}
