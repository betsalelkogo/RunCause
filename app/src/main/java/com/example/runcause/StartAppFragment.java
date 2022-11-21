package com.example.runcause;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.runcause.UI.ListProjectFragmentViewModel;
import com.example.runcause.model.Model;
import com.example.runcause.model.User;
import com.example.runcause.model.intefaces.GetUserByEmailListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.DatagramPacket;

public class StartAppFragment extends Fragment {
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    EditText name,email_register,emailLogin,bYear,weight,height,password,passwordRegister, confirmPassword;
    Button registerDialog,loginDialog;
    ImageButton cancelRegisterDialog;
    ListProjectFragmentViewModel viewModel;
    Button registerBtn,signInBtn;
    View view;
    FirebaseUser user=null;
    ProgressBar progressBar;
    User user1;
    private FirebaseAuth mAuth;
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
        mAuth = FirebaseAuth.getInstance();
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
                createNewContactDialogRegister();
            }
        });
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewContactDialogLogin();
            }
        });
        setHasOptionsMenu(true);

        return view;

    }

    private void createNewContactDialogLogin() {
        dialogBuilder=new AlertDialog.Builder(getContext());
        final View contactPopupView = getLayoutInflater().inflate(R.layout.fragment_login,null);
        loginDialog= contactPopupView.findViewById(R.id.sign_up_btn);
        cancelRegisterDialog=contactPopupView.findViewById(R.id.register_close_btn);
        emailLogin=contactPopupView.findViewById(R.id.sign_in_username_et);
        password=contactPopupView.findViewById(R.id.sign_in_password_et);
        dialogBuilder.setView(contactPopupView);
        dialog=dialogBuilder.create();
        dialog.show();
        loginDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateLogin()){
                    Toast.makeText(getActivity(), "Please check your input", Toast.LENGTH_SHORT).show();
                    return;
                }
                validateUser();
                dialog.dismiss();
            }
        });
        cancelRegisterDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
    private void validateUser() {
        mAuth.signInWithEmailAndPassword(emailLogin.getText().toString(), password.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressBar.setVisibility(View.VISIBLE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser userAuth = mAuth.getCurrentUser();
                            Model.instance.getUserByEmail(userAuth.getEmail(), new GetUserByEmailListener() {
                                @Override
                                public void onComplete(User u) {
                                    user1 = u;
                                    if(checkIfProjectEmpty()) {
                                        Toast.makeText(getActivity(), "No Run Projects", Toast.LENGTH_SHORT).show();
                                        StartAppFragmentDirections.ActionStartAppFragmentToAddRunProjectFragment action=StartAppFragmentDirections.actionStartAppFragmentToAddRunProjectFragment(u);
                                        Navigation.findNavController(view).navigate(action);
                                    }
                                    else{
                                        StartAppFragmentDirections.ActionStartAppFragmentToProjectRunListFragment action = StartAppFragmentDirections.actionStartAppFragmentToProjectRunListFragment(u);
                                        Navigation.findNavController(view).navigate(action);
                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
    public void createNewContactDialogRegister(){
        dialogBuilder=new AlertDialog.Builder(getContext());
        final View contactPopupView = getLayoutInflater().inflate(R.layout.fragment_register,null);
        registerDialog= contactPopupView.findViewById(R.id.register_sign_up_btn);
        cancelRegisterDialog=contactPopupView.findViewById(R.id.register_close_btn);
        name=contactPopupView.findViewById(R.id.edit_user_name_et);
        email_register=contactPopupView.findViewById(R.id.edit_user_email_et);
        bYear=contactPopupView.findViewById(R.id.edit_bYear_et);
        weight=contactPopupView.findViewById(R.id.edit_weight_et);
        height=contactPopupView.findViewById(R.id.edit_height_et);
        passwordRegister=contactPopupView.findViewById(R.id.edit_password_et);
        confirmPassword=contactPopupView.findViewById(R.id.edit_confirm_password_et);
        dialogBuilder.setView(contactPopupView);
        dialog=dialogBuilder.create();
        dialog.show();


        registerDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                save();
                dialog.dismiss();
                createNewContactDialogLogin();

            }
        });

        cancelRegisterDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void save() {
        if (!validate()){
            Toast.makeText(getActivity(), "Please check your input", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigateUp();
            progressBar.setVisibility(View.GONE);
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email_register.getText().toString(), passwordRegister.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success, update UI with the signed-in user's information
                            User user=new User(name.getText().toString(),passwordRegister.getText().toString(),email_register.getText().toString(),bYear.getText().toString(),weight.getText().toString(),height.getText().toString());
                            Model.instance.addUser(user, ()->{
                               //open the login popup
                            });
                        } else {
                            // If sign up fails, display a message to the user.
                            Toast.makeText(getActivity(), task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
    private boolean validate() {
        return (name.getText().length() > 2 && confirmPassword.getText().length() > 5&&email_register.getText().length() > 2 && passwordRegister.getText().length() > 5);
    }
    private boolean validateLogin() {
        return (emailLogin.getText().length() > 2 && password.getText().length() > 2);
    }
    private boolean checkIfProjectEmpty() {
        return viewModel.getData().getValue() == null;
    }
}