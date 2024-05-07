package com.tutorials.reminderappsamsung.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddReminderByImage extends AppCompatActivity {

    TextView titleReminderByImage;
    CheckBox important;
    EditText title, location, note;
    TimePicker timePicker;
    DatePicker datePicker;
    Button date, time;

    RelativeLayout relativeLayoutTime, outside;

    LinearLayout datetimeButton;

    TextView cancel, save, timeTV;

    boolean relativeLayoutTimeChecked = true;

    private Calendar calendar = Calendar.getInstance();

    private Reminder mReminder;

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private ImageView microphone;

    String formTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_reminder);

        initById();

        mReminder = (Reminder) Objects.requireNonNull(getIntent().getExtras()).get("reminder_item_by_image");

        setupDateTimePickerUpdate(mReminder.getDate(), mReminder.getTime());
        setupTitleLocationNote(mReminder.getTitle(), mReminder.getLocation());

        ElementInReminderOnClick();
    }

    private void setupTitleLocationNote(String strTitle, String strlocation) {
        if(!strlocation.equals("")){
            location.setText(strlocation);
        }
        if(!strTitle.equals("")){
            title.setText(strTitle);
            save.setEnabled(true);
            save.setText("Save");
        }else {
            save.setEnabled(false);
        }
    }

    private void initById() {

//        titleReminderByImage = findViewById(R.id.title);
//        titleReminderByImage.setText("Remider By Image");

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

//        timePicker.setVisibility(View.GONE);
//        datePicker.setVisibility(View.GONE);
//        datetimeButton.setVisibility(View.GONE);


        outside = findViewById(R.id.outside);
        relativeLayoutTime = findViewById(R.id.time);

        cancel = findViewById(R.id.cancel);
        save = findViewById(R.id.save);
        timeTV = findViewById(R.id.timeTV);
        //Microphone
        microphone = findViewById(R.id.microphone);

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

    private void setupDateTimePickerUpdate(String dateString, String timeString) {

        if(!dateString.equals("")){
            // Phân tích chuỗi ngày thành các thành phần
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd yyyy", Locale.getDefault());
            try {
                calendar.setTime(Objects.requireNonNull(dateFormat.parse(dateString)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Thiết lập DatePicker từ ngày được phân tích
            datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH), null);


            SimpleDateFormat inputFormat = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd yyyy", Locale.forLanguageTag("vi"));

            try {
                Date date = inputFormat.parse(dateString);
                String outputDate = outputFormat.format(date);
                SimpleDateFormat dateFormat1 = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
                //Log.v("TAGY", "outputDate " + outputDate);
                try {
                    calendar.setTime(Objects.requireNonNull(dateFormat1.parse(outputDate)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // Thiết lập DatePicker từ ngày được phân tích
                datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH), null);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(!timeString.equals("")){
            // Phân tích chuỗi thời gian thành các thành phần
            String[] timeParts = timeString.split(":");
            Log.v("TAGY", "timeParts " + timeParts[0] + " " + timeParts[1]);
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            // Thiết lập TimePicker từ thời gian được phân tích
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.setHour(hour);
                timePicker.setMinute(minute);
            } else {
                // Cho các phiên bản Android cũ hơn
                timePicker.setCurrentHour(hour);
                timePicker.setCurrentMinute(minute);
            }
        }
    }

    private void ElementInReminderOnClick() {

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
                Intent intent = new Intent(AddReminderByImage.this, MainActivity.class);
                startActivity(intent);
            }
        });

//        // Disable the save button initially
//        if(title.getText().toString().isEmpty()){
//            save.setEnabled(false);
//        }else {
//
//        }

        //co the dung TextUtils.isEmpty(str)
        TextChangedListenerUpdate();


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strTitle = title.getText().toString();
                if (!strTitle.isEmpty()) {
                    String strNote = note.getText().toString();
                    String strLocation = location.getText().toString();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd yyyy", Locale.getDefault());
                    String formattedDate = dateFormat.format(calendar.getTime());

//                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
//                    String formattedTime = timeFormat.format(calendar.getTime());
//                    Log.v("TAGY+TITLE", formattedTime + " ");
                    Intent intent = new Intent(AddReminderByImage.this, MainActivity.class);

                    if(formTime != null){
                        mReminder.setTime(formTime);
                    }
                    mReminder.setDate(formattedDate);
                    mReminder.setTitle(strTitle);
                    mReminder.setDescription(strNote);
                    mReminder.setLocation(strLocation);
                    mReminder.setImportant(important.isChecked());
                    ReminderDatabase.getInstance(AddReminderByImage.this).getReminderDAO().insert(mReminder);
                    startActivity(intent);
                }
            }
        });
        important.setChecked(mReminder.isImportant());

        important.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if(important.isChecked() != mReminder.isImportant()){
                    save.setEnabled(true);
                    save.setText(R.string.save);
                }else {
                    save.setEnabled(false);
                    save.setText(null);
                }
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if(hourOfDay < 10){
                    formTime = "0"+hourOfDay+":";
                }else {
                    formTime = hourOfDay+":";
                }

                if(minute < 10){
                    formTime += "0"+minute;
                }else {
                    formTime += minute;
                }
                //Log.v("TAGY+TITLE1", formTime);
//                save.setEnabled(true);
//                save.setText(R.string.save);
            }
        });


    }

    private void setupDateTimePicker() {
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year, monthOfYear, dayOfMonth);
//                        save.setEnabled(true);
//                        save.setText(R.string.save);
                    }
                });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
            }
        });
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

    private void TextChangedListenerUpdate() {
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
                save.setEnabled(strTitle.isEmpty());
                save.setEnabled(!strTitle.isEmpty());
                if(!strTitle.isEmpty()){
                    save.setText(R.string.save);
                }
            }
        });

        location.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String strLocation = s.toString();
                // Enable the save button if strTitle is not empty, otherwise disable it
//                save.setEnabled(strLocation.isEmpty());
//                save.setEnabled(!strLocation.isEmpty());
//                if(!strLocation.isEmpty()){
//                    save.setText(R.string.save);
//                }
            }
        });

        note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String strNote = s.toString();
                // Enable the save button if strTitle is not empty, otherwise disable it
//                save.setEnabled(strNote.isEmpty());
//                save.setEnabled(!strNote.isEmpty());
//                if(!strNote.isEmpty()){
//                    save.setText(R.string.save);
//                }
            }
        });
    }

    private void hideKeyboardEdittext(View v) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }catch (NullPointerException message){
            message.printStackTrace();
        }
    }
}