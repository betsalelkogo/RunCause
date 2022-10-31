package com.example.runcause;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.runcause.UI.ListProjectFragmentViewModel;
import com.example.runcause.model.Model;
import com.example.runcause.model.User;
import com.example.runcause.model.adapter.MyAdapter;


public class ProjectRunListFragment extends Fragment {
    ListProjectFragmentViewModel viewModel;
    View view;
    MyAdapter adapter;
    User user;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefresh;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(ListProjectFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_project_run_list, container, false);
        user=ProjectRunListFragmentArgs.fromBundle(getArguments()).getUser();
        progressBar = view.findViewById(R.id.list_project_progressbar);
        swipeRefresh = view.findViewById(R.id.project_list_swipe_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(true);
                Model.instance.reloadProjectList();
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }
        });







        return view;
    }
}