package com.example.runcause;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.runcause.model.Model;
import com.example.runcause.model.Project;
import com.example.runcause.model.User;


public class AddRunProjectFragment extends Fragment {
    View view;
    EditText p_name,p_details, p_target,p_startDate,p_endDate;
    Button cancelBtn, sendBtn;
    ProgressBar progressBar;
    CheckBox checkBox_Public;
    User user;
    Project p = new Project();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_run_project, container, false);
        p_name=view.findViewById(R.id.add_project_name);
        p_details=view.findViewById(R.id.add_project_details);
        p_target=view.findViewById(R.id.add_project_km_target);
        p_startDate=view.findViewById(R.id.add_project_start_date);
        p_endDate=view.findViewById(R.id.add_project_end_date);
        cancelBtn=view.findViewById(R.id.edit_cancel_btn);
        sendBtn=view.findViewById(R.id.add_project_upload_btn);
        progressBar=view.findViewById(R.id.add_project_progressBar);
        checkBox_Public=view.findViewById(R.id.checkBox_Public);
        progressBar.setVisibility(View.GONE);
        user= AddRunProjectFragmentArgs.fromBundle(getArguments()).getUser();
        sendBtn.setOnClickListener(v -> {
            if (!validate()) {
                Toast.makeText(getActivity(), "Please check your input", Toast.LENGTH_SHORT).show();
                return;
            }
            save();
        });
        cancelBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            AddRunProjectFragmentDirections.ActionAddRunProjectFragmentToUserHomePageFragment action1 = AddRunProjectFragmentDirections.actionAddRunProjectFragmentToUserHomePageFragment(user,p);
            Navigation.findNavController(view).navigate(action1);
        });
        setHasOptionsMenu(true);

        return view;
    }
    private void save() {
        progressBar.setVisibility(View.VISIBLE);
        sendBtn.setEnabled(false);
        cancelBtn.setEnabled(false);
        p.setProjectName(p_name.getText().toString());
        p.setTtl(p_endDate.getText().toString());
        p.setRunDistance("0");
        p.setProjectDetails(p_details.getText().toString());
        p.setTotalDistance(p_target.getText().toString());
        p.setPublic(checkBox_Public.isChecked());
        Model.instance.addProject(p, () -> Navigation.findNavController(sendBtn).navigateUp());

    }
    private boolean validate() {
        return (p_name.getText().length() > 2 && p_details.getText().length() > 2&&p_target!=null&&checkBox_Public!=null&&p_endDate!=null);
    }
}