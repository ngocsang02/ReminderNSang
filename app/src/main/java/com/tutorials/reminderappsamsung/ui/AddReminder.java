package com.tutorials.reminderappsamsung.ui;
import static com.tutorials.reminderappsamsung.MainActivity.REQUEST_PERMISSION_CODE;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.RecognizerIntent;
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
import com.tutorials.reminderappsamsung.Notification.AlarmBrodcast;
import com.tutorials.reminderappsamsung.R;
import com.tutorials.reminderappsamsung.data.database.ReminderDatabase;
import com.tutorials.reminderappsamsung.data.model.Reminder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddReminder extends AppCompatActivity {
    CheckBox important;
    EditText title, location, note;
    TimePicker timePicker;
    DatePicker datePicker;
    Button date, time;

    RelativeLayout relativeLayoutTime, outside;

    LinearLayout datetimeButton;

    TextView cancel, save, timeTV;

    boolean relativeLayoutTimeChecked = false;

    private Calendar selectedDateTime = Calendar.getInstance();

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private ImageView microphone;

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

        timePicker.setVisibility(View.GONE);
        datePicker.setVisibility(View.GONE);
        datetimeButton.setVisibility(View.GONE);


        outside = findViewById(R.id.outside);
        relativeLayoutTime = findViewById(R.id.time);

        cancel = findViewById(R.id.cancel);
        save = findViewById(R.id.save);
        timeTV = findViewById(R.id.timeTV);
        //Microphone
        microphone = findViewById(R.id.microphone);

        setupDateTimePicker();

        microphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechToText();
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

                    if(isReminderExist(formattedDate, formattedTime, strTitle, strLocation, strNote) == false){
                        Reminder mReminder = new Reminder(formattedDate, formattedTime, strTitle, strNote, important.isChecked(), strLocation, 0);
                        ReminderDatabase.getInstance(AddReminder.this).getReminderDAO().insert(mReminder);
                        int id = ReminderDatabase.getInstance(AddReminder.this).getReminderDAO().getLastItemId();
                        setAlarm(strTitle, formattedDate, formattedTime, id, strLocation, strNote);
//                        Log.v("TAGY", "ID: " + id);
                        Intent intent = new Intent(AddReminder.this, MainActivity.class);
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

    private void setAlarm(String text, String date, String time, int id, String strLocation, String strNote) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.POST_NOTIFICATIONS};
            requestPermissions(permissions, REQUEST_PERMISSION_CODE);
        }
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);                   //assigining alaram manager object to set alaram

        Intent intent = new Intent(getApplicationContext(), AlarmBrodcast.class);
        intent.putExtra("event", text);                                                       //sending data to alarm class to create channel and notification
        intent.putExtra("time", date);
        intent.putExtra("date", time);
        intent.putExtra("idNotification", id);
        intent.putExtra("location", strLocation);
        intent.putExtra("description", strNote);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        String dateandtime = date + " " + time;
        Log.v("TAGY", dateandtime);
        DateFormat formatter;
        if(dateandtime.contains("thg")){
            formatter = new SimpleDateFormat("EEE, MMM dd yyyy hh:mm", Locale.forLanguageTag("vi"));
        }else {
            formatter = new SimpleDateFormat("EEE, MMM dd yyyy hh:mm", Locale.ENGLISH);
        }
        try {
            Date date1 = formatter.parse(dateandtime);
            Calendar calendar = Calendar.getInstance();
            assert date1 != null;
            calendar.setTime(date1);
//            Log.v("TAGY", "Date: " + date1.getDate() + " " + date1.getHours() + " " + date1.getMinutes());
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(getApplicationContext(), "Alarm Set", Toast.LENGTH_SHORT).show();

        } catch (ParseException e) {
            e.printStackTrace();
        }
//        Intent intentBack = new Intent(getApplicationContext(), MainActivity.class);                //this intent will be called once the setting alaram is completes
//        intentBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intentBack);                                                                  //navigates from adding reminder activity ot mainactivity
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

    // Method to determine which EditText was focused
    private EditText getFocusedEditText() {
        if (title.hasFocus()) {
            return title;
        } else if (location.hasFocus()) {
            return location;
        } else if (note.hasFocus()) {
            return note;
        } else {
            return null;
        }
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

    private boolean isReminderExist(String date, String time, String title, String location, String description){
        List<Reminder> listCheck = ReminderDatabase.getInstance(this).getReminderDAO().checkReminder(date, time, title, location, description);
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