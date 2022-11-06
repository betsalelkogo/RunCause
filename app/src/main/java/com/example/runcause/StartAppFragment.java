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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.runcause.UI.ListProjectFragmentViewModel;
import com.example.runcause.model.Model;
import com.example.runcause.model.User;
import com.example.runcause.model.intefaces.GetUserByEmailListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.DatagramPacket;

public class StartAppFragment extends Fragment {
    ListProjectFragmentViewModel viewModel;
    Button registerBtn,signInBtn;
    View view;
    FirebaseUser user=null;
    ProgressBar progressBar;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(ListProjectFragmentViewModel.class);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_start_app, container, false);
        progressBar=view.findViewById(R.id.start_app_progressBar);
        progressBar.setVisibility(View.GONE);
        user= FirebaseAuth.getInstance().getCurrentUser();
        registerBtn= view.findViewById(R.id.start_app_register_btn);
        signInBtn = view.findViewById(R.id.start_app_signin_btn);
        if (user != null ) {
            registerBtn.setEnabled(false);
            signInBtn.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            Model.instance.getUserByEmail(user.getEmail(), new GetUserByEmailListener() {
                @Override
                public void onComplete(User u) {
                    if(!checkIfProjectEmpty()) {
                        Toast.makeText(getActivity(), "No Run Projects", Toast.LENGTH_SHORT).show();
                        StartAppFragmentDirections.ActionStartAppFragmentToAddRunProjectFragment action=StartAppFragmentDirections.actionStartAppFragmentToAddRunProjectFragment(u);
                        Navigation.findNavController(view).navigate(action);
                    }
                    else{
                        StartAppFragmentDirections.ActionStartAppFragmentToProjectRunListFragment action=StartAppFragmentDirections.actionStartAppFragmentToProjectRunListFragment(u);
                        Navigation.findNavController(view).navigate(action);
                    }
                }
            });
        }
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Navigation.findNavController(v).navigate(R.id.action_startAppFragment_to_registerFragment);
            }
        });
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Navigation.findNavController(v).navigate(R.id.action_startAppFragment_to_loginFragment);
            }
        });
        setHasOptionsMenu(true);

        return view;

    }

    private boolean checkIfProjectEmpty() {
        return viewModel.getData().getValue() == null;
    }
}