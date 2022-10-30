package com.example.runcause.model.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.runcause.R;
import com.example.runcause.model.intefaces.OnItemClickListener;


public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView nameTv;
    TextView targetKm;
    TextView doneKm;

    public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
        super(itemView);
        nameTv = itemView.findViewById(R.id.listrow_name_tv);
        targetKm = itemView.findViewById(R.id.listrow_target_km_tv);
        doneKm = itemView.findViewById(R.id.listrow_km_done_tv2);
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
