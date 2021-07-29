package com.example.wgutscheduler.Activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.wgutscheduler.DB.DataBase;
import com.example.wgutscheduler.Entity.Course;
import com.example.wgutscheduler.R;
import com.example.wgutscheduler.Utilities.Converter;
import com.example.wgutscheduler.Utilities.DatePickerFrag;
import com.example.wgutscheduler.Utilities.Notifications;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddCourse extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final int PERMISSIONS_REQUEST_SMS = 0;
    private static final int REQUEST_READ_PHONE_STATE = 0;

    DataBase db;
    boolean courseAdded;
    Date cDate;
    Date endDate;
    Date startDate;
    EditText acSendNumber;
    EditText addCourseNameText;
    EditText addCourseNotes;
    ExtendedFloatingActionButton acSendFAB;
    int courseID;
    Intent intent;
    int termID;
    SimpleDateFormat formatter;
    Spinner addCourseStatus;
    String message;
    String name;
    String phone;
    String statusV;
    Switch addCourseAlert;
    TextView addECourseTerm;
    TextView addSCourseTerm;
    private TextView datePickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        intent = getIntent();
        db = DataBase.getInstance(getApplicationContext());
        termID = intent.getIntExtra("termID", -1);
        addCourseNameText = findViewById(R.id.addCourseNameText);
        addCourseStatus = findViewById(R.id.addCourseStatus);
        addSCourseTerm = findViewById(R.id.addSCourseTerm);
        addECourseTerm = findViewById(R.id.addECourseTerm);
        addCourseAlert = findViewById(R.id.addCourseAlert);
        addCourseNotes = findViewById(R.id.addCourseNotes);
        acSendFAB = findViewById(R.id.acSendbutton);
        acSendNumber = findViewById(R.id.acSendNumber);

        setupDatePicker();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.course_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addCourseStatus.setAdapter(adapter);

        addCourseStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                statusV = addCourseStatus.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        acSendFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messagePermission();
            }
        });
    }

    private void addCourse() throws ParseException {
        formatter = new SimpleDateFormat("MM/dd/yyyy");
        name = addCourseNameText.getText().toString();
        String sDate = addSCourseTerm.getText().toString();
        String eDate = addECourseTerm.getText().toString();
        String notes = addCourseNotes.getText().toString();
        String cDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
        boolean alert = addCourseAlert.isChecked();
        startDate = formatter.parse(sDate);
        endDate = formatter.parse(eDate);
        this.cDate = formatter.parse(cDate);


        if (name.trim().isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (startDate.after(endDate)) {
            Toast.makeText(this, "Start date cannot be after the end date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (sDate.trim().isEmpty()) {
            Toast.makeText(this, "Start date is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (eDate.trim().isEmpty()) {
            Toast.makeText(this, "End date is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (notes.trim().isEmpty()) {
            notes = " ";
        }

        Course course = new Course();
        course.setTerm_id_fk(termID);
        course.setCourse_name(name);
        course.setCourse_start(startDate);
        course.setCourse_end(endDate);
        course.setCourse_status(statusV);
        course.setCourse_notes(notes);
        course.setCourse_alert(alert);
        db.courseDao().insertCourse(course);
        Toast.makeText(this, name + " has been added", Toast.LENGTH_SHORT).show();
        courseAdded = true;
        if (alert) {
            AddCourseAlert();
        }
    }

    public void AddCourseAlert() {
        Course course = new Course();
        course = db.courseDao().getCurrentCourse(termID);
        courseID = course.getCourse_id();
        String sText = name + "starts today!";
        String eText = name + "ends today!";

        setAlert(courseID, startDate, name, sText);
        setAlert(courseID, endDate, name, eText);
    }

    private void setAlert(int ID, Date date, String title, String text) {
        long alertTime = Converter.dateToTimeStamp(date);
        if (date.compareTo(cDate) < 0) {
            return;
        }
        Notifications.setCourseAlert(getApplicationContext(), ID, alertTime, title, text);
        Toast.makeText(this, "Alarm for " + title + "added", Toast.LENGTH_SHORT).show();
    }

    private void setupDatePicker() {

        addSCourseTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerView = findViewById(R.id.addSCourseTerm);
                DialogFragment datePicker = new DatePickerFrag();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        addECourseTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerView = findViewById(R.id.addECourseTerm);
                DialogFragment datePicker = new DatePickerFrag();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month = month + 1);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        // String currentDateString = month + "/" + dayOfMonth + "/" + year;
        String currentDateString = month + "/" + dayOfMonth + "/" + year;
        datePickerView.setText(currentDateString);
    }

    protected void messagePermission() {
        phone = acSendNumber.getText().toString();
        String notes = addCourseNotes.getText().toString();
        String cName = addCourseNameText.getText().toString();
        message = "Course: " + cName + "  Notes: " + notes;

        if (phone.trim().isEmpty()) {
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cName.trim().isEmpty()) {
            Toast.makeText(this, "Please enter a course name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (notes.trim().isEmpty()) {
            Toast.makeText(this, "Please add some notes", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SMS);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                sendSMSMessage();
            }
        }
    }

    protected void sendSMSMessage() {

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, message, null, null);
        Toast.makeText(getApplicationContext(), "SMS message successfully sent", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        if (requestCode == PERMISSIONS_REQUEST_SMS) {
            if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null, message, null, null);
                Toast.makeText(getApplicationContext(), "SMS message successfully sent", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "SMS message failed, try again", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_course, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addCourseFAB:
                try {
                    addCourse();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (courseAdded) {
                    Intent intent = new Intent(getApplicationContext(), TermDetails.class);
                    intent.putExtra("termID", termID);
                    startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
