package com.tutorials.reminderappsamsung.adapter.category;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tutorials.reminderappsamsung.R;
import com.tutorials.reminderappsamsung.adapter.searchview.Adapter;
import com.tutorials.reminderappsamsung.adapter.reminder.ReminderAdapter;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder>{

    private Context mContext;
    private List<Category> mListCategory;
    Adapter adapter;

    private Adapter.ClickUpdateItemReminder clickUpdateItemReminder;



    public CategoryAdapter(List<Category> mListCategory, Context mContext, Adapter.ClickUpdateItemReminder clickUpdateItemReminder) {
        this.mContext = mContext;
        this.mListCategory = mListCategory;
        this.clickUpdateItemReminder = clickUpdateItemReminder;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Category> mListCategory){
        this.mListCategory = mListCategory;
        notifyDataSetChanged();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Category> list, Adapter adapter){
        this.mListCategory = list;
        this.adapter = adapter;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_category,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = mListCategory.get(position);
        if(category == null){
            return;
        }
        holder.tvNameCategory.setText(category.getNameCategory());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        holder.rcvReminder.setLayoutManager(linearLayoutManager);

        ReminderAdapter reminderAdapter = new ReminderAdapter(
                category.getReminders(),
                mContext,
                clickUpdateItemReminder
        );
        reminderAdapter.setData(category.getReminders());

        holder.rcvReminder.setAdapter(reminderAdapter);
    }



    @Override
    public int getItemCount() {
        if(mListCategory != null){
            return mListCategory.size();
        }
        return 0;
    }
}
