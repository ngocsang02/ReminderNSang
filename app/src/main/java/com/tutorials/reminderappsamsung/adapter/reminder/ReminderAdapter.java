package com.tutorials.reminderappsamsung.adapter.reminder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.tutorials.reminderappsamsung.R;
import com.tutorials.reminderappsamsung.adapter.searchview.Adapter;
import com.tutorials.reminderappsamsung.data.model.Reminder;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderViewHolder>{

    List<Reminder> reminderList;
    Context context;
    private List<Reminder> reminderListOld;

    private Adapter.ClickUpdateItemReminder clickUpdateItemReminder;

    public ReminderAdapter(List<Reminder> reminderList, Context context, Adapter.ClickUpdateItemReminder clickUpdateItemReminder) {
        this.reminderList = reminderList;
        this.context = context;
        this.clickUpdateItemReminder = clickUpdateItemReminder;
        this.reminderListOld = reminderList;
        //notifyDataSetChanged();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Reminder> reminderList){
        this.reminderList = reminderList;
        this.reminderListOld = reminderList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReminderViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.reminder_list,
                        parent,
                        false
                )
        );
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.noteReminderItem.setVisibility(View.VISIBLE);
        holder.defaultNote.setVisibility(View.VISIBLE);
        holder.locationReminderItem.setVisibility(View.VISIBLE);

        if(reminderList.get(position).getState() == 0){
            holder.cardViewReminderItem.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
            holder.titleReminderItem.setText(reminderList.get(position).getTitle());
            holder.dateTimeReminderItem.setText(reminderList.get(position).getDate() + " " + reminderList.get(position).getTime());
            holder.locationReminderItem.setText(reminderList.get(position).getLocation());

            holder.dateTimeReminderItem.setTextColor(ContextCompat.getColor(context, R.color.datetime));
            holder.titleReminderItem.setTextColor(ContextCompat.getColor(context, R.color.black));

        }else if(reminderList.get(position).getState() == 1){
            holder.titleReminderItem.setText(StrikeThrough(reminderList.get(position).getTitle()));
            holder.locationReminderItem.setText(StrikeThrough(reminderList.get(position).getLocation()));
            holder.dateTimeReminderItem.setText(StrikeThrough(reminderList.get(position).getDate() + " " + reminderList.get(position).getTime()));

            holder.dateTimeReminderItem.setTextColor(ContextCompat.getColor(context, R.color.darkGrayColor));
            holder.titleReminderItem.setTextColor(ContextCompat.getColor(context, R.color.darkGrayColor));
        }

        //Location
        if (reminderList.get(position).getLocation().equals("")){
            holder.locationReminderItem.setVisibility(View.GONE);
        }

        if (reminderList.get(position).getDescription().equals("")){
            holder.noteReminderItem.setVisibility(View.GONE);
            holder.defaultNote.setVisibility(View.GONE);
        }else if(reminderList.get(position).getState() == 0){
            holder.noteReminderItem.setText(reminderList.get(position).getDescription());
            holder.defaultNote.setText("Note:");
        }else if(reminderList.get(position).getState() == 1){
            holder.noteReminderItem.setText(StrikeThrough(reminderList.get(position).getDescription()));
            holder.defaultNote.setText(StrikeThrough("Note:"));
        }

        holder.cardViewReminderItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickUpdateItemReminder.updateItemReminder(reminderList.get(position));
            }
        });
        holder.cardViewReminderItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clickUpdateItemReminder.deleteItemReminder(reminderList.get(position));
                return true;
            }
        });

        holder.checkBoxReminderItem.setChecked(reminderList.get(position).getState() == 0 ? false : true);

        holder.checkBoxReminderItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.checkBoxReminderItem.isChecked()){
                    clickUpdateItemReminder.updateItemReminderComplete(reminderList.get(position));
                }else {
                    clickUpdateItemReminder.updateItemReminderNoComplete(reminderList.get(position));
                }
            }
        });

        holder.starReminderItem.setVisibility(reminderList.get(position).isImportant()?View.VISIBLE:View.GONE);

    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public SpannableString StrikeThrough(String text){
        SpannableString spannableString = new SpannableString(text);
        // Apply StrikethroughSpan to a part of the text
        int start = 0; // Starting index of the text to strike through
        int end = text.length(); // Ending index of the text to strike through
        StrikethroughSpan strikethroughSpan = new StrikethroughSpan();
        spannableString.setSpan(strikethroughSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
