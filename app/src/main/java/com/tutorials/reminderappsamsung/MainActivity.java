package com.tutorials.reminderappsamsung;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tutorials.reminderappsamsung.Notification.AlarmBrodcast;
import com.tutorials.reminderappsamsung.adapter.category.Category;
import com.tutorials.reminderappsamsung.adapter.category.CategoryAdapter;
import com.tutorials.reminderappsamsung.adapter.searchview.Adapter;
import com.tutorials.reminderappsamsung.data.database.ReminderDAO;
import com.tutorials.reminderappsamsung.data.database.ReminderDatabase;
import com.tutorials.reminderappsamsung.data.model.Reminder;
import com.tutorials.reminderappsamsung.detect.BoundingBox;
import com.tutorials.reminderappsamsung.detect.Detector;
import com.tutorials.reminderappsamsung.ui.AddReminder;
import com.tutorials.reminderappsamsung.ui.AddReminderByImage;
import com.tutorials.reminderappsamsung.ui.SearchViewActivity;
import com.tutorials.reminderappsamsung.ui.UpdateReminderItem;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CAMERA_CODE = 100;
    private static final int REQUEST_NOTIFICATIONS_CODE = 101;
    public static final int REQUEST_PERMISSION_CODE = 103;
    FloatingActionButton fab, newReminderFab, cameraFab;
    List<Reminder> listReminder;

    RecyclerView recyclerView;

    ReminderDAO reminderDAO;

//    Adapter adapter;

    DrawerLayout drawerLayout;

    SearchView searchView;

    ImageView emptyReminder;

    Animation rotateOpen;
    Animation rotateClose;
    Animation fromBottom;
    Animation toBottom;

    View transparentBGR;



    Bitmap bitmap;

    Detector detector;

    private Calendar calendar = Calendar.getInstance();

    CategoryAdapter adapter;

    List<Category> mListCategory = new ArrayList<>();

    List<Reminder> noCompletePast = new ArrayList<>();
    List<Reminder> noCompleteFuture = new ArrayList<>();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        createRequestNotify();
        navClickedDrawer();



        // Database:
        ReminderDatabase db = ReminderDatabase.getInstance(this);

        //DAO
        reminderDAO = db.getReminderDAO();

//        // Nhận Intent đã gửi đến
//        Intent intentGet = getIntent();
//
//        // Nhận biến boolean checkAddReminder từ Intent
//        boolean checkAddReminder = intentGet.getBooleanExtra("CheckReminderAdd", false);
//
//        if(checkAddReminder == true){
//            String DateReminderAdd = intentGet.getStringExtra("DateReminderAdd");
//            String TimeReminderAdd = intentGet.getStringExtra("TimeReminderAdd");
//            String TitleReminderAdd = intentGet.getStringExtra("TitleReminderAdd");
//            String NoteReminderAdd = intentGet.getStringExtra("NoteReminderAdd");
//            String LocationReminderAdd = intentGet.getStringExtra("LocationReminderAdd");
//
//            boolean ImportantReminderAdd = intentGet.getBooleanExtra("ImportantReminderAdd", false);
//            int StateReminderAdd = intentGet.getIntExtra("StateReminderAdd", 0);
//            Reminder rm = new Reminder(
//                    DateReminderAdd, TimeReminderAdd,
//                    TitleReminderAdd,NoteReminderAdd, ImportantReminderAdd,
//                    LocationReminderAdd, StateReminderAdd
//            );
//            reminderDAO.insert(rm);
//        }



