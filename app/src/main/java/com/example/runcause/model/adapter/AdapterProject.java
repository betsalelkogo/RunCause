package com.example.runcause.model.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.runcause.R;
import com.example.runcause.model.Project;
import com.example.runcause.model.Run;
import com.example.runcause.model.intefaces.OnItemClickListener;

import java.util.List;

public class AdapterProject extends RecyclerView.Adapter<MyViewHolder> {
    OnItemClickListener listener;
    private List<Project> data;
    private Fragment fragment;
    public AdapterProject() {
    }

    public AdapterProject(List<Project> data, Fragment fragment) {
        this.data=data;
        this.fragment=fragment;
    }
    public void setData(List<Project> data) {
        this.data=data;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = fragment.getLayoutInflater().inflate(R.layout.ptoject_list_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view, listener);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Project p = data.get(position);
        holder.nameTv.setText(p.getProjectName());
        holder.targetKm.setText(p.getTotalDistance());
        holder.doneKm.setText(p.getRunDistance());
    }

    @Override
    public int getItemCount() {
        if(data==null)
            return 0;
        return data.size();
    }
}