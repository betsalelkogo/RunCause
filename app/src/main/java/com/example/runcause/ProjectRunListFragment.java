package com.example.runcause;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.runcause.UI.ListProjectFragmentViewModel;
import com.example.runcause.model.LoadingState;
import com.example.runcause.model.Model;
import com.example.runcause.model.Project;
import com.example.runcause.model.User;
import com.example.runcause.model.adapter.AdapterProject;
import com.example.runcause.model.adapter.MyAdapter;
import com.example.runcause.model.intefaces.OnItemClickListener;

import java.util.List;


public class ProjectRunListFragment extends Fragment {
    ListProjectFragmentViewModel viewModel;
    View view;
    AdapterProject adapter;
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
        RecyclerView list = view.findViewById(R.id.project_list_rv);
        adapter = new AdapterProject();
        list.setAdapter(adapter);
        list.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        list.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list.getContext(), linearLayoutManager.getOrientation());
        list.addItemDecoration(dividerItemDecoration);
        setHasOptionsMenu(true);
        viewModel.getData().observe(getViewLifecycleOwner(), new Observer<List<Project>>() {
            @Override
            public void onChanged(List<Project> projects) {
                adapter.setFragment(ProjectRunListFragment.this);
                adapter.setData(projects);
                adapter.notifyDataSetChanged();
            }
        });
        progressBar.setVisibility(View.GONE);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                progressBar.setVisibility(View.VISIBLE);

            }
        });
        swipeRefresh.setRefreshing(Model.instance.getLoadingState().getValue()== LoadingState.loading);
        Model.instance.getLoadingState().observe(getViewLifecycleOwner(),loadingState -> {
            swipeRefresh.setRefreshing(loadingState== LoadingState.loading);
        });






        return view;
    }
}