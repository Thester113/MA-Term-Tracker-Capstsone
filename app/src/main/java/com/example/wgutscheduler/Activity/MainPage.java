package com.example.wgutscheduler.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wgutscheduler.DB.DataBase;
import com.example.wgutscheduler.Entity.Assessment;
import com.example.wgutscheduler.Entity.Course;
import com.example.wgutscheduler.Entity.Term;
import com.example.wgutscheduler.R;
import com.example.wgutscheduler.Utilities.AddSampleData;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

public class MainPage extends AppCompatActivity {
    DataBase db;
    TextView termData;
    TextView coursesPendingTextView;
    TextView coursesCompletedTextView;
    TextView coursesDroppedTextView;
    TextView assessmentsPendingTextView;
    TextView assessmentsPassedTextView;
    TextView assessmentsFailedTextView;
    ExtendedFloatingActionButton hTermListFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = DataBase.getInstance(getApplicationContext());
        termData = findViewById(R.id.termData);
        coursesPendingTextView = findViewById(R.id.coursesPendingTextView);
        coursesCompletedTextView = findViewById(R.id.coursesCompletedTextView);
        coursesDroppedTextView = findViewById(R.id.coursesDroppedTextView);
        assessmentsPendingTextView = findViewById(R.id.assessmentsPendingTextView);
        assessmentsPassedTextView = findViewById(R.id.assessmentsPassedTextView);
        assessmentsFailedTextView = findViewById(R.id.assessmentsFailedTextView);
        hTermListFAB = findViewById(R.id.hTermListFAB);

        updateViews();
        hTermListFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TermList.class);
                startActivity(intent);
            }
        });
    }


    private void updateViews() {
        int term = 0;
        int termComplete = 0;
        int termPending = 0;
        int course = 0;
        int assessment = 0;
        int coursesPending = 0;
        int coursesCompleted = 0;
        int coursesDropped = 0;
        int assessmentsPending = 0;
        int assessmentsPassed = 0;
        int assessmentsFailed = 0;

        try {
            List<Term> termList = db.termDao().getAllTerms();
            List<Course> courseList = db.courseDao().getAllCourses();
            List<Assessment> assessmentList = db.assessmentDao().getAllAssessments();

            try {
                for (int i = 0; i < termList.size(); i++) {
                    term = termList.size();
                    if (termList.get(i).getTerm_status().contains("Completed")) termComplete++;
                    if (termList.get(i).getTerm_status().contains("In-Progress")) termPending++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                for (int i = 0; i < courseList.size(); i++) {
                    course = courseList.size();
                    if (courseList.get(i).getCourse_status().contains("Pending")) coursesPending++;
                    if (courseList.get(i).getCourse_status().contains("In-Progress"))
                        coursesPending++;
                    if (courseList.get(i).getCourse_status().contains("Completed"))
                        coursesCompleted++;
                    if (courseList.get(i).getCourse_status().contains("Dropped")) coursesDropped++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                for (int i = 0; i < assessmentList.size(); i++) {
                    assessment = assessmentList.size();
                    if (assessmentList.get(i).getAssessment_status().contains("Pending"))
                        assessmentsPending++;
                    if (assessmentList.get(i).getAssessment_status().contains("Passed"))
                        assessmentsPassed++;
                    if (assessmentList.get(i).getAssessment_status().contains("Failed"))
                        assessmentsFailed++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        termData.setText(String.valueOf(term));
        coursesPendingTextView.setText(String.valueOf(coursesPending));
        coursesCompletedTextView.setText(String.valueOf(coursesCompleted));
        coursesDroppedTextView.setText(String.valueOf(coursesDropped));
        assessmentsPendingTextView.setText(String.valueOf(assessmentsPending));
        assessmentsFailedTextView.setText(String.valueOf(assessmentsFailed));
        assessmentsPassedTextView.setText(String.valueOf(assessmentsPassed));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateViews();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.populateDBMenu:
                AddSampleData addSampleData = new AddSampleData();
                addSampleData.populate(getApplicationContext());
                updateViews();
                Toast.makeText(this, "LocalDB Populated", Toast.LENGTH_SHORT);
                return true;
            case R.id.resetDBMenu:
                db.clearAllTables();
                updateViews();
                Toast.makeText(this, "LocalDB Reset", Toast.LENGTH_SHORT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}