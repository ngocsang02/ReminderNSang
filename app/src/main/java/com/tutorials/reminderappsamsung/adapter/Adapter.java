package com.tutorials.reminderappsamsung.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tutorials.reminderappsamsung.R;
import com.tutorials.reminderappsamsung.data.database.ReminderDatabase;
import com.tutorials.reminderappsamsung.data.model.Reminder;
import com.tutorials.reminderappsamsung.ui.UpdateReminderItem;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<MyViewHolder> implements Filterable{

    List<Reminder> reminderList;
    Context context;
    private List<Reminder> reminderListOld;

    private ClickUpdateItemReminder clickUpdateItemReminder;

    public interface ClickUpdateItemReminder{

        void updateItemReminder(Reminder reminder);
        void deleteItemReminder(Reminder reminder);

        void updateItemReminderComplete(Reminder reminder);

        void updateItemReminderNoComplete(Reminder reminder);
    }
    public Adapter(List<Reminder> reminderList, Context context, ClickUpdateItemReminder clickUpdateItemReminder) {
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
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.reminder_list,
                        parent,
                        false
                )
        );
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.titleReminderItem.setText(reminderList.get(position).getTitle());
        holder.dateTimeReminderItem.setText(reminderList.get(position).getDate() + " " + reminderList.get(position).getTime());
        if (reminderList.get(position).getLocation().equals("")){
            holder.locationReminderItem.setVisibility(View.GONE);
        }else {
            holder.locationReminderItem.setText(reminderList.get(position).getLocation());
        }
        if (reminderList.get(position).getDescription().equals("")){
            holder.noteReminderItem.setVisibility(View.GONE);
            holder.defaultNote.setVisibility(View.GONE);
        }else {
            holder.noteReminderItem.setText(reminderList.get(position).getDescription());
        }

//        holder.updateReminderItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickUpdateItemReminder.updateItemReminder(reminderList.get(position));
//            }
//        });
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

//        holder.deleteReminderItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickUpdateItemReminder.deleteItemReminder(reminderList.get(position));
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString();
                if(strSearch.isEmpty()){
                    reminderList = reminderListOld;
                }else {
                    List<Reminder> newReminderList = new ArrayList<>();
                    for(Reminder rm: ReminderDatabase.getInstance(context).getReminderDAO().getAllReminder()){
                        if(rm.getTitle().toLowerCase().contains(strSearch.toLowerCase())){
                            newReminderList.add(rm);
                        }
                    }
                    reminderList = newReminderList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = reminderList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                reminderList = (List<Reminder>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
