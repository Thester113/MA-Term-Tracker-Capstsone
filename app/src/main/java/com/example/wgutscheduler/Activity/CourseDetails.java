package com.example.wgutscheduler.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wgutscheduler.DB.DataBase;
import com.example.wgutscheduler.Entity.Assessment;
import com.example.wgutscheduler.Entity.Course;
import com.example.wgutscheduler.Entity.CourseMentor;
import com.example.wgutscheduler.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;




import java.util.List;

public class CourseDetails extends AppCompatActivity {
    DataBase db;
    Intent intent;
    int courseID;
    int termID;
    ListView cdMentorList;
    ListView cdAssessmentList;
    List<CourseMentor> allMentors;
    List<Assessment> allAssessments;
    FloatingActionButton cdAddMentorFAB;
    FloatingActionButton cdAddAssessmentFAB;
    TextView cdName;
    TextView cdStatus;
    TextView cdAlert;
    TextView cdsDate;
    TextView cdeDate;
    TextView cdNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
        intent = getIntent();
        db = DataBase.getInstance(getApplicationContext());
        termID = intent.getIntExtra("termID", -1);
        courseID = intent.getIntExtra("courseID", -1);
        cdMentorList = findViewById(R.id.cdMentorList);
        cdAssessmentList = findViewById(R.id.cdAssessmentList);
        cdAddMentorFAB = findViewById(R.id.cdAddMentorFAB);
        cdAddAssessmentFAB = findViewById(R.id.cdAddAssessmentFAB);
        cdName = findViewById(R.id.cdName);
        cdStatus = findViewById(R.id.cdStatus);
        cdAlert = findViewById(R.id.cdAlert);
        cdsDate = findViewById(R.id.cdSdate);
        cdeDate = findViewById(R.id.cdEdate);
        cdNotes = findViewById(R.id.cdNotes);

        setValues();
        updateLists();

        //Mentors
        cdAddMentorFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddMentor.class);
                intent.putExtra("termID", termID);
                intent.putExtra("courseID", courseID);
                startActivity(intent);
            }
        });

        cdMentorList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getApplicationContext(), MentorDetails.class);
            intent.putExtra("termID", termID);
            intent.putExtra("courseID", courseID);
            intent.putExtra("mentorID", allMentors.get(position).getMentor_id());
            startActivity(intent);
            System.out.println(id);
        });

        //Assessments
        cdAddAssessmentFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddAssessment.class);
                intent.putExtra("termID", termID);
                intent.putExtra("courseID", courseID);
                startActivity(intent);
            }
        });

        cdAssessmentList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getApplicationContext(), AssessmentDetails.class);
            intent.putExtra("termID", termID);
            intent.putExtra("courseID", courseID);
            intent.putExtra("assessmentID", allAssessments.get(position).getAssessment_id());
            startActivity(intent);
            System.out.println(id);
        });

    }

    private void setValues() {
        try{
            Course course = new Course();
            course = db.courseDao().getCourse(termID, courseID);
            String name = course.getCourse_name();
            String status = course.getCourse_status();
            boolean alert1 = course.getCourse_alert();
            String sDate = DateFormat.format("MM/dd/yyyy", course.getCourse_start()).toString();
            String eDate = DateFormat.format("MM/dd/yyyy", course.getCourse_end()).toString();
            String notes = course.getCourse_notes();
            String alert = "Off";
            if (alert1) {
                alert = "On";
            }
            cdName.setText(name);
            cdStatus.setText(status);
            cdAlert.setText(alert);
            cdsDate.setText(sDate);
            cdeDate.setText(eDate);
            cdNotes.setText(notes);

        } catch (NullPointerException e) {
            System.out.print("NullPointerException caught");

        }

    }

    private void updateLists() {
        List<CourseMentor> allMentors = db.MentorDao().getMentorList(courseID);
        ArrayAdapter<CourseMentor> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allMentors);
        cdMentorList.setAdapter(adapter);
        this.allMentors = allMentors;

        adapter.notifyDataSetChanged();

        List<Assessment> allAssessments = db.assessmentDao().getAssessmentList(courseID);
        ArrayAdapter<Assessment> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allAssessments);
        cdAssessmentList.setAdapter(adapter2);
        this.allAssessments = allAssessments;
        adapter2.notifyDataSetChanged();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.tdEditCourseIC) {
            Intent intent = new Intent(getApplicationContext(), EditCourse.class);
            intent.putExtra("termID", termID);
            intent.putExtra("courseID", courseID);
            intent.putExtra("mentorList", allMentors.size());
            intent.putExtra("assessmentList", allAssessments.size());
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
