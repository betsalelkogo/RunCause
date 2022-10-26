package com.example.runcause.model.intefaces;

import com.example.runcause.model.User;

import java.util.List;

public interface GetAllUserListener {
    void onComplete(List<User> data);
}
