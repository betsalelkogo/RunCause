package com.example.runcause;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.runcause.UI.ListProjectFragmentViewModel;
import com.example.runcause.model.LoadingState;
import com.example.runcause.model.Model;
import com.example.runcause.model.Project;
import com.example.runcause.model.User;
import com.example.runcause.model.adapter.AdapterProject;
import com.example.runcause.model.intefaces.OnItemClickListener;



public class ProjectRunListFragment extends Fragment {
    ListProjectFragmentViewModel viewModel;
    View view;
    AdapterProject adapter;
    User user;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefresh;
    ProjectRunListFragmentDirections.ActionProjectRunListFragmentToUserHomePageFragment actionUser;
    Project project=new Project();
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
        swipeRefresh.setOnRefreshListener(() -> {
            swipeRefresh.setRefreshing(true);
            Model.instance.reloadProjectList();
            adapter.notifyDataSetChanged();
            swipeRefresh.setRefreshing(false);
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
        viewModel.getData().observe(getViewLifecycleOwner(), projects -> {
            adapter.setFragment(ProjectRunListFragment.this);
            adapter.setData(projects);
            adapter.notifyDataSetChanged();
        });
        progressBar.setVisibility(View.GONE);
        swipeRefresh.setRefreshing(Model.instance.getLoadingState().getValue()== LoadingState.loading);
        Model.instance.getLoadingState().observe(getViewLifecycleOwner(),loadingState -> {
            swipeRefresh.setRefreshing(loadingState== LoadingState.loading);
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                progressBar.setVisibility(View.VISIBLE);
                project = viewModel.getData().getValue().get(position);
                ProjectRunListFragmentDirections.ActionProjectRunListFragmentToUserHomePageFragment action = ProjectRunListFragmentDirections.actionProjectRunListFragmentToUserHomePageFragment(user,project);
                Navigation.findNavController(v).navigate(action);
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu( Menu menu,  MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.project_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        boolean result = true;
        if (!super.onOptionsItemSelected(item)) {
            switch (item.getItemId()) {
                case R.id.userPage:
                    actionUser = ProjectRunListFragmentDirections.actionProjectRunListFragmentToUserHomePageFragment(user,project);
                    progressBar.setVisibility(View.VISIBLE);
                    System.out.println(user);
                    Navigation.findNavController(view).navigate(actionUser);
                    break;
                default:
                    result = false;
                    break;
            }
        }
        return result;
    }
}