package com.example.runcause;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.runcause.UI.RunsListFragmentViewModel;
import com.example.runcause.UI.UserRunListFragmentViewModel;
import com.example.runcause.model.Project;
import com.example.runcause.model.User;

public class UserStatistcProjectFragment extends Fragment {
    View view;
    TextView email,km,time;
    UserRunListFragmentViewModel viewModel;
    Button close;
    User user;
    Project project;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(UserRunListFragmentViewModel.class);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_user_statistc_project, container, false);
        user=UserStatistcProjectFragmentArgs.fromBundle(getArguments()).getUser();
        project=UserStatistcProjectFragmentArgs.fromBundle(getArguments()).getProject();
        close=view.findViewById(R.id.statistic_btn);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserStatistcProjectFragmentDirections.ActionUserStatistcProjectFragmentToUserHomePageFragment action =UserStatistcProjectFragmentDirections.actionUserStatistcProjectFragmentToUserHomePageFragment(user,project);
                Navigation.findNavController(view).navigate(action);
            }
        });


        return view;
    }
}