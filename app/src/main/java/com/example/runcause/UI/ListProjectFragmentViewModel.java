package com.example.runcause.UI;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.wineapp.model.Model;
import com.example.wineapp.model.Post;

import java.util.List;

public class ListProjectFragmentViewModel extends ViewModel {
    LiveData<List<Project>> data = Model.instance.getAll();
    public LiveData<List<Project>> getData() {
        return data;
    }



}
