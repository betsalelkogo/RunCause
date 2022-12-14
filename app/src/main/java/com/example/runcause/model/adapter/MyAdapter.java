package com.example.runcause.model.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.runcause.R;
import com.example.runcause.model.Run;
import com.example.runcause.model.intefaces.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    OnItemClickListener listener;
    private List<Run> data;
    private Fragment fragment;
    public MyAdapter() {
    }

    public MyAdapter(List<Run> data,Fragment fragment) {
        this.data=data;
        this.fragment=fragment;
    }
    public void setData(List<Run> data) {
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
        View view = fragment.getLayoutInflater().inflate(R.layout.run_list_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view, listener);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Run r = data.get(position);
        holder.date.setText(r.getDate());
        holder.time.setText(getTimerText(Float.parseFloat(r.getTime())));
    }

    @Override
    public int getItemCount() {
        if(data==null)
            return 0;
        return data.size();
    }
    private String getTimerText(float time)
    {
        int rounded = (int) Math.round(time);

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
