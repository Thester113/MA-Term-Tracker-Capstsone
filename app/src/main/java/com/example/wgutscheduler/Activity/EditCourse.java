package com.example.wgutscheduler.Activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.format.DateFormat;
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
import androidx.appcompat.widget.SwitchCompat;
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

public class EditCourse extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    DataBase db;
    private static final int PERMISSIONS_REQUEST_SMS = 0;
    boolean courseDeleted;
    boolean courseUpdated;
    ExtendedFloatingActionButton ecSendFAB;
    ExtendedFloatingActionButton updateCourseFAB;
    Date cDate;
    Date endDate;
    Date startDate;
    EditText ecSendNumber;
    EditText editCourseNameText;
    EditText editCourseNotes;
    int assessmentList;
    int courseID;
    Intent intent;
    int mentorList;
    int termID;
    SimpleDateFormat formatter;
    Spinner editCourseStatus;
    String message;
    String name;
    String phone;
    String statusV;
    SwitchCompat editCourseAlert;
    TextView editECourseTerm;
    TextView editSCourseTerm;
    private TextView datePickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);
        intent = getIntent();
        db = DataBase.getInstance(getApplicationContext());
        termID = intent.getIntExtra("termID", -1);
        courseID = intent.getIntExtra("courseID", -1);
        mentorList = intent.getIntExtra("mentorList", -1);
        assessmentList = intent.getIntExtra("assessmentList", -1);
        editCourseNameText = findViewById(R.id.editCourseNameText);
        editCourseStatus = findViewById(R.id.editCourseStatus);
        editSCourseTerm = findViewById(R.id.editSCourseTerm);
        editECourseTerm = findViewById(R.id.editECourseTerm);
        editCourseAlert = findViewById(R.id.editCourseAlert);
        editCourseNotes = findViewById(R.id.editCourseNotes);
        updateCourseFAB = findViewById(R.id.updateCourseFAB);
        ecSendFAB = findViewById(R.id.ecSendFAB);
        ecSendNumber = findViewById(R.id.ecSendNumber);

        setupDatePicker();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.course_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editCourseStatus.setAdapter(adapter);

        editCourseStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                statusV = editCourseStatus.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        setValues();

        updateCourseFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    updateCourse();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (courseUpdated) {
                    Intent intent = new Intent(getApplicationContext(), CourseDetails.class);
                    intent.putExtra("termID", termID);
                    intent.putExtra("courseID", courseID);
                    startActivity(intent);
                }
            }
        });

        ecSendFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messagePermission();
            }
        });
    }

    private void setValues() {
        Course course = new Course();
        course = db.courseDao().getCourse(termID, courseID);
        String name = course.getCourse_name();
        String status = course.getCourse_status();
        String sDate = DateFormat.format("MM/dd/yyyy", course.getCourse_start()).toString();
        String eDate = DateFormat.format("MM/dd/yyyy", course.getCourse_end()).toString();
        boolean alert1 = course.getCourse_alert();
        String notes = course.getCourse_notes();

        editCourseNameText.setText(name);
        editCourseStatus.setSelection(getIndex(editCourseStatus, status));
        editSCourseTerm.setText(sDate);
        editECourseTerm.setText(eDate);
        editCourseAlert.setChecked(alert1);
        editCourseNotes.setText(notes);
    }

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }
        return 0;
    }

    private void setupDatePicker() {

        editSCourseTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerView = findViewById(R.id.editSCourseTerm);
                DialogFragment datePicker = new DatePickerFrag();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        editECourseTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerView = findViewById(R.id.editECourseTerm);
                DialogFragment datePicker = new DatePickerFrag();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
    }

    private void deleteCourse() {
        if (mentorList > 0) {
            Toast.makeText(this, "Must delete all mentors associated with this course before deleting", Toast.LENGTH_SHORT).show();
            return;
        }

        if (assessmentList > 0) {
            Toast.makeText(this, "Must delete all assessments associated with this course before deleting", Toast.LENGTH_SHORT).show();
            return;
        }

        Course course = new Course();
        course = db.courseDao().getCourse(termID, courseID);
        db.courseDao().deleteCourse(course);
        Toast.makeText(this, "Course has been deleted", Toast.LENGTH_SHORT).show();
        courseDeleted = true;
    }

    private void updateCourse() throws ParseException {
        formatter = new SimpleDateFormat("MM/dd/yyyy");
        name = editCourseNameText.getText().toString();
        String sDate = editSCourseTerm.getText().toString();
        String eDate = editECourseTerm.getText().toString();
        String notes = editCourseNotes.getText().toString();
        String cDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
        boolean alert = editCourseAlert.isChecked();
        startDate = formatter.parse(sDate);
        endDate = formatter.parse(eDate);
        this.cDate = formatter.parse(cDate);


        if (name.trim().isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (startDate.after(endDate)) {
            Toast.makeText(this, "Start date cant be after the end date", Toast.LENGTH_SHORT).show();
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
        course.setCourse_id(courseID);
        course.setCourse_name(name);
        course.setCourse_start(startDate);
        course.setCourse_end(endDate);
        course.setCourse_status(statusV);
        course.setCourse_notes(notes);
        course.setCourse_alert(alert);
        db.courseDao().updateCourse(course);
        Toast.makeText(this, name + " has been updated", Toast.LENGTH_SHORT).show();
        courseUpdated = true;
        if (alert) {
            AddCourseAlert();
        }
    }

    public void AddCourseAlert() {
        String sText = name + " starts today!";
        String eText = name + " ends today!";

        setAlert(courseID, startDate, name, sText);
        setAlert(courseID, endDate, name, eText);
    }

    private void setAlert(int ID, Date date, String title, String text) {
        long alertTime = Converter.dateToTimeStamp(date);
        if (date.compareTo(cDate) < 0) {
            return;
        }
        Notifications.setCourseAlert(getApplicationContext(), ID, alertTime, title, text);
        Toast.makeText(this, "Course alarm enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month = month + 1);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = month + "/" + dayOfMonth + "/" + year;
        datePickerView.setText(currentDateString);
    }

    protected void messagePermission() {
        phone = ecSendNumber.getText().toString();
        String notes = editCourseNotes.getText().toString();
        String cName = editCourseNameText.getText().toString();
        message = "Course: " + cName + "  Notes: " + notes;

        if (phone.trim().isEmpty()) {
            Toast.makeText(this, "A phone number is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cName.trim().isEmpty()) {
            Toast.makeText(this, "A course name is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (notes.trim().isEmpty()) {
            Toast.makeText(this, "Please add some notes", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SMS);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            sendSMSMessage();
        }
    }

    protected void sendSMSMessage() {

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, message, null, null);
        Toast.makeText(getApplicationContext(), "SMS message sent successfully", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        if (requestCode == PERMISSIONS_REQUEST_SMS) {
            if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null, message, null, null);
                Toast.makeText(getApplicationContext(), "SMS message sent successfully", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "SMS message failed, please try again", Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.delete_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.deleteCourseIC) {
            deleteCourse();
            Intent intent = new Intent(getApplicationContext(), TermList.class);
            intent.putExtra("termID", termID);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
