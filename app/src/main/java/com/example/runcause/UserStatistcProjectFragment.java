package com.example.runcause;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.runcause.UI.RunsListFragmentViewModel;
import com.example.runcause.UI.UserRunListFragmentViewModel;
import com.example.runcause.model.Model;
import com.example.runcause.model.Project;
import com.example.runcause.model.User;
import com.example.runcause.model.intefaces.AddProjectListener;

public class UserStatistcProjectFragment extends Fragment {
    View view;
    TextView projectName;
    UserRunListFragmentViewModel viewModel;
    Button close, delete;
    User user;
    Project project;
    int progress = 0;
    ProgressBar progressBar;
    TextView textViewProgress;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(UserRunListFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_statistc_project, container, false);
        user = UserStatistcProjectFragmentArgs.fromBundle(getArguments()).getUser();
        project = UserStatistcProjectFragmentArgs.fromBundle(getArguments()).getProject();
        projectName=view.findViewById(R.id.project_name_tv);
        projectName.setText(project.getProjectName());
        close = view.findViewById(R.id.statistic_btn);
        progressBar = view.findViewById(R.id.progress_bar);
        textViewProgress = view.findViewById(R.id.text_view_progress);
        delete=view.findViewById(R.id.delete_project_btn);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                project.setDeleted(true);
                Model.instance.addProject(project, new AddProjectListener() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(MyApplication.getContext(),"This Project is noe Deleted",Toast.LENGTH_LONG).show();
                        UserStatistcProjectFragmentDirections.ActionUserStatistcProjectFragmentToUserHomePageFragment action = UserStatistcProjectFragmentDirections.actionUserStatistcProjectFragmentToUserHomePageFragment(user, project);
                        Navigation.findNavController(view).navigate(action);
                    }
                });
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserStatistcProjectFragmentDirections.ActionUserStatistcProjectFragmentToUserHomePageFragment action = UserStatistcProjectFragmentDirections.actionUserStatistcProjectFragmentToUserHomePageFragment(user, project);
                Navigation.findNavController(view).navigate(action);
            }
        });

    init();
        return view;
    }

    public void init() {
        TableLayout stk = (TableLayout) view.findViewById(R.id.table_main);
        TableRow tbrow0 = new TableRow(MyApplication.getContext());
        TextView tv0 = new TextView(MyApplication.getContext());
        tv0.setText(" Run.No ");
        tv0.setTextColor(Color.WHITE);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(MyApplication.getContext());
        tv1.setText(" User Email ");
        tv1.setTextColor(Color.WHITE);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(MyApplication.getContext());
        tv2.setText(" Run Kilometres ");
        tv2.setTextColor(Color.WHITE);
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(MyApplication.getContext());
        tv3.setText(" Time ");
        tv3.setTextColor(Color.WHITE);
        tbrow0.addView(tv3);
        stk.addView(tbrow0);
        int counter=1;
        for (int i = 0; i < viewModel.getData().getValue().size(); i++) {
            if(project.getId_key().equalsIgnoreCase(viewModel.getData().getValue().get(i).getProjectId())){
                TableRow tbrow = new TableRow(MyApplication.getContext());
                TextView t1v = new TextView(MyApplication.getContext());
                t1v.setText("" + counter);
                counter++;
                t1v.setTextColor(Color.WHITE);
                t1v.setGravity(Gravity.CENTER);
                tbrow.addView(t1v);
                TextView t2v = new TextView(MyApplication.getContext());
                t2v.setText(viewModel.getData().getValue().get(i).getUser());
                t2v.setTextColor(Color.WHITE);
                t2v.setGravity(Gravity.CENTER);
                tbrow.addView(t2v);
                TextView t3v = new TextView(MyApplication.getContext());
                t3v.setText(viewModel.getData().getValue().get(i).getDistance());
                t3v.setTextColor(Color.WHITE);
                t3v.setGravity(Gravity.CENTER);
                tbrow.addView(t3v);
                TextView t4v = new TextView(MyApplication.getContext());
                t4v.setText(getTimerText(viewModel.getData().getValue().get(i).getTime()));
                t4v.setTextColor(Color.WHITE);
                t4v.setGravity(Gravity.CENTER);
                tbrow.addView(t4v);
                stk.addView(tbrow);
            }
        }
        updateProgressBar();

    }
    private void updateProgressBar()
    {
        progress= (int) ((Float.parseFloat(project.getRunDistance())/Float.parseFloat(project.getTotalDistance()))*100);
        progressBar.setProgress(progress);
        textViewProgress.setText(progress + "%");
    }
    private String getTimerText(String time)
    {
        int rounded = (int) Math.round(Float.parseFloat(time));

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours)
    {
        return String.format("%02d",hours) + " : " + String.format("%02d",minutes) + " : " + String.format("%02d",seconds);
    }
}