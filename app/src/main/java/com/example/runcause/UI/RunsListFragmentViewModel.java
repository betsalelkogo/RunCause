package com.example.runcause.UI;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.runcause.model.Model;
import com.example.runcause.model.Run;
import com.example.runcause.model.User;

import java.util.List;

public class RunsListFragmentViewModel extends ViewModel {
    User user;
    LiveData<List<Run>> data;
    public LiveData<List<Run>> getData()
    {
        return data;
    }

    public void setData(User user)
    {
        this.user=user;
        this.data=Model.instance.getUserRun(user.getEmail());
    }


}
