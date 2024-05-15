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

    @Query("SELECT * FROM reminder_table WHERE (reminder_title=:title AND reminder_date=:date AND reminder_time=:time AND reminder_location=:location AND reminder_description=:description)")
    List<Reminder> checkReminder(String date, String time, String title, String location, String description);

    @Update
    void updateReminderItem(Reminder reminder);

    @Query("SELECT * FROM reminder_table WHERE reminder_state = 0")
    List<Reminder> getNoCompletedReminder();

    @Query("SELECT * FROM reminder_table WHERE reminder_state = 1")
    List<Reminder> getCompletedReminder();

    @Query("SELECT * FROM reminder_table WHERE reminder_important = true")
    List<Reminder> getImportantReminder();

    @Query("SELECT reminder_id FROM reminder_table ORDER BY reminder_id DESC LIMIT 1")
    int getLastItemId();
}