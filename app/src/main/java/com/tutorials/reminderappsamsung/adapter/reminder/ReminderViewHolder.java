package com.tutorials.reminderappsamsung.adapter.reminder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tutorials.reminderappsamsung.R;

public class ReminderViewHolder extends RecyclerView.ViewHolder {

    CheckBox checkBoxReminderItem;
    TextView titleReminderItem, dateTimeReminderItem, locationReminderItem, noteReminderItem, defaultNote;

    ImageView updateReminderItem, deleteReminderItem, starReminderItem;
    CardView cardViewReminderItem;


    public ReminderViewHolder(@NonNull View itemView) {
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
