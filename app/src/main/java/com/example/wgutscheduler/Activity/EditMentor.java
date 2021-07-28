package com.example.wgutscheduler.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.wgutscheduler.DB.DataBase;
import com.example.wgutscheduler.Entity.CourseMentor;
import com.example.wgutscheduler.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class EditMentor extends AppCompatActivity {
    DataBase db;
    boolean mentorDeleted;
    boolean mentorUpdated;
    ExtendedFloatingActionButton updateMentorFAB;
    EditText editMentorEmailAddress;
    EditText editMentorName;
    EditText editMentorPhone;
    int courseID;
    Intent intent;
    int mentorID;
    int termID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mentor);
        intent = getIntent();
        db = DataBase.getInstance(getApplicationContext());
        termID = intent.getIntExtra("termID", -1);
        courseID = intent.getIntExtra("courseID", -1);
        mentorID = intent.getIntExtra("mentorID", -1);
        editMentorName = findViewById(R.id.editMentorName);
        editMentorPhone = findViewById(R.id.editMentorPhone);
        editMentorEmailAddress = findViewById(R.id.editMentorEmailAddress);
        updateMentorFAB = findViewById(R.id.updateMentorFAB);

        setValues();

        updateMentorFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMentor();

                if (mentorUpdated) {
                    Intent intent = new Intent(getApplicationContext(), MentorDetails.class);
                    intent.putExtra("termID", termID);
                    intent.putExtra("courseID", courseID);
                    intent.putExtra("mentorID", mentorID);
                    startActivity(intent);
                }
            }
        });
    }

    private void setValues() {
        CourseMentor mentor = new CourseMentor();
        mentor = db.MentorDao().getMentor(courseID, mentorID);
        String name = mentor.getMentor_name();
        String phone = mentor.getMentor_phone();
        String email = mentor.getMentor_email();

        editMentorName.setText(name);
        editMentorPhone.setText(phone);
        editMentorEmailAddress.setText(email);
    }

    private void updateMentor() {
        String name = editMentorName.getText().toString();
        String phone = editMentorPhone.getText().toString();
        String email = editMentorEmailAddress.getText().toString();

        if (name.trim().isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.trim().isEmpty()) {
            Toast.makeText(this, "Number is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.trim().isEmpty()) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
            return;
        }

        CourseMentor mentor = new CourseMentor();
        mentor.setCourse_id_fk(courseID);
        mentor.setMentor_id(mentorID);
        mentor.setMentor_name(name);
        mentor.setMentor_phone(phone);
        mentor.setMentor_email(email);
        db.MentorDao().updateMentor(mentor);
        Toast.makeText(this, name + " has been updated", Toast.LENGTH_SHORT).show();
        mentorUpdated = true;
    }

    private void deleteMentor() {
        CourseMentor mentor = new CourseMentor();
        mentor = db.MentorDao().getMentor(courseID, mentorID);
        db.MentorDao().deleteMentor(mentor);
        Toast.makeText(this, "Mentor has been deleted", Toast.LENGTH_SHORT).show();
        mentorDeleted = true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.delete_mentor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteMentorIC:
                deleteMentor();
                Intent intent = new Intent(getApplicationContext(), CourseDetails.class);
                intent.putExtra("termID", termID);
                intent.putExtra("courseID", courseID);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
