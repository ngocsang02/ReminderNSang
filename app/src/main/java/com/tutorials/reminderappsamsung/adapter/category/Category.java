package com.tutorials.reminderappsamsung.adapter.category;

import com.tutorials.reminderappsamsung.data.model.Reminder;

import java.util.List;

public class Category {

    private String nameCategory;
    private List<Reminder> mReminder;

    public Category(String nameCategory, List<Reminder> mReminder) {
        this.nameCategory = nameCategory;
        this.mReminder = mReminder;
    }

    public String getNameCategory() {
        return nameCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }

    public List<Reminder> getReminders() {
        return mReminder;
    }

    public void setBooks(List<Reminder> mReminder) {
        this.mReminder = mReminder;
    }
}
