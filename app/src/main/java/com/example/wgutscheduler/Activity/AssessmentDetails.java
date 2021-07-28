package com.example.wgutscheduler.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wgutscheduler.DB.DataBase;
import com.example.wgutscheduler.Entity.Assessment;
import com.example.wgutscheduler.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class AssessmentDetails extends AppCompatActivity {
    DataBase db;
    int termID;
    int courseID;
    int assessmentID;
    Intent intent;
    TextView adName;
    TextView adType;
    TextView adStatus;
    TextView adDueDate;
    TextView adAlert;
    ExtendedFloatingActionButton adEditFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_details);
        intent = getIntent();
        db = DataBase.getInstance(getApplicationContext());
        termID = intent.getIntExtra("termID", -1);
        courseID = intent.getIntExtra("courseID", -1);
        assessmentID = intent.getIntExtra("assessmentID", -1);
        adName = findViewById(R.id.adName);
        adType = findViewById(R.id.adType);
        adStatus = findViewById(R.id.adStatus);
        adDueDate = findViewById(R.id.adDueDate);
        adAlert = findViewById(R.id.adAlert);
        adEditFAB = findViewById(R.id.adEditFAB);

        setValues();

        adEditFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditAssessment.class);
                intent.putExtra("termID", termID);
                intent.putExtra("courseID", courseID);
                intent.putExtra("assessmentID", assessmentID);
                startActivity(intent);
            }
        });
    }

    private void setValues() {
        Assessment assessment = new Assessment();
        assessment = db.assessmentDao().getAssessment(courseID, assessmentID);
        String name = assessment.getAssessment_name();
        String type = assessment.getAssessment_type();
        String status = assessment.getAssessment_status();
        String dDate = DateFormat.format("MM/dd/yyyy", assessment.getAssessment_due_date()).toString();
        boolean alert1 = assessment.getAssessment_alert();
        String alert = "Off";

        if (alert1) {
            alert = "On";
        }
        adName.setText(name);
        adType.setText(type);
        adStatus.setText(status);
        adDueDate.setText(dDate);
        adAlert.setText(alert);
    }
}
