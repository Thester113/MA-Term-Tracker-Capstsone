package com.example.wgutscheduler.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import androidx.fragment.app.DialogFragment;
import androidx.room.Database;

import com.example.wgutscheduler.DB.DataBase;
import com.example.wgutscheduler.Entity.Assessment;
import com.example.wgutscheduler.R;
import com.example.wgutscheduler.Utilities.Converter;
import com.example.wgutscheduler.Utilities.DatePickerFrag;
import com.example.wgutscheduler.Utilities.Notifications;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddAssessment extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    public static String LOG_TAG = "Adding assessment";

    DataBase db;
    boolean assessmentAdded;
    Date cDate;
    Date dueDate;
    EditText addAssessmentName;
    int assessmentID;
    int courseID;
    Intent intent;
    int termID;
    SimpleDateFormat formatter;
    Spinner addAssessmentStatus;
    Spinner addAssessmentType;
    String name;
    String status;
    String type;
    SwitchCompat aAlert;
    TextView addAssessmentDueDate;
    private TextView datePickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assessment);
        intent = getIntent();
        db = DataBase.getInstance(getApplicationContext());
        termID = intent.getIntExtra("termID", -1);
        courseID = intent.getIntExtra("courseID", -1);
        addAssessmentName = findViewById(R.id.addAssessmentName);
        addAssessmentType = findViewById(R.id.addAssessmentType);
        addAssessmentStatus = findViewById(R.id.addAssessmentStatus);
        addAssessmentDueDate = findViewById(R.id.addAssessmentDueDate);
        aAlert = findViewById(R.id.aAlert);

        setupDatePicker();
        setupSpinner();
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.assessment_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addAssessmentType.setAdapter(adapter);

        addAssessmentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = addAssessmentType.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.assessment_status_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addAssessmentStatus.setAdapter(adapter2);

        addAssessmentStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                status = addAssessmentStatus.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void addAssessment() throws ParseException {
        formatter = new SimpleDateFormat("MM/dd/yyyy");
        name = addAssessmentName.getText().toString();
        String dDate = addAssessmentDueDate.getText().toString();
        String cDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
        boolean alert = aAlert.isChecked();
        dueDate = formatter.parse(dDate);
        this.cDate = formatter.parse(cDate);

        if (name.trim().isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dDate.trim().isEmpty()) {
            Toast.makeText(this, "A due date is required", Toast.LENGTH_SHORT).show();
            return;
        }

        Assessment assessment = new Assessment();
        assessment.setCourse_id_fk(courseID);
        assessment.setAssessment_name(name);
        assessment.setAssessment_type(type);
        assessment.setAssessment_status(status);
        assessment.setAssessment_due_date(dueDate);
        assessment.setAssessment_alert(alert);
        db.assessmentDao().insertAssessment(assessment);
        Toast.makeText(this, name + " has been added.", Toast.LENGTH_SHORT).show();
        Log.d(LOG_TAG, name + " has been added.");
        assessmentAdded = true;
        if (alert) {
            AddAssessmentAlert();
        }
    }

    public void AddAssessmentAlert() {
        Assessment assessment = new Assessment();
        assessment = db.assessmentDao().getCurrentAssessment(courseID);
        assessmentID = assessment.getAssessment_id();
        String sText = name + " is due today!";
        setAlert(assessmentID, dueDate, name, sText);
    }

    private void setAlert(int ID, Date date, String title, String text) {
        long alertTime = Converter.dateToTimeStamp(date);
        if (dueDate.compareTo(cDate) < 0) {
            return;
        }
        Notifications.setAssessmentAlert(getApplicationContext(), ID, alertTime, title, text);
        Toast.makeText(this, name + " due date alarm enabled", Toast.LENGTH_SHORT).show();
    }

    private void setupDatePicker() {

        addAssessmentDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerView = findViewById(R.id.addAssessmentDueDate);
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
        String currentDateString = month + "/" + dayOfMonth + "/" + year;
        datePickerView.setText(currentDateString);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_assessment, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addAssessmentFAB:
                try {
                    addAssessment();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (assessmentAdded) {
                    Intent intent = new Intent(getApplicationContext(), CourseDetails.class);
                    intent.putExtra("termID", termID);
                    intent.putExtra("courseID", courseID);
                    startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
