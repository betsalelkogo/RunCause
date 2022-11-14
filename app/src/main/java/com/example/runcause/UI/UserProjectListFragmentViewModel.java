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
    User user;
    LiveData<List<Project>> data;
    //List<Project> temp=new ArrayList<>();
    public LiveData<List<Project>> getData() {
        return data;
    }
    public void setData(User user)
    {
        this.user=user;
        this.data= Model.instance.getProjectByName(user.getMyList().get(0));
    }
}
