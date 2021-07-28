package com.example.wgutscheduler.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wgutscheduler.DB.DataBase;
import com.example.wgutscheduler.Entity.CourseMentor;
import com.example.wgutscheduler.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class AddMentor extends AppCompatActivity {

    DataBase db;
    boolean mentorAdded;
    EditText addMentorEmailAddress;
    EditText addMentorName;
    EditText addMentorPhone;
    ExtendedFloatingActionButton addMentorFAB;
    int courseID;
    Intent intent;
    int termID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mentor);
        intent = getIntent();
        db = DataBase.getInstance(getApplicationContext());
        termID = intent.getIntExtra("termID", -1);
        courseID = intent.getIntExtra("courseID", -1);
        addMentorName = findViewById(R.id.addMentorName);
        addMentorPhone = findViewById(R.id.addMentorPhone);
        addMentorEmailAddress = findViewById(R.id.addMentorEmailAddress);
        addMentorFAB = findViewById(R.id.addMentorFAB);
        addMentorFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMentor();

                if (mentorAdded) {
                    Intent intent = new Intent(getApplicationContext(), CourseDetails.class);
                    intent.putExtra("termID", termID);
                    intent.putExtra("courseID", courseID);
                    startActivity(intent);
                }
            }
        });

    }

    private void addMentor() {
        String name = addMentorName.getText().toString();
        String phone = addMentorPhone.getText().toString();
        String email = addMentorEmailAddress.getText().toString();

        if (name.trim().isEmpty()) {
            Toast.makeText(this, "A name is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.trim().isEmpty()) {
            Toast.makeText(this, "A phone number is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.trim().isEmpty()) {
            Toast.makeText(this, "An email is required", Toast.LENGTH_SHORT).show();
            return;
        }

        CourseMentor mentor = new CourseMentor();
        mentor.setCourse_id_fk(courseID);
        mentor.setMentor_name(name);
        mentor.setMentor_phone(phone);
        mentor.setMentor_email(email);
        db.MentorDao().insertMentor(mentor);
        Toast.makeText(this, name + " has been added", Toast.LENGTH_SHORT).show();
        mentorAdded = true;
    }
}