//        for(Reminder r: listReminder){
//            Log.v("TAGY", r.getId() + " " + r.getTitle() + " " + r.getDescription());
//        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(newReminderFab.getVisibility() == View.GONE){
                    //Edit
                    newReminderFab.setVisibility(View.VISIBLE);
                    newReminderFab.startAnimation(fromBottom);

                    //camera
                    cameraFab.setVisibility(View.VISIBLE);
                    cameraFab.startAnimation(fromBottom);

                    //View
                    transparentBGR.setVisibility(View.VISIBLE);

                    fab.startAnimation(rotateOpen);

                    newReminderFab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(MainActivity.this, "Edit clicked", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, AddReminder.class);
                            startActivity(intent);
                        }
                    });

                    cameraFab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectImage();
                            //Toast.makeText(MainActivity.this, "Camera Clicked", Toast.LENGTH_SHORT).show();
                        }
                    });

                    transparentBGR.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideFab();
                        }
                    });

                }else {
                    hideFab();
                }
            }
        });


        setupAdapter(this);
    }

    private void createRequestNotify() {
        if (
                ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
            return;
        }else {
            String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.POST_NOTIFICATIONS};
            requestPermissions(permissions, REQUEST_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Permission Dennied", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void hideFab() {
        newReminderFab.startAnimation(toBottom);
        cameraFab.startAnimation(toBottom);

        fab.startAnimation(rotateClose);
        //edit restart
        newReminderFab.setVisibility(View.GONE);
        newReminderFab.setOnClickListener(null);
        //camera restart
        cameraFab.setVisibility(View.GONE);
        cameraFab.setOnClickListener(null);
        //View
        transparentBGR.setVisibility(View.GONE);
    }

    private void selectImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.CAMERA};
            requestPermissions(permissions, REQUEST_PERMISSION_CODE);
        }
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null){
                Uri resultUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
//                Bitmap image = null;
//                image = Bitmap.createScaledBitmap(bitmap, 640, 640, false);
                    classifyImage(bitmap);
//                getTextFromImage(bitmap);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else {
                // Không có kết quả hình ảnh được chọn, chuyển về MainActivity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish(); // Đóng Activity hiện tại nếu bạn không muốn quay lại nó khi chuyển về MainActivity
            }
        }
    }

    private void classifyImage(Bitmap frame) {
        detector = new Detector(getBaseContext(), "finaldetectinfposter.tflite", "labels.txt");
        detector.setup();
        List<BoundingBox> boundingBoxes = detector.detect(frame);
        String s = getTextFromImage(frame);
        String textFullPoster = s;
        String location = "";
        location = findLocation(textFullPoster);
        s = s.trim().replaceAll("\\s+", " ");
        s = replaceString(s);
        Log.v("TAGY", s);
        if(boundingBoxes.isEmpty()){
            Intent intent = new Intent(MainActivity.this, AddReminder.class);
            startActivity(intent);
        }else {
            String title = "";
            String daymonthyear = "";
            String hour = "";
            for(BoundingBox bb: boundingBoxes){
                if(bb.clsName.equals("Title")){
                    Bitmap image = Bitmap.createBitmap(frame,
                            (int) (bb.x1*frame.getWidth()),
                            (int) (bb.y1*frame.getHeight()),
                            (int) (bb.x2*frame.getWidth() - bb.x1*frame.getWidth()),
                            (int) (bb.y2*frame.getHeight() - bb.y1*frame.getHeight()));
                    title = getTextFromImage(image);
                    title = title.replaceAll("\n", " ");
                    title = title.trim();
                }
                if(bb.clsName.equals("DayMonthYear")){
                    Bitmap image = Bitmap.createBitmap(frame,
                            (int) (bb.x1*frame.getWidth()),
                            (int) (bb.y1*frame.getHeight()),
                            (int) (bb.x2*frame.getWidth() - bb.x1*frame.getWidth()),
                            (int) (bb.y2*frame.getHeight() - bb.y1*frame.getHeight()));
                    daymonthyear = getTextFromImage(image);
                    daymonthyear = daymonthyear.replaceAll("\n", " ");
                    daymonthyear = daymonthyear.trim();
                    daymonthyear = convertToDate(daymonthyear, s);
                    Log.v("TAGY", daymonthyear);
                }
                if(bb.clsName.equals("Location") && location.equals("")){
                    Bitmap image = Bitmap.createBitmap(frame,
                            (int) (bb.x1*frame.getWidth()),
                            (int) (bb.y1*frame.getHeight()),
                            (int) (bb.x2*frame.getWidth() - bb.x1*frame.getWidth()),
                            (int) (bb.y2*frame.getHeight() - bb.y1*frame.getHeight()));
                    location = getTextFromImage(image);
                    location = location.replaceAll("\n", " ");
                    location = location.trim();
                }
                if(bb.clsName.equals("Hour")){
                    Bitmap image = Bitmap.createBitmap(frame,
                            (int) (bb.x1*frame.getWidth()),
                            (int) (bb.y1*frame.getHeight()),
                            (int) (bb.x2*frame.getWidth() - bb.x1*frame.getWidth()),
                            (int) (bb.y2*frame.getHeight() - bb.y1*frame.getHeight()));
                    hour = getTextFromImage(image);
                    hour = hour.replaceAll("\n", " ");
                    hour = hour.trim();
                    hour = hour.replaceAll("O", "0");
                }
            }

            Log.v("TAGY", "Title: " + title);
            if(title.equals("")){
                //title is null
                hour = processHour(hour);
                if(hour.equals("")){
                    hour = findHour(s);
                    hour = processHour(hour);
                }
//                Log.v("TAGY", "Hour2 " + hour + " " + hour.length());
//                hour = "20:10";
                Reminder reminder = new Reminder(daymonthyear, hour, title, "", false, location, 0);
                Intent intentAddReminderByImage = new Intent(MainActivity.this, AddReminderByImage.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("reminder_item_by_image", reminder);
                intentAddReminderByImage.putExtras(bundle);
                startActivity(intentAddReminderByImage);
            }else {
                //title is not null
                if(daymonthyear.equals("")){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd yyyy", Locale.getDefault());
                    String formattedDate = dateFormat.format(calendar.getTime());
                    daymonthyear = formattedDate;
                }else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd yyyy", Locale.getDefault());
                    String formattedDate = dateFormat.format(calendar.getTime());
                    String check = formattedDate.substring(0, 12);
                    if(check.contains("thg")){
                        SimpleDateFormat inputFormat = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
                        SimpleDateFormat outputFormat = new SimpleDateFormat("EEE, 'thg' M dd yyyy", Locale.forLanguageTag("vi"));

                        try {
                            Date date = inputFormat.parse(daymonthyear);
                            String outputDate = outputFormat.format(date);
                            daymonthyear = outputDate;
                        } catch (java.text.ParseException e) {
                            e.printStackTrace();
                        }
                    }else {
                        SimpleDateFormat inputFormat = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
                        SimpleDateFormat outputFormat = new SimpleDateFormat("EEE, MMM dd yyyy", Locale.ENGLISH);

                        try {
                            Date date = inputFormat.parse(daymonthyear);
                            String outputDate = outputFormat.format(date);
                            daymonthyear = outputDate;
                        } catch (java.text.ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(hour.equals("")){
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    String formattedTime = timeFormat.format(calendar.getTime());
                    hour = formattedTime;
                }else {
                    hour = processHour(hour);
                    if(hour.equals("")){
                        hour = findHour(s);
                        hour = processHour(hour);
                    }
                }
//                Log.v("TAGY", "dmy hour: " + daymonthyear + " " + hour);
                Reminder reminder = new Reminder(daymonthyear, hour, title, "", false, location, 0);
//                reminderDAO.insert(reminder);
//                updateRecyclerView();
                Intent intentAddReminderByImage = new Intent(MainActivity.this, AddReminderByImage.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("reminder_item_by_image", reminder);
                intentAddReminderByImage.putExtras(bundle);
                startActivity(intentAddReminderByImage);
            }

            //Log.v("TAGY", title + " " + daymonthyear + " " + location + " " + hour);
        }
    }
    public static String findLocation(String text){
        List<String> tinhThanh = Arrays.asList(
                "An Giang",
                "Bà Rịa",
                "Vũng Tàu",
                "Bạc Liêu",
                "Bắc Giang",
                "Bắc Kạn",
                "Bắc Ninh",
                "Bến Tre",
                "Bình Định",
                "Bình Dương",
                "Bình Phước",
                "Bình Thuận",
                "Cà Mau",
                "Cao Bằng",
                "Đắk Lắk",
                "Đắk Nông",
                "Điện Biên",
                "Đồng Nai",
                "Đồng Tháp",
                "Gia Lai",
                "Hà Giang",
                "Hà Nam",
                "Hà Tĩnh",
                "Hải Dương",
                "Hậu Giang",
                "Hòa Bình",
                "Hưng Yên",
                "Khánh Hòa",
                "Kiên Giang",
                "Kon Tum",
                "Lai Châu",
                "Lâm Đồng",
                "Lạng Sơn",
                "Lào Cai",
                "Long An",
                "Nam Định",
                "Nghệ An",
                "Ninh Bình",
                "Ninh Thuận",
                "Phú Thọ",
                "Phú Yên",
                "Quảng Bình",
                "Quảng Nam",
                "Quảng Ngãi",
                "Quảng Ninh",
                "Quảng Trị",
                "Sóc Trăng",
                "Sơn La",
                "Tây Ninh",
                "Thái Bình",
                "Thái Nguyên",
                "Thanh Hóa",
                "Thừa Thiên Huế",
                "Tiền Giang",
                "Trà Vinh",
                "Tuyên Quang",
                "Vĩnh Long",
                "Vĩnh Phúc",
                "Yên Bái",
                "Cần Thơ",
                "Đà Nẵng",
                "Hải Phòng",
                "Hà Nội",
                "Hồ Chí Minh"
        );
        String s = "";
        for (String tt: tinhThanh){
            if(text.contains(tt)){
                s = tt;
                break;
            }
        }
        String result = "";
        if(!s.equals("")){
            List<String> stringList = Arrays.asList(text.split("\n"));
            for(String t: stringList){
                if(t.contains(s)){
                    result = t;
                }
            }
        }
        Log.v("TAGY", result);
        return result;
    }

    private String replaceString(String s) {
//        s = s.replaceAll("/ ", "/");
//        s = s.replaceAll(" /", "/");
//        s = s.replaceAll(" / ", "/");
//
//        s = s.replaceAll(": ", ":");
//        s = s.replaceAll(" :", ":");
//        s = s.replaceAll(" : ", ":");
//
//        s = s.replaceAll(". ", ".");
//        s = s.replaceAll(" .", ".");
//        s = s.replaceAll(" . ", ".");
//
//        s = s.replaceAll("- ", "-");
//        s = s.replaceAll(" -", "-");
//        s = s.replaceAll(" - ", "-");

        s = s.replaceAll("\\s*([/:.-])\\s*", "$1");
        return s;
    }

    private String processHour(String hour){
        hour = hour.replaceAll("O", "0");
        hour = hour.replaceAll("o", "0");
        if(hour.length() == 8){
            if(hour.contains("AM")){
                int h = Integer.parseInt(hour.substring(0, 2));
                if(h < 10){
                    return "0" + h +hour.substring(2, 5);
                }else {
                    return h + hour.substring(2, 5);
                }
            }else {
                int h = Integer.parseInt(hour.substring(0, 2));
                h = (h + 12) % 24;
                if(h < 10){
                    return "0" + h +hour.substring(2, 5);
                }else {
                    return h + hour.substring(2, 5);
                }
            }
        }else if(hour.length() == 5){
            return hour;
        }else {
            return "";
        }
    }

    private String findHour(String text) {
        String[] timePatterns = {
                "\\b\\d{1,2}:\\d{2}\\s*[AP]M\\b",  // 03:55 PM
                "\\b\\d{1,2}:\\d{2}\\b"             // 15:10
        };
        String a = "";
        for (String timePattern : timePatterns) {
            a = findAndConvertTimes(text, timePattern);
            if(!a.equals("")){
                return a;
            }
        }
        return a;
    }

    private String findAndConvertTimes(String text, String timePattern) {
        Pattern pattern = Pattern.compile(timePattern);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String timeString = matcher.group();;
            return timeString;
        }
        return "";
    }

    private String getTextFromImage(Bitmap bm){
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        if(!recognizer.isOperational()){
            Toast.makeText(this, "Error Occurred!!!", Toast.LENGTH_SHORT).show();
            return "";
        }else {
            Frame frame = new Frame.Builder().setBitmap(bm).build();
            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for(int i = 0; i < textBlockSparseArray.size(); i++){
                TextBlock textBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");
            }
//            Log.v("TAGY", stringBuilder.toString());
            return stringBuilder.toString();
        }
    }
    private static final Map<String, String> monthMap = new HashMap<>();

    static {
        monthMap.put("Jan", "January");
        monthMap.put("Feb", "February");
        monthMap.put("Mar", "March");
        monthMap.put("Apr", "April");
        monthMap.put("May", "May");
        monthMap.put("Jun", "June");
        monthMap.put("Jul", "July");
        monthMap.put("Aug", "August");
        monthMap.put("Sep", "September");
        monthMap.put("Oct", "October");
        monthMap.put("Nov", "November");
        monthMap.put("Dec", "December");
    }
    public static String convertToDate(String dateString, String s) {
//        dateString = "";
        Date date = null;
        SimpleDateFormat inputFormat = null;
        try{
            if(dateString.contains("/")){
                dateString = dateString.replaceAll("O", "0");
                dateString = dateString.replaceAll("o", "0");
                String[] listDate = dateString.split("/");
                if(listDate.length == 3){
                    dateString = listDate[0].trim()+"/"+ listDate[1].trim() +"/"+listDate[2].trim();
                }else {
                    dateString = listDate[0].trim() + "/" + listDate[1].trim();
                }
                Log.v("TAGY (/):", dateString);
            } else if (dateString.contains("-")) {
                String[] listDate = dateString.split("-");
                listDate[0] = listDate[0].replaceAll("O", "0");
                listDate[0] = listDate[0].replaceAll("o", "0");
                if(listDate.length == 3){
                    listDate[2] = listDate[2].replaceAll("O", "0");
                    listDate[2] = listDate[2].replaceAll("o", "0");
                    dateString = listDate[0].trim()+"-"+ listDate[1].trim() +"-"+listDate[2].trim();
                }else {
                    dateString = listDate[0].trim() + "-" + listDate[1].trim();
                }
                Log.v("TAGY (-):", dateString);
            } else if (dateString.contains(".")) {
                dateString = dateString.replaceAll("O", "0");
                dateString = dateString.replaceAll("o", "0");
                String[] listDate = dateString.split("\\.");
                if(listDate.length == 3){
                    dateString = listDate[0].trim()+"."+ listDate[1].trim() +"."+listDate[2].trim();
                }else {
                    dateString = listDate[0].trim() + "." + listDate[1].trim();
                }
                Log.v("TAGY (.):", dateString);
            }else {
                String[] listDate = dateString.split(" ");
                StringBuilder d = new StringBuilder(listDate[0]);
                for (int i = 1; i < listDate.length; i++){
                    if(listDate[i].length() > 1){
                        listDate[i] = listDate[i].replaceAll("O", "0");
                        listDate[i] = listDate[i].replaceAll("o", "0");
                        d.append(" ").append(listDate[i]);
                    }
                }
                dateString = d.toString();
                Log.v("TAGY (ok):", dateString);
            }

            if (dateString.matches("\\d{2}/\\d{2}")) { // 14/03
                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                dateString += "/" + currentYear;
                inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                date = inputFormat.parse(dateString);
            } else if (dateString.matches("\\d{2}/\\d{2}/\\d{4}")) { // 14/03/2024
                inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                date = inputFormat.parse(dateString);
            } else if (dateString.matches("\\d{2}-\\w{3}")) { // 14-Mar
                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                dateString += "-" + currentYear;
                inputFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                date = inputFormat.parse(dateString);
            } else if (dateString.matches("\\d{2}-\\w{3}-\\d{4}")) { // 14-Mar-2024
                inputFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                date = inputFormat.parse(dateString);
            } else if (dateString.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) { // 05.06.2024
                inputFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
                date = inputFormat.parse(dateString);
            } else if (dateString.matches("\\w+ \\d{1,2} \\d{4}")) { // February 14 2024
                inputFormat = new SimpleDateFormat("MMMM dd yyyy", Locale.ENGLISH);
                date = inputFormat.parse(dateString);
            } else {
                throw new ParseException("Unparseable date: " + dateString, 0);
            }
            if (date == null) {
                throw new ParseException("Unparseable date: " + dateString, 0);
            }

            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
            return outputFormat.format(date);
        } catch (Exception e) {
            // Return current date if parsing fails

            String find = findDMY(s);
            if(!find.equals("")){
                try{
                    if(find.contains("/")){
                        find = find.replaceAll("O", "0");
                        find = find.replaceAll("o", "0");
                        String[] listDate = find.split("/");
                        if(listDate.length == 3){
                            find = listDate[0].trim()+"/"+ listDate[1].trim() +"/"+listDate[2].trim();
                        }else {
                            find = listDate[0].trim() + "/" + listDate[1].trim();
                        }
                        Log.v("TAGY (/):", find);
                    } else if (find.contains("-")) {
                        String[] listDate = find.split("-");
                        listDate[0] = listDate[0].replaceAll("O", "0");
                        listDate[0] = listDate[0].replaceAll("o", "0");
                        if(listDate.length == 3){
                            listDate[2] = listDate[2].replaceAll("O", "0");
                            listDate[2] = listDate[2].replaceAll("o", "0");
                            find = listDate[0].trim()+"-"+ listDate[1].trim() +"-"+listDate[2].trim();
                        }else {
                            find = listDate[0].trim() + "-" + listDate[1].trim();
                        }
                        Log.v("TAGY (-):", find);
                    } else if (find.contains(".")) {
                        find = find.replaceAll("O", "0");
                        find = find.replaceAll("o", "0");
                        String[] listDate = find.split("\\.");
                        if(listDate.length == 3){
                            find = listDate[0].trim()+"."+ listDate[1].trim() +"."+listDate[2].trim();
                        }else {
                            find = listDate[0].trim() + "." + listDate[1].trim();
                        }
                        Log.v("TAGY (.):", find);
                    }else {
                        String[] listDate = find.split(" ");
                        StringBuilder d = new StringBuilder(listDate[0]);
                        for (int i = 1; i < listDate.length; i++){
                            if(listDate[i].length() > 1){
                                listDate[i] = listDate[i].replaceAll("O", "0");
                                listDate[i] = listDate[i].replaceAll("o", "0");
                                d.append(" ").append(listDate[i]);
                            }
                        }
                        find = d.toString();
                        Log.v("TAGY (ok):", find);
                    }

                    if (find.matches("\\d{2}/\\d{2}")) { // 14/03
                        Calendar calendar = Calendar.getInstance();
                        int currentYear = calendar.get(Calendar.YEAR);
                        find += "/" + currentYear;
                        inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                        date = inputFormat.parse(find);
                    } else if (find.matches("\\d{2}/\\d{2}/\\d{4}")) { // 14/03/2024
                        inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                        date = inputFormat.parse(find);
                    } else if (find.matches("\\d{2}-\\w{3}")) { // 14-Mar
                        Calendar calendar = Calendar.getInstance();
                        int currentYear = calendar.get(Calendar.YEAR);
                        find += "-" + currentYear;
                        inputFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                        date = inputFormat.parse(find);
                    } else if (find.matches("\\d{2}-\\w{3}-\\d{4}")) { // 14-Mar-2024
                        inputFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                        date = inputFormat.parse(find);
                    } else if (find.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) { // 05.06.2024
                        inputFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
                        date = inputFormat.parse(find);
                    } else if (find.matches("\\w+ \\d{1,2} \\d{4}")) { // February 14 2024
                        inputFormat = new SimpleDateFormat("MMMM dd yyyy", Locale.ENGLISH);
                        date = inputFormat.parse(find);
                    } else {
                        throw new ParseException("Unparseable date: " + find, 0);
                    }
                    if (date == null) {
                        throw new ParseException("Unparseable date: " + find, 0);
                    }
                    SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
                    return outputFormat.format(date);
                } catch (Exception ex) {
                    SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
                    return outputFormat.format(new Date());
                }
            }
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
            return outputFormat.format(new Date());
        }
    }

    private static String findDMY(String s) {
        // Regular expressions for different date formats
        String[] datePatterns = {
                "\\b\\w+ \\d{1,2} \\d{4}\\b",         // February 14 2024
                "\\b\\w{3} \\d{1,2} \\d{4}\\b",       // Feb 14 2024
                "\\b\\d{2}/\\d{2}/\\d{4}\\b",         // 14/03/2024
                "\\b\\d{2}/\\d{2}\\b",                // 14/03
                "\\b\\d{2}-\\w{3}-\\d{4}\\b",         // 14-Mar-2024
                "\\b\\d{2}-\\w{3}\\b",                // 14-Mar
                "\\b\\d{2}\\.\\d{2}\\.\\d{4}\\b"      // 05.06.2024
        };
        String a = "";
        for (String datePattern : datePatterns) {
            a = findAndConvertDates(s, datePattern);
            if(!a.equals("")){
                break;
            }
        }
        return a;
    }

    private static String findAndConvertDates(String text, String datePattern) {
        Pattern pattern = Pattern.compile(datePattern);
        Matcher matcher = pattern.matcher(text);
        Calendar calendar = Calendar.getInstance();
        while (matcher.find()) {
            String dateString = matcher.group();
            // Check if the date is in "dd/MM" or "dd-MMM" format and add the current year
            if (dateString.matches("\\d{2}/\\d{2}")) {
                dateString += "/" + calendar.get(Calendar.YEAR);
            }
            if(dateString.matches("\\d{2}-\\w{3}")){
                dateString += "-" + calendar.get(Calendar.YEAR);
            }
            String formattedDate = convertDate2(dateString);
            Log.v("TAGY", "Original date: " + dateString + " | Formatted date: " + formattedDate);
            return formattedDate;
        }
        return "";
    }

    private static String convertDate2(String dateString) {
        String[] dateFormats = {
                "MMMM dd yyyy",      // February 14 2024
                "MMM dd yyyy",       // Feb 14 2024
                "dd/MM/yyyy",        // 14/03/2024
                "dd-MMM-yyyy",       // 14-Mar-2024
                "dd.MM.yyyy"         // 05.06.2024
        };

        Date date;

        for (String format : dateFormats) {
            SimpleDateFormat inputFormat = new SimpleDateFormat(format, Locale.ENGLISH);
            try {
                date = inputFormat.parse(dateString);
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
                assert date != null;
                return outputFormat.format(date);
            } catch (Exception e) {
                // Continue to the next format
//                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
//                return outputFormat.format(new Date());
            }
        }
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
        return outputFormat.format(new Date());
    }

    private void checkRequestCamera() {
        //check xem da duoc cap quyen truy cap camera chua
        if(ContextCompat.checkSelfPermission(
                MainActivity.this,
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //chua duoc cap phep
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, REQUEST_CAMERA_CODE);
        }
    }

    private void initUI() {
        //fab
        fab = findViewById(R.id.fab);
        newReminderFab = findViewById(R.id.newReminderFab);
        cameraFab = findViewById(R.id.cameraFab);

        cameraFab.setVisibility(View.GONE);
        newReminderFab.setVisibility(View.GONE);
        //Animation

        rotateOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.to_bottom_anim);

        //View
        transparentBGR = findViewById(R.id.transparentBGR);
        transparentBGR.setVisibility(View.GONE);

        //Image Empty reminder
        emptyReminder = findViewById(R.id.emptyReminderImage);
        emptyReminder.setVisibility(View.GONE);
        //Recycler View
        recyclerView = findViewById(R.id.recyclerview);
    }

    private void setupAdapter(Context context) {
        //Get data roomdb (initdata)
        listReminder = reminderDAO.getNoCompletedReminder();
        List<Reminder> completedReminder = reminderDAO.getCompletedReminder();
        if(reminderDAO.getAllReminder().size() < 1){
            emptyReminder.setVisibility(View.VISIBLE);
        }

        PastAndFutureNoComplete(listReminder);

        Category category1 = new Category("Past", noCompletePast);
        Category category2 = new Category("No Complete", noCompleteFuture);
        Category category3 = new Category("Complete", completedReminder);
        List<Category> categoryList = new ArrayList<>();
        if(!category1.getReminders().isEmpty()){
            categoryList.add(category1);
        }
        if(!category2.getReminders().isEmpty()){
            categoryList.add(category2);
        }
        if(!category3.getReminders().isEmpty()){
            categoryList.add(category3);
        }
        mListCategory = categoryList;
        adapter = new CategoryAdapter(mListCategory, context, new Adapter.ClickUpdateItemReminder(){
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
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }

    private void PastAndFutureNoComplete(List<Reminder> listReminder){
        // Ngày hiện tại
        Date currentDate = new Date();
        if(!listReminder.isEmpty()){
            noCompletePast = new ArrayList<>();
            noCompleteFuture = new ArrayList<>();
            SimpleDateFormat sdf;
            if(!listReminder.get(0).getDate().contains("thg")){
                sdf = new SimpleDateFormat("EEE, MMM dd yyyy", Locale.ENGLISH);
            }else {
                sdf = new SimpleDateFormat("EEE, MMM dd yyyy", Locale.forLanguageTag("vi"));
            }
            // SimpleDateFormat để phân tích cú pháp ngày
            Log.v("TAGY", currentDate.toString());
            // Lọc reminders
            String currentDateString = sdf.format(currentDate);
            for (Reminder reminder : listReminder) {
                try {
                    Date reminderDate = sdf.parse(reminder.getDate());
                    assert reminderDate != null;
                    if (reminderDate.after(currentDate) || currentDateString.equals(reminder.getDate())) {
                        noCompleteFuture.add(reminder);
                    } else {
                        noCompletePast.add(reminder);
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }else {
            noCompletePast = new ArrayList<>();
            noCompleteFuture = new ArrayList<>();
        }

    }

    private void clickUpdateReminderNoCompletedItem(Reminder reminder) {
        reminder.setState(0);
        reminderDAO.updateReminderItem(reminder);
        List<Reminder> noCompletedReminder = reminderDAO.getNoCompletedReminder();
        List<Reminder> completedReminder = reminderDAO.getCompletedReminder();
        //noCompletedReminder.addAll(completedReminder);


        PastAndFutureNoComplete(noCompletedReminder);

        Category category1 = new Category("Past", noCompletePast);
        Category category2 = new Category("No Complete", noCompleteFuture);
        Category category3 = new Category("Complete", completedReminder);
        List<Category> categoryList = new ArrayList<>();

        if(!category1.getReminders().isEmpty()){
            categoryList.add(category1);
        }
        if(!category2.getReminders().isEmpty()){
            categoryList.add(category2);
        }
        if(!category3.getReminders().isEmpty()){
            categoryList.add(category3);
        }
        mListCategory = categoryList;

        adapter.setData(mListCategory);
        if(reminderDAO.getAllReminder().size() < 1){
            emptyReminder.setVisibility(View.VISIBLE);
        }
    }

    private void clickUpdateReminderCompletedItem(Reminder reminder) {
        reminder.setState(1);
        reminderDAO.updateReminderItem(reminder);
        List<Reminder> noCompletedReminder = reminderDAO.getNoCompletedReminder();
        List<Reminder> completedReminder = reminderDAO.getCompletedReminder();
//        noCompletedReminder.addAll(completedReminder);

        PastAndFutureNoComplete(noCompletedReminder);

        Category category1 = new Category("Past", noCompletePast);
        Category category2 = new Category("No Complete", noCompleteFuture);
        Category category3 = new Category("Complete", completedReminder);
        List<Category> categoryList = new ArrayList<>();
        if(!category1.getReminders().isEmpty()){
            categoryList.add(category1);
        }
        if(!category2.getReminders().isEmpty()){
            categoryList.add(category2);
        }
        if(!category3.getReminders().isEmpty()){
            categoryList.add(category3);
        }
        mListCategory = categoryList;

        adapter.setData(mListCategory);
        if(reminderDAO.getAllReminder().size() < 1){
            emptyReminder.setVisibility(View.VISIBLE);
        }
    }

    private void clickDeleteReminderItem(Reminder reminder) {

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Confirm delete reminder")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Delete User
                        cancelAlarm(reminder.getId());
                        reminderDAO.deleteReminderItem(reminder);
                        Toast.makeText(MainActivity.this, "Delete reminder successfully", Toast.LENGTH_SHORT).show();
                        updateRecyclerView();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void cancelAlarm(int requestCode) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(), AlarmBrodcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Cancel the alarm
        am.cancel(pendingIntent);
        Toast.makeText(getApplicationContext(), "Cancel Alarm", Toast.LENGTH_SHORT).show();
        // Show toast or perform any other action
    }

    private void updateRecyclerView() {
        if(getSupportActionBar().getTitle().equals("Completed")){
            listReminder = reminderDAO.getCompletedReminder();
            Category category = new Category("Complete", listReminder);
            List<Category> categoryList = new ArrayList<>();
            if(!category.getReminders().isEmpty()){
                categoryList.add(category);
            }
            mListCategory = categoryList;
        }
        if(getSupportActionBar().getTitle().equals("All")){
            listReminder = reminderDAO.getNoCompletedReminder();
            List<Reminder> completedReminder = reminderDAO.getCompletedReminder();
//            for(Reminder rm: completedReminder){
//                listReminder.add(rm);
//            }

            PastAndFutureNoComplete(listReminder);

            Category category1 = new Category("Past", noCompletePast);
            Category category2 = new Category("No Complete", noCompleteFuture);
            Category category3 = new Category("Complete", completedReminder);
            List<Category> categoryList = new ArrayList<>();
            if(!category1.getReminders().isEmpty()){
                categoryList.add(category1);
            }
            if(!category2.getReminders().isEmpty()){
                categoryList.add(category2);
            }
            if(!category3.getReminders().isEmpty()){
                categoryList.add(category3);
            }
            mListCategory = categoryList;
        }
        if(getSupportActionBar().getTitle().equals("Today")){
            // Lấy thời gian hiện tại
            Date currentTime = new Date();


            // Định dạng thời gian theo "EEE, MMM dd yyyy"
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd yyyy", new Locale("vi", "VN"));
            String formattedDate = sdf.format(currentTime);

            SimpleDateFormat dateFM = new SimpleDateFormat("EEE, MMM dd yyyy", Locale.US);
            String formattedDate1 = dateFM.format(currentTime);

            listReminder = reminderDAO.getAllReminder();
            List<Reminder> reminderTodayComplete = new ArrayList<>();
            List<Reminder> reminderTodayNoComplete = new ArrayList<>();

            for(Reminder rm: listReminder){
                //Log.v("TAGY123", rm.getDate() + " " + formattedDate);
                if(rm.getDate().equals(formattedDate) || rm.getDate().equals(formattedDate1)){
                    if(rm.getState() == 0){
                        reminderTodayNoComplete.add(rm);
                    }else {
                        reminderTodayComplete.add(rm);
                    }
                }
            }
            listReminder = reminderTodayNoComplete;
//            listReminder.addAll(reminderTodayComplete);

            PastAndFutureNoComplete(listReminder);

            Category category1 = new Category("Past", noCompletePast);
            Category category2 = new Category("No Complete", noCompleteFuture);
            Category category3 = new Category("Complete", reminderTodayComplete);
            List<Category> categoryList = new ArrayList<>();
            if(!category1.getReminders().isEmpty()){
                categoryList.add(category1);
            }
            if(!category2.getReminders().isEmpty()){
                categoryList.add(category2);
            }
            if(!category3.getReminders().isEmpty()){
                categoryList.add(category3);
            }
            mListCategory = categoryList;
        }
        if(getSupportActionBar().getTitle().equals("Scheduled")){
            listReminder = reminderDAO.getNoCompletedReminder();
            Category category = new Category("Scheduled", listReminder);
            List<Category> categoryList = new ArrayList<>();
            if(!category.getReminders().isEmpty()){
                categoryList.add(category);
            }
            mListCategory = categoryList;
        }
        if(getSupportActionBar().getTitle().equals("Important")){
            listReminder = reminderDAO.getImportantReminder();

            List<Reminder> reminderComplete = new ArrayList<>();
            List<Reminder> reminderNoComplete = new ArrayList<>();
            for(Reminder rm: listReminder){
                if(rm.getState() == 0){
                    reminderNoComplete.add(rm);
                }else if(rm.getState() == 1){
                    reminderComplete.add(rm);
                }
            }
            PastAndFutureNoComplete(reminderNoComplete);

            Category category1 = new Category("Past", noCompletePast);
            Category category2 = new Category("No Complete", noCompleteFuture);
            Category category3 = new Category("Complete", reminderComplete);
            List<Category> categoryList = new ArrayList<>();
            if(!category1.getReminders().isEmpty()){
                categoryList.add(category1);
            }
            if(!category2.getReminders().isEmpty()){
                categoryList.add(category2);
            }
            if(!category3.getReminders().isEmpty()){
                categoryList.add(category3);
            }
            mListCategory = categoryList;
        }
        adapter.setData(mListCategory);
        if(reminderDAO.getAllReminder().size() < 1){
            emptyReminder.setVisibility(View.VISIBLE);
        }
    }

    private void clickUpdateReminderItem(Reminder reminder) {
        Intent itentUpdateReminderItem = new Intent(MainActivity.this, UpdateReminderItem.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_reminder_item", reminder);
        itentUpdateReminderItem.putExtras(bundle);
        startActivity(itentUpdateReminderItem);
    }

    //reminder with title ton tai chua

    private boolean isReminderExist(String date, String time, String title, String location, String description){
        List<Reminder> listCheck = ReminderDatabase.getInstance(this).getReminderDAO().checkReminder(date, time, title, location, description);
        return listCheck != null && !listCheck.isEmpty();
    }
    @SuppressLint("RestrictedApi")
    private void navClickedDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the listener
        navigationView.setNavigationItemSelectedListener(this);

        // Hide the title
        getSupportActionBar().setTitle("All");

        // Set transparent background
        toolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_all) {
            getSupportActionBar().setTitle("All");
            //showToast("All Clicked");
            listReminder = reminderDAO.getNoCompletedReminder();
            List<Reminder> completedReminder = reminderDAO.getCompletedReminder();
//            for(Reminder rm: completedReminder){
//                listReminder.add(rm);
//            }

            PastAndFutureNoComplete(listReminder);

            Category category1 = new Category("Past", noCompletePast);
            Category category2 = new Category("No Complete", noCompleteFuture);
            Category category3 = new Category("Complete", completedReminder);
            List<Category> categoryList = new ArrayList<>();
            if(!category1.getReminders().isEmpty()){
                categoryList.add(category1);
            }
            if(!category2.getReminders().isEmpty()){
                categoryList.add(category2);
            }
            if(!category3.getReminders().isEmpty()){
                categoryList.add(category3);
            }
            mListCategory = categoryList;

            adapter.setData(mListCategory);
            if(reminderDAO.getAllReminder().size() < 1) {
                emptyReminder.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.nav_today) {
            getSupportActionBar().setTitle("Today");
            //showToast("Today Clicked");

            // Lấy thời gian hiện tại
            Date currentTime = new Date();


            // Định dạng thời gian theo "EEE, MMM dd yyyy"
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd yyyy", new Locale("vi", "VN"));
            String formattedDate = sdf.format(currentTime);

            SimpleDateFormat dateFM = new SimpleDateFormat("EEE, MMM dd yyyy", Locale.US);
            String formattedDate1 = dateFM.format(currentTime);

            listReminder = reminderDAO.getAllReminder();
            List<Reminder> reminderToday;

            List<Reminder> reminderTodayComplete = new ArrayList<>();
            List<Reminder> reminderTodayNoComplete = new ArrayList<>();

            for(Reminder rm: listReminder){
                //Log.v("TAGY123", rm.getDate() + " " + formattedDate);
                if(rm.getDate().equals(formattedDate) || rm.getDate().equals(formattedDate1)){
                    if(rm.getState() == 0){
                        reminderTodayNoComplete.add(rm);
                    }else {
                        reminderTodayComplete.add(rm);
                    }
                }
            }
            reminderToday = reminderTodayNoComplete;
//            reminderToday.addAll(reminderTodayComplete);
            Category category2 = new Category("NoComplete", reminderToday);
            Category category3 = new Category("Complete", reminderTodayComplete);
            List<Category> categoryList = new ArrayList<>();
//        categoryList.add(category1);
            if(!category2.getReminders().isEmpty()){
                categoryList.add(category2);
            }
            if(!category3.getReminders().isEmpty()){
                categoryList.add(category3);
            }
            mListCategory = categoryList;

            adapter.setData(mListCategory);
            if(reminderDAO.getAllReminder().size() < 1){
                emptyReminder.setVisibility(View.VISIBLE);
            }

        } else if (id == R.id.nav_scheduled) {
            getSupportActionBar().setTitle("Scheduled");
            //showToast("Scheduled Clicked");

            listReminder = reminderDAO.getNoCompletedReminder();

            PastAndFutureNoComplete(listReminder);

            Category category1 = new Category("Past", noCompletePast);
            Category category2 = new Category("No Complete", noCompleteFuture);
            List<Category> categoryList = new ArrayList<>();
            if(!category1.getReminders().isEmpty()){
                categoryList.add(category1);
            }
            if(!category2.getReminders().isEmpty()){
                categoryList.add(category2);
            }
            mListCategory = categoryList;

            adapter.setData(mListCategory);
            if(reminderDAO.getAllReminder().size() < 1){
                emptyReminder.setVisibility(View.VISIBLE);
            }

        } else if (id == R.id.nav_important) {
            getSupportActionBar().setTitle("Important");
            //showToast("Important Clicked");
            listReminder = reminderDAO.getImportantReminder();

            List<Reminder> reminderComplete = new ArrayList<>();
            List<Reminder> reminderNoComplete = new ArrayList<>();
            for(Reminder rm: listReminder){
                if(rm.getState() == 0){
                    reminderNoComplete.add(rm);
                }else if(rm.getState() == 1){
                    reminderComplete.add(rm);
                }
            }
            PastAndFutureNoComplete(reminderNoComplete);

            Category category1 = new Category("Past", noCompletePast);
            Category category2 = new Category("No Complete", noCompleteFuture);
            Category category3 = new Category("Complete", reminderComplete);
            List<Category> categoryList = new ArrayList<>();
            if(!category1.getReminders().isEmpty()){
                categoryList.add(category1);
            }

            if(!category2.getReminders().isEmpty()){
                categoryList.add(category2);
            }
            if(!category3.getReminders().isEmpty()){
                categoryList.add(category3);
            }
            mListCategory = categoryList;

            adapter.setData(mListCategory);
            if(reminderDAO.getAllReminder().size() < 1){
                emptyReminder.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.nav_completed) {
            getSupportActionBar().setTitle("Completed");
            //showToast("Completed Clicked");

            listReminder = reminderDAO.getCompletedReminder();

            Category category = new Category("Complete", listReminder);
            List<Category> categoryList = new ArrayList<>();


            if(!category.getReminders().isEmpty()){
                categoryList.add(category);
            }

            mListCategory = categoryList;
            adapter.setData(mListCategory);
            if(reminderDAO.getAllReminder().size() < 1){
                emptyReminder.setVisibility(View.VISIBLE);
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        return super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_search) {// Show a Toast message
            Intent intent = new Intent(MainActivity.this, SearchViewActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        assert searchView != null;
//        searchView.setQueryHint("Search...");
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setMaxWidth(Integer.MAX_VALUE);
//
//        searchView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Hide the title
//                getSupportActionBar().setDisplayShowTitleEnabled(false);
//            }
//        });
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                adapter.getFilter().filter(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adapter.getFilter().filter(newText);
//                return false;
//            }
//        });
        return true;
    }

    @Override
    public void onBackPressed() {

        if(cameraFab.getVisibility() == View.VISIBLE){
            newReminderFab.startAnimation(toBottom);
            cameraFab.startAnimation(toBottom);

            fab.startAnimation(rotateClose);
            //edit restart
            newReminderFab.setVisibility(View.GONE);
            newReminderFab.setOnClickListener(null);
            //camera restart
            cameraFab.setVisibility(View.GONE);
            cameraFab.setOnClickListener(null);
            //View
            transparentBGR.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
        }
    }
}

//        Reminder reminder = new Reminder("Mon, Apr 18 2024", "10:20", "Hoc Toan", "Lam bai so 2", false, "Ha Noi", 0);
//        Reminder reminder1 = new Reminder("Tue, Apr 19 2024", "20:10","Hoc Van", "Lam bai so 3", false, "Ha Noi", 0);
//        Reminder reminder2 = new Reminder("Wed, Apr 20 2024", "05:20", "Hoc T.A", "Lam bai so 4", false, "Ha Noi", 0);
//
//        reminderDAO.insert(reminder);
//        reminderDAO.insert(reminder1);
//        reminderDAO.insert(reminder2);