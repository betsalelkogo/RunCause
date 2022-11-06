package com.example.runcause.model.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.runcause.R;
import com.example.runcause.model.intefaces.OnItemClickListener;

public class ViewHolderProject extends RecyclerView.ViewHolder{
    TextView nameTv;
    TextView targetKm;
    TextView doneKm;

    public ViewHolderProject(@NonNull View itemView, OnItemClickListener listener) {
        super(itemView);
        nameTv = itemView.findViewById(R.id.project_list_rv);
        targetKm = itemView.findViewById(R.id.project_row_target_km_tv1);
        doneKm = itemView.findViewById(R.id.project_row_km_done_tv1);
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
