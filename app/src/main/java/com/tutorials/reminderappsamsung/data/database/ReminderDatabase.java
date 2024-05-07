package com.tutorials.reminderappsamsung.data.database;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.tutorials.reminderappsamsung.data.model.Reminder;

@Database(entities = {Reminder.class}, version = 2, exportSchema = false)
public abstract class ReminderDatabase extends RoomDatabase {

    //version old = 1 new = 2
    static Migration migration_from_1_to_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
            supportSQLiteDatabase.execSQL("ALTER TABLE reminder_table RENAME COLUMN time TO reminder_time");
        }
    };
    public abstract ReminderDAO getReminderDAO();

    private static ReminderDatabase dbInstance;
    public static synchronized ReminderDatabase getInstance(Context context){
        if(dbInstance == null){
            dbInstance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    ReminderDatabase.class,
                    "reminder_db"
            ).allowMainThreadQueries().addMigrations(migration_from_1_to_2).build();//
        }
        return dbInstance;
    }

}

