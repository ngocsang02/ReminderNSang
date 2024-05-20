package com.tutorials.reminderappsamsung.adapter.category;


import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tutorials.reminderappsamsung.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder {

    TextView tvNameCategory;
    RecyclerView rcvReminder;
    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        tvNameCategory = itemView.findViewById(R.id.tv_name_category);
        rcvReminder = itemView.findViewById(R.id.rcv_reminder);
    }
}
