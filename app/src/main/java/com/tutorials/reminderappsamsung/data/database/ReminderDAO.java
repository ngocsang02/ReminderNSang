package com.tutorials.reminderappsamsung.data.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.tutorials.reminderappsamsung.data.model.Reminder;

import java.util.List;

@Dao
public interface ReminderDAO {
    @Insert
    void insert(Reminder reminder);

    @Delete
    void deleteReminderItem(Reminder reminder);

    @Query("SELECT * FROM reminder_table")
    List<Reminder> getAllReminder();

    @Query("SELECT * FROM reminder_table WHERE reminder_title=:title")
    List<Reminder> checkReminder(String title);

    @Update
    void updateReminderItem(Reminder reminder);

    @Query("SELECT * FROM reminder_table WHERE reminder_state = 0")
    List<Reminder> getNoCompletedReminder();

    @Query("SELECT * FROM reminder_table WHERE reminder_state = 1")
    List<Reminder> getCompletedReminder();

    @Query("SELECT * FROM reminder_table WHERE reminder_important = true")
    List<Reminder> getImportantReminder();
}