package com.example.runcause;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.runcause.UI.ListProjectFragmentViewModel;
import com.example.runcause.model.LoadingState;
import com.example.runcause.model.Model;
import com.example.runcause.model.Project;
import com.example.runcause.model.User;
import com.example.runcause.model.adapter.AdapterProject;
import com.google.firebase.auth.FirebaseAuth;

public class UserHomePageFragment extends Fragment {
    ListProjectFragmentViewModel viewModelProject;
    View view;
    TextView userName, email;
    User user;
    UserHomePageFragmentDirections.ActionUserHomePageFragmentToUserRunListFragment HomeToRunList;
    UserHomePageFragmentDirections.ActionUserHomePageFragmentToRunScreenFragment UserToNewRun;
    UserHomePageFragmentDirections.ActionUserHomePageFragmentToEditUserFragment UserToEditUser;
    UserHomePageFragmentDirections.ActionUserHomePageFragmentToAddRunProjectFragment UserToAddProject;
    SwipeRefreshLayout swipeRefresh;
    ImageButton editUserImg, addNewProjectBtn;
    ImageView userImage;
    ProgressBar progressBar;
    AdapterProject adapter;
    Project project;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModelProject = new ViewModelProvider(this).get(ListProjectFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_project_run_list, container, false);
        user=ProjectRunListFragmentArgs.fromBundle(getArguments()).getUser();
        project=ProjectRunListFragmentArgs.fromBundle(getArguments()).getUser();
        userName= view.findViewById(R.id.user_page_name_tv);
        email= view.findViewById(R.id.user_page_email_tv);
        progressBar = view.findViewById(R.id.list_project_progressbar);
        swipeRefresh = view.findViewById(R.id.project_list_swipe_refresh);
        swipeRefresh.setOnRefreshListener(() -> {
            swipeRefresh.setRefreshing(true);
            Model.instance.reloadProjectList();
            adapter.notifyDataSetChanged();
            swipeRefresh.setRefreshing(false);
        });
        userImage=view.findViewById(R.id.user_page_image);
        editUserImg=view.findViewById(R.id.user_add_page_image_btn);
        addNewProjectBtn=view.findViewById(R.id.add_project_btn);
        RecyclerView list = view.findViewById(R.id.project_list_rv);
        adapter = new AdapterProject();
        list.setAdapter(adapter);
        list.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        list.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list.getContext(), linearLayoutManager.getOrientation());
        list.addItemDecoration(dividerItemDecoration);
        setHasOptionsMenu(true);
        viewModelProject.getData().observe(getViewLifecycleOwner(), projects -> {
            adapter.setFragment(UserHomePageFragment.this);
            adapter.setData(projects);
            adapter.notifyDataSetChanged();
        });
        progressBar.setVisibility(View.GONE);
        adapter.setOnItemClickListener((position, v) -> progressBar.setVisibility(View.VISIBLE));
        swipeRefresh.setRefreshing(Model.instance.getLoadingState().getValue()== LoadingState.loading);
        Model.instance.getLoadingState().observe(getViewLifecycleOwner(),loadingState -> {
            swipeRefresh.setRefreshing(loadingState== LoadingState.loading);
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        boolean result = true;
        if (!super.onOptionsItemSelected(item)) {
            switch (item.getItemId()) {
                case R.id.run_list_menu:
                    HomeToRunList = UserHomePageFragmentDirections.actionUserHomePageFragmentToUserRunListFragment(user);
                    progressBar.setVisibility(View.VISIBLE);
                    System.out.println(user);
                    Navigation.findNavController(view).navigate(HomeToRunList);
                    break;
                case R.id.edit_user:
                    UserToEditUser = UserHomePageFragmentDirections.actionUserHomePageFragmentToEditUserFragment(user);
                    progressBar.setVisibility(View.VISIBLE);
                    System.out.println(user);
                    Navigation.findNavController(view).navigate(UserToEditUser);
                    break;
                case R.id.home_menu:
                    UserToNewRun = UserHomePageFragmentDirections.actionUserHomePageFragmentToRunScreenFragment(user,project);
                    progressBar.setVisibility(View.VISIBLE);
                    System.out.println(user);
                    Navigation.findNavController(view).navigate(UserToNewRun);
                    break;
                case R.id.new_project:
                    UserToAddProject = UserHomePageFragmentDirections.actionUserHomePageFragmentToAddRunProjectFragment(user);
                    progressBar.setVisibility(View.VISIBLE);
                    System.out.println(user);
                    Navigation.findNavController(view).navigate(UserToAddProject);
                    break;
                case R.id.logout_menu:
                    FirebaseAuth.getInstance().signOut();
                    Navigation.findNavController(view).navigate(R.id.action_userHomePageFragment_to_startAppFragment);
                    break;
                default:
                    result = false;
                    break;
            }
        }
        return result;
    }
}
