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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.runcause.UI.ListProjectFragmentViewModel;
import com.example.runcause.model.LoadingState;
import com.example.runcause.model.Model;
import com.example.runcause.model.User;
import com.example.runcause.model.adapter.MyAdapter;
import com.example.runcause.model.intefaces.OnItemClickListener;

import java.util.List;

public class ProjectRunListFragment extends Fragment {
    ListProjectFragmentViewModel viewModel;
    View view;
    MyAdapter adapter;
    User user;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefresh;
    ProjectRunListFragment.ActionProjectRunListFragmentTo action1;
    ProjectRunListFragment.ActionProjectRunListFragmentTo action11;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(ListProjectFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_project_run_list, container, false);
        user = ProjectRunListFragmentArgs.fromBundle(getArguments()).getUser();
        progressBar = view.findViewById(R.id.list_project_progressbar);
        swipeRefresh = view.findViewById(R.id.project_list_swipe_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(true);
                Model.instance.reloadRunsList();
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }
        });
        RecyclerView list = view.findViewById(R.id.project_list_rv);
        adapter = new MyAdapter();
        list.setAdapter(adapter);
        list.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        list.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list.getContext(), linearLayoutManager.getOrientation());
        list.addItemDecoration(dividerItemDecoration);
        setHasOptionsMenu(true);
        viewModel.getData().observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                adapter.setFragment(ProjectRunListFragment.this);
                adapter.setData(posts);
                adapter.notifyDataSetChanged();
            }
        });
        progressBar.setVisibility(View.GONE);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                progressBar.setVisibility(View.VISIBLE);
                Post p = viewModel.getData().getValue().get(position);
                ListPostFragmentDirections.ActionListPostFragmentToPostDetailsFragment action = ListPostFragmentDirections.actionListPostFragmentToPostDetailsFragment(p);
                Navigation.findNavController(v).navigate(action);
            }
        });
        swipeRefresh.setRefreshing(Model.instance.getLoadingState().getValue()== LoadingState.loading);
        Model.instance.getLoadingState().observe(getViewLifecycleOwner(),loadingState -> {
            swipeRefresh.setRefreshing(loadingState== LoadingState.loading);
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.wine_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean result = true;
        if (!super.onOptionsItemSelected(item)) {
            switch (item.getItemId()) {
                case R.id.userPage:
                    action1 = ListPostFragmentDirections.actionListPostFragmentToUserPageFragment(user);
                    progressBar.setVisibility(View.VISIBLE);
                    Navigation.findNavController(view).navigate(action1);
                    break;
                case R.id.map_list:
                    action11 = ListPostFragmentDirections.actionListPostFragmentToMapFragment(user);
                    progressBar.setVisibility(View.VISIBLE);
                    Navigation.findNavController(view).navigate(action11);
                    break;
                default:
                    result = false;
                    break;
            }
        }
        return result;
    }
}