package com.example.wineapp.UI;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.wineapp.model.Model;
import com.example.wineapp.model.Post;

import java.util.List;

public class ListPostFragmentViewModel extends ViewModel {
    LiveData<List<Post>> data = Model.instance.getAll();
    public LiveData<List<Post>> getData() {
        return data;
    }



}
