package com.tutorials.reminderappsamsung.adapter.searchview;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.tutorials.reminderappsamsung.R;

public class MyViewHolder extends RecyclerView.ViewHolder {

    CheckBox checkBoxReminderItem;
    TextView titleReminderItem, dateTimeReminderItem, locationReminderItem, noteReminderItem, defaultNote;

    ImageView updateReminderItem, deleteReminderItem, starReminderItem;
    CardView cardViewReminderItem;


    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        //Text
        titleReminderItem = itemView.findViewById(R.id.titleReminderItem);
        dateTimeReminderItem = itemView.findViewById(R.id.dateTimeReminderItem);
        locationReminderItem = itemView.findViewById(R.id.locationReminderItem);
        noteReminderItem = itemView.findViewById(R.id.descriptionReminderItem);
        defaultNote = itemView.findViewById(R.id.noteReminderItem);

        //ImageView
        starReminderItem = itemView.findViewById(R.id.starReminderItem);

        //Click
        checkBoxReminderItem = itemView.findViewById(R.id.checkboxReminderItem);
        updateReminderItem = itemView.findViewById(R.id.updateReminderItem);
        deleteReminderItem = itemView.findViewById(R.id.deleteReminderItem);

        cardViewReminderItem = itemView.findViewById(R.id.cardViewReminderItem);
    }
}
