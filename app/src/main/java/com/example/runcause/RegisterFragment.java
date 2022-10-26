package com.example.runcause;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.runcause.model.Model;
import com.example.runcause.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterFragment extends Fragment {
    EditText name, password, confirmPassword,email,bYear,weight,height;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        Button signUpBtn = view.findViewById(R.id.register_sign_up_btn);
        name=view.findViewById(R.id.register_user_name_et);
        password=view.findViewById(R.id.register_password_et);
        bYear=view.findViewById(R.id.bYear_et);
        weight=view.findViewById(R.id.register_weight_et);
        height=view.findViewById(R.id.register_height_et);
        confirmPassword=view.findViewById(R.id.register_confirm_password_et);
        email=view.findViewById(R.id.register_user_email_et);
        progressBar=view.findViewById(R.id.register_progressbar);
        progressBar.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()){
                    Toast.makeText(getActivity(), "Please check your input", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign up success, update UI with the signed-in user's information
                                    User user=new User(name.getText().toString(),password.getText().toString(),email.getText().toString(),bYear.getText().toString(),weight.getText().toString(),height.getText().toString());
                                    Model.instance.addUser(user, ()->{
                                        Navigation.findNavController(v).navigate(R.id.action_registerFragment_to_loginFragment);
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
        });
        setHasOptionsMenu(true);
        return view;
    }
    private boolean validate() {
        return (name.getText().length() > 2 && confirmPassword.getText().length() > 5&&email.getText().length() > 2 && password.getText().length() > 5);
    }
}