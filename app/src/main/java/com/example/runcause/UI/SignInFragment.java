package com.example.runcause.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.wineapp.MyApplication;
import com.example.wineapp.R;
import com.example.wineapp.model.Model;
import com.example.wineapp.model.User;
import com.example.wineapp.model.intefaces.GetUserByEmailListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignInFragment extends Fragment {
    User user=null;
    EditText email, password;
    ProgressBar progressBar;
    Button enterBtn;
    private FirebaseAuth mAuth;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_sign_in, container, false);
        enterBtn = view.findViewById(R.id.register_sign_up_btn);
        email=view.findViewById(R.id.sign_in_username_et);
        password=view.findViewById(R.id.sign_in_password_et);
        progressBar= view.findViewById(R.id.sign_in_progressbar);
        progressBar.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()){
                    Toast.makeText(getActivity(), "Please check your input", Toast.LENGTH_SHORT).show();
                    return;
                }
                validateUser();
            }
        });
        setHasOptionsMenu(true);
        return view;
    }
    private void validateUser() {
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
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
                                    user = u;
                                    SignInFragmentDirections.ActionSignInFragmentToListPostFragment action = SignInFragmentDirections.actionSignInFragmentToListPostFragment(user);
                                    Navigation.findNavController(view).navigate(action);
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            draw();
                        }
                    }
                });
    }
    private boolean validate() {
        return (email.getText().length() > 2 && password.getText().length() > 2);
    }
    private void draw(){
        email.setText("");
        password.setText("");
        Toast.makeText(MyApplication.getContext(), "Try again", Toast.LENGTH_SHORT).show();
    }
}