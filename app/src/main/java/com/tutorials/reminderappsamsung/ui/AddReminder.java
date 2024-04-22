package com.tutorials.reminderappsamsung.ui;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tutorials.reminderappsamsung.MainActivity;
import com.tutorials.reminderappsamsung.R;
import com.tutorials.reminderappsamsung.data.database.ReminderDatabase;
import com.tutorials.reminderappsamsung.data.model.Reminder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddReminder extends AppCompatActivity {

    CheckBox important;
    EditText title, location, note;
    TimePicker timePicker;
    DatePicker datePicker;
    Button date, time;

    ImageView camera;

    RelativeLayout relativeLayoutTime, outside;

    LinearLayout datetimeButton;

    TextView cancel, save, timeTV;

    boolean relativeLayoutTimeChecked = false;


    private Calendar selectedDateTime = Calendar.getInstance();

    @SuppressLint({"WrongViewCast", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_reminder);

        datetimeButton = findViewById(R.id.dateTimeButton);
        important = findViewById(R.id.important);
        title = findViewById(R.id.edittextTitle);
        location = findViewById(R.id.editLocation);
        note = findViewById(R.id.editNote);

        //time picker, date picker
        timePicker = findViewById(R.id.timepicker);
        datePicker = findViewById(R.id.datepicker);
        //button date time
        date = findViewById(R.id.btnDate);
        time = findViewById(R.id.btnTime);

        //Camera
        camera = findViewById(R.id.camera);

        timePicker.setVisibility(View.GONE);
        datePicker.setVisibility(View.GONE);
        datetimeButton.setVisibility(View.GONE);


        outside = findViewById(R.id.outside);
        relativeLayoutTime = findViewById(R.id.time);

        cancel = findViewById(R.id.cancel);
        save = findViewById(R.id.save);
        timeTV = findViewById(R.id.timeTV);

        setupDateTimePicker();

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddReminder.this, "Camera Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        relativeLayoutTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardEdittext(v);
                if(relativeLayoutTimeChecked){
                    timeTV.setText(null);
                    relativeLayoutTimeChecked = false;
                    //set time
                    time.setTextColor(0xFF000000);
                    timePicker.setVisibility(View.GONE);
                    //set date
                    date.setTextColor(0xFF000000);
                    datePicker.setVisibility(View.GONE);
                    datetimeButton.setVisibility(View.GONE);
                }else {
                    timeTV.setText(R.string.textviewTime);
                    timeTV.setTypeface(null, Typeface.BOLD);
                    relativeLayoutTimeChecked = true;
                    datetimeButton.setVisibility(View.VISIBLE);
                    //set time
                    time.setTextColor(0xFF5BBCFF);
                    timePicker.setVisibility(View.VISIBLE);
                    //set date
                    date.setTextColor(0xFF000000);
                    datePicker.setVisibility(View.GONE);
                    date.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(timePicker.getVisibility() == View.VISIBLE){
                                timePicker.setVisibility(View.GONE);
                                time.setTextColor(0xFF000000);
                            }
                            date.setTextColor(0xFF5BBCFF);
                            datePicker.setVisibility(View.VISIBLE);
                        }
                    });
                    time.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(datePicker.getVisibility() == View.VISIBLE){
                                datePicker.setVisibility(View.GONE);
                                date.setTextColor(0xFF000000);
                            }
                            time.setTextColor(0xFF5BBCFF);
                            timePicker.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });

        outside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker.setVisibility(View.GONE);
                datePicker.setVisibility(View.GONE);
                datetimeButton.setVisibility(View.GONE);
                timeTV.setTypeface(null);
                timeTV.setText(null);
                hideKeyboardEdittext(v);
            }
        });

        TitleNoteLocationFocus();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddReminder.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Disable the save button initially
        save.setEnabled(false);

        //co the dung TextUtils.isEmpty(str)
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Update strTitle whenever the text in the EditText changes
                String strTitle = s.toString();
                // Enable the save button if strTitle is not empty, otherwise disable it
                save.setEnabled(!strTitle.isEmpty());
                if(!strTitle.isEmpty()){
                    save.setText(R.string.save);
                }
            }
        });



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strTitle = title.getText().toString();
                if (!strTitle.isEmpty()) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd yyyy", Locale.getDefault());
                    String formattedDate = dateFormat.format(selectedDateTime.getTime());

                    String strNote = note.getText().toString();
                    String strLocation = location.getText().toString();

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    String formattedTime = timeFormat.format(selectedDateTime.getTime());
                    //Log.v("TAGY+TITLE", strTitle + " " + strTitle.length() + " " + formattedDate + " " + formattedTime);

                    if(isReminderExist(strTitle) == false){
                        boolean checkAddReminder = true;
                        Intent intent = new Intent(AddReminder.this, MainActivity.class);
                        putData(intent, formattedDate, formattedTime, strTitle, strNote, important.isChecked(), strLocation, 0, checkAddReminder);
                        startActivity(intent);
                    }else {
                        Toast.makeText(AddReminder.this, "Reminder Title exist!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        important.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                Log.v("TAGY", important.isChecked() + "");
                if(important.isChecked()){
                    //important.setBackgroundTintList(ColorStateList.valueOf(R.color.yellow));
                }else {
                    //important.setBackgroundTintList(ColorStateList.valueOf(R.color.black));
                }
            }
        });


    }

    private void TitleNoteLocationFocus() {
        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    timePicker.setVisibility(View.GONE);
                    datePicker.setVisibility(View.GONE);
                    datetimeButton.setVisibility(View.GONE);
                    timeTV.setTypeface(null);
                    timeTV.setText(null);
                }
            }
        });
        location.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    timePicker.setVisibility(View.GONE);
                    datePicker.setVisibility(View.GONE);
                    datetimeButton.setVisibility(View.GONE);
                    timeTV.setTypeface(null);
                    timeTV.setText(null);
                }
            }
        });
        note.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    timePicker.setVisibility(View.GONE);
                    datePicker.setVisibility(View.GONE);
                    datetimeButton.setVisibility(View.GONE);
                    timeTV.setTypeface(null);
                    timeTV.setText(null);
                }
            }
        });
    }

    private boolean isReminderExist(String title){
        List<Reminder> listCheck = ReminderDatabase.getInstance(this).getReminderDAO().checkReminder(title);
        return listCheck != null && !listCheck.isEmpty();
    }

    private void hideKeyboardEdittext(View v) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }catch (NullPointerException message){
            message.printStackTrace();
        }
    }

    private void putData(Intent intent, String formattedDate, String formattedTime, String strTitle, String strNote, boolean important, String strLocation, int state, boolean checkAddReminder) {
        intent.putExtra("CheckReminderAdd", checkAddReminder);
        intent.putExtra("DateReminderAdd", formattedDate);
        intent.putExtra("TimeReminderAdd", formattedTime);
        intent.putExtra("TitleReminderAdd", strTitle);
        intent.putExtra("NoteReminderAdd", strNote);
        intent.putExtra("ImportantReminderAdd", important);
        intent.putExtra("LocationReminderAdd", strLocation);
        intent.putExtra("StateReminderAdd", state);
    }

    private void setupDateTimePicker() {
        datePicker.init(selectedDateTime.get(Calendar.YEAR), selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        selectedDateTime.set(year, monthOfYear, dayOfMonth);
                    }
                });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedDateTime.set(Calendar.MINUTE, minute);
            }
        });
    }
}