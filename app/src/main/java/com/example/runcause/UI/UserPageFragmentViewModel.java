package com.example.runcause.UI;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.wineapp.model.Model;
import com.example.wineapp.model.Post;
import com.example.wineapp.model.User;

import java.util.List;

public class UserPageFragmentViewModel extends ViewModel {
    LiveData<List<Post>> data;
     User user;
    public LiveData<List<Post>> getData()
    {
        return data;
    }

    public void setData(User user)
    {
        this.user=user;
        this.data=Model.instance.getPostByName(user.getName());
    }
}
