package com.tutorials.reminderappsamsung.data.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.tutorials.reminderappsamsung.data.model.Reminder;

@Database(entities = {Reminder.class}, version = 1)
public abstract class ReminderDatabase extends RoomDatabase {
    public abstract ReminderDAO getReminderDAO();

    private static ReminderDatabase dbInstance;
    public static synchronized ReminderDatabase getInstance(Context context){
        if(dbInstance == null){
            dbInstance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    ReminderDatabase.class,
                    "reminder_db"
            ).allowMainThreadQueries().build();
        }
        return dbInstance;
    }

}

