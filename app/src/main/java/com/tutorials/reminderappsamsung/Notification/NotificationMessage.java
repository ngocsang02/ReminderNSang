package com.tutorials.reminderappsamsung.Notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tutorials.reminderappsamsung.MainActivity;
import com.tutorials.reminderappsamsung.R;

public class NotificationMessage extends AppCompatActivity {

    TextView message_title, message_datetime, message_description, message_location;
    ImageView btn_finish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_message);

        Bundle bundle = getIntent().getExtras();     //call the data which is passed by another intent
        message_title = findViewById(R.id.message_title);
        message_datetime = findViewById(R.id.message_datetime);
        message_description = findViewById(R.id.message_description);
        message_location = findViewById(R.id.message_location);
        btn_finish = findViewById(R.id.btn_finish_notification);


        //String message = bundle.getString("message");
        if (bundle != null) {
            String message_title_text = bundle.getString("message_title");
            String message_datetime_text = bundle.getString("message_datetime");
            String message_location_text = bundle.getString("message_location");
            String message_description_text = bundle.getString("message_description");
            if(message_title_text != null && !message_title_text.equals("")){
                message_title.setText(message_title_text);
            }
            if(message_datetime_text != null && !message_datetime_text.equals("")){
                message_datetime.setText(message_datetime_text);
            }
            if(message_location_text != null && !message_location_text.equals("")){
                message_location.setVisibility(View.VISIBLE);
                message_location.setText(message_location_text);
            }else {
                message_location.setVisibility(View.GONE);
            }
            if(message_description_text != null && !message_description_text.equals("")){
                message_description.setVisibility(View.VISIBLE);
                message_description.setText(message_description_text);
            }else {
                message_description.setVisibility(View.GONE);
            }

        } else {
            message_title.setText("No message received");
            message_datetime.setText("No message received");
            message_location.setText("No message received");
            message_description.setText("No message received");
        }

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationMessage.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}