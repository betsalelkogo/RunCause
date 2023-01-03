package com.example.runcause;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.runcause.UI.ListProjectFragmentViewModel;
import com.example.runcause.UI.RunsListFragmentViewModel;
import com.example.runcause.UI.UserProjectListFragmentViewModel;
import com.example.runcause.UI.UserRunListFragmentViewModel;
import com.example.runcause.model.LoadingState;
import com.example.runcause.model.Location;
import com.example.runcause.model.Model;
import com.example.runcause.model.Project;
import com.example.runcause.model.Run;
import com.example.runcause.model.User;
import com.example.runcause.model.adapter.AdapterProject;
import com.example.runcause.model.adapter.MyAdapter;
import com.example.runcause.model.intefaces.GetLocationListener;
import com.example.runcause.model.intefaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;


public class UserRunListFragment extends Fragment {
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    RunsListFragmentViewModel viewModel;
    View view;
    MyAdapter adapter;
    User user;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefresh;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(RunsListFragmentViewModel.class);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_user_run_list, container, false);
        user=UserRunListFragmentArgs.fromBundle(getArguments()).getUser();
        viewModel.setData(user);
        progressBar = view.findViewById(R.id.list_run_progressbar);
        swipeRefresh = view.findViewById(R.id.run_list_swipe_refresh);
        swipeRefresh.setOnRefreshListener(() -> {
            swipeRefresh.setRefreshing(true);
            Model.instance.reloadRunsList();
            adapter.notifyDataSetChanged();
            swipeRefresh.setRefreshing(false);
        });
        RecyclerView list = view.findViewById(R.id.run_list_rv);
        adapter = new MyAdapter();
        list.setAdapter(adapter);
        list.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        list.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list.getContext(), linearLayoutManager.getOrientation());
        list.addItemDecoration(dividerItemDecoration);
        setHasOptionsMenu(true);

        viewModel.getData().observe(getViewLifecycleOwner(),runs -> {
            adapter.setFragment(UserRunListFragment.this);
            adapter.setData(viewModel.getData().getValue());
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
                Run r=viewModel.getData().getValue().get(position);
                UserRunListFragmentDirections.ActionUserRunListFragmentToEndRunFragment action =UserRunListFragmentDirections.actionUserRunListFragmentToEndRunFragment(user,r,null);
                Navigation.findNavController(view).navigate(action);
            }


        });

        return view;
    }
}