package com.tutorials.reminderappsamsung.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tutorials.reminderappsamsung.MainActivity;
import com.tutorials.reminderappsamsung.R;
import com.tutorials.reminderappsamsung.adapter.searchview.Adapter;
import com.tutorials.reminderappsamsung.data.database.ReminderDatabase;
import com.tutorials.reminderappsamsung.data.model.Reminder;

import java.util.ArrayList;
import java.util.List;

public class SearchViewActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;

    ImageButton btn_back;
    EditText search_reminder;

    Adapter adapter;

    List<Reminder> listReminder;

    ImageView imgNoReminder, micro;

    TextView textNoReminder;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);

        btn_back = findViewById(R.id.back_btn);
        search_reminder = findViewById(R.id.search_reminder_input);
        listReminder = ReminderDatabase.getInstance(SearchViewActivity.this).getReminderDAO().getAllReminder();
        imgNoReminder = findViewById(R.id.image_no_reminder);
        textNoReminder = findViewById(R.id.no_reminder);
        recyclerView = findViewById(R.id.search_reminder_recycler_view);
        micro = findViewById(R.id.mic_search_reminder);
        adapter = new Adapter(listReminder, this, new Adapter.ClickUpdateItemReminder(){
            @Override
            public void updateItemReminder(Reminder reminder){
                clickUpdateReminderItem(reminder);
            }

            @Override
            public void deleteItemReminder(Reminder reminder) {
                clickDeleteReminderItem(reminder);
            }

            @Override
            public void updateItemReminderComplete(Reminder reminder) {
                clickUpdateReminderCompletedItem(reminder);
            }

            @Override
            public void updateItemReminderNoComplete(Reminder reminder) {
                clickUpdateReminderNoCompletedItem(reminder);
            }
        });

        micro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechToText();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchViewActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        search_reminder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.getFilter().filter(s.toString());
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN"); // Specify Vietnamese language
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Nói gì đó...");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Không hỗ trợ nhập âm thanh", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && !result.isEmpty()) {
                    String recognizedText = result.get(0);
                    // Determine which EditText was focused when the microphone was clicked
                    EditText focusedEditText = getFocusedEditText();
                    if (focusedEditText != null) {
                        focusedEditText.setText(recognizedText);
                    }
                }
            }
        }
    }
    private EditText getFocusedEditText() {
        if (search_reminder.hasFocus()) {
            return search_reminder;
        }else {
            return null;
        }
    }

    private void clickUpdateReminderNoCompletedItem(Reminder reminder) {
        reminder.setState(0);
        ReminderDatabase.getInstance(this).getReminderDAO().updateReminderItem(reminder);
        List<Reminder> noCompletedReminder = ReminderDatabase.getInstance(this).getReminderDAO().getNoCompletedReminder();
        List<Reminder> completedReminder = ReminderDatabase.getInstance(this).getReminderDAO().getCompletedReminder();
        noCompletedReminder.addAll(completedReminder);
        adapter.setData(noCompletedReminder);
        if(ReminderDatabase.getInstance(this).getReminderDAO().getAllReminder().size() < 1){
            textNoReminder.setVisibility(View.VISIBLE);
            imgNoReminder.setVisibility(View.VISIBLE);
        }else {
            textNoReminder.setVisibility(View.GONE);
            imgNoReminder.setVisibility(View.GONE);
        }
    }

    private void clickUpdateReminderCompletedItem(Reminder reminder) {
        reminder.setState(1);
        ReminderDatabase.getInstance(this).getReminderDAO().updateReminderItem(reminder);
        List<Reminder> noCompletedReminder = ReminderDatabase.getInstance(this).getReminderDAO().getNoCompletedReminder();
        List<Reminder> completedReminder = ReminderDatabase.getInstance(this).getReminderDAO().getCompletedReminder();
        noCompletedReminder.addAll(completedReminder);
        adapter.setData(noCompletedReminder);
        if(ReminderDatabase.getInstance(this).getReminderDAO().getAllReminder().size() < 1){
            //emptyReminder.setVisibility(View.VISIBLE);
            textNoReminder.setVisibility(View.VISIBLE);
            imgNoReminder.setVisibility(View.VISIBLE);
        }else {
            textNoReminder.setVisibility(View.GONE);
            imgNoReminder.setVisibility(View.GONE);
        }
    }

    private void clickDeleteReminderItem(Reminder reminder) {
    }

    private void clickUpdateReminderItem(Reminder reminder) {
        Intent itentUpdateReminderItem = new Intent(SearchViewActivity.this, UpdateReminderItem.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_reminder_item", reminder);
        itentUpdateReminderItem.putExtras(bundle);
        startActivity(itentUpdateReminderItem);
    }
}