package com.example.runcause.model.intefaces;

import com.example.runcause.model.Run;

import java.util.List;

public interface GetAllRunsListener {
    void onComplete(List<Run> data);
}
