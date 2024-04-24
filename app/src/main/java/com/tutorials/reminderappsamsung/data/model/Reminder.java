package com.tutorials.reminderappsamsung.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "reminder_table")
public class Reminder implements Serializable {

    @ColumnInfo(name = "reminder_id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "reminder_date")
    private String date;

    @ColumnInfo(name = "reminder_time")
    private String time;

    @ColumnInfo(name = "reminder_title")
    private String title;

    @ColumnInfo(name = "reminder_description")
    private String description;

    @ColumnInfo(name = "reminder_important")
    private boolean important;


    @ColumnInfo(name = "reminder_location")
    private String location;

    @ColumnInfo(name = "reminder_state")
    private int state;


    public Reminder(String date, String time, String title, String description, boolean important, String location, int state) {
        this.date = date;
        this.time = time;
        this.title = title;
        this.description = description;
        this.important = important;
        this.location = location;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
