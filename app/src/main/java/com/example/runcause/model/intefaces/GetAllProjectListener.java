package com.example.runcause.model.intefaces;

import com.example.runcause.model.Project;

import java.util.List;

public interface GetAllProjectListener {
    void onComplete(List<Project> data);
}
