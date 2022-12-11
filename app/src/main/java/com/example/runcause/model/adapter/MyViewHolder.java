package com.example.runcause.model.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.runcause.R;
import com.example.runcause.model.intefaces.OnItemClickListener;


public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView date;
    TextView time;

    public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
        super(itemView);
        date = itemView.findViewById(R.id.date_run_tv);
        time = itemView.findViewById(R.id.run_time_tv);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = getAdapterPosition();
                if (listener != null) {
                    listener.onItemClick(pos, v);
                }
            }
        });
    }
}
