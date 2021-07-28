package com.example.wgutscheduler.Utilities;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wgutscheduler.DB.DataBase;
import com.example.wgutscheduler.Entity.Assessment;
import com.example.wgutscheduler.Entity.Course;
import com.example.wgutscheduler.Entity.CourseMentor;
import com.example.wgutscheduler.Entity.Term;

import java.util.Calendar;
import java.util.List;

public class AddSampleData extends AppCompatActivity {
    public static String LOG_TAG = "Data Populated";

    Term tempTerm1 = new Term();
    Term tempTerm2 = new Term();
    Term tempTerm3 = new Term();

    Course tempCourse1 = new Course();
    Course tempCourse2 = new Course();
    Course tempCourse3 = new Course();

    Assessment tempAssessment1 = new Assessment();

    CourseMentor tempCourseMentor1 = new CourseMentor();

    DataBase db;

    public void populate(Context context) {
        db = DataBase.getInstance(context);
        try {
            insertTerms();
            insertCourses();
            insertAssessments();
            insertCourseMentors();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "Populate LocalDB failed");
        }
    }

    private void insertTerms() {
        Calendar start;
        Calendar end;

        start = Calendar.getInstance();
        end = Calendar.getInstance();
        start.add(Calendar.MONTH, -2);
        end.add(Calendar.MONTH, 1);
        tempTerm1.setTerm_name("Fall 2020");
        tempTerm1.setTerm_start(start.getTime());
        tempTerm1.setTerm_status("Completed");
        tempTerm1.setTerm_end(end.getTime());

        start = Calendar.getInstance();
        end = Calendar.getInstance();
        start.add(Calendar.MONTH, 2);
        end.add(Calendar.MONTH, 5);
        tempTerm2.setTerm_name("Spring 2021");
        tempTerm2.setTerm_start(start.getTime());
        tempTerm2.setTerm_status("In-Progress");
        tempTerm2.setTerm_end(end.getTime());

        start = Calendar.getInstance();
        end = Calendar.getInstance();
        start.add(Calendar.MONTH, 6);
        end.add(Calendar.MONTH, 9);
        tempTerm3.setTerm_name("Summer 2021");
        tempTerm3.setTerm_start(start.getTime());
        tempTerm3.setTerm_status("Not Enrolled");
        tempTerm3.setTerm_end(end.getTime());

        db.termDao().insertAllTerms(tempTerm1, tempTerm2, tempTerm3);
    }

    private void insertCourses() {
        Calendar start;
        Calendar end;
        List<Term> TermList = db.termDao().getTermList();
        if (TermList == null) return;

        start = Calendar.getInstance();
        end = Calendar.getInstance();
        start.add(Calendar.MONTH, -2);
        end.add(Calendar.MONTH, -1);
        tempCourse1.setCourse_name("Software 1");
        tempCourse1.setCourse_start(start.getTime());
        tempCourse1.setCourse_end(end.getTime());
        tempCourse1.setCourse_status("Pending");
        tempCourse1.setCourse_notes("Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        tempCourse1.setTerm_id_fk(TermList.get(0).getTerm_id());

        start = Calendar.getInstance();
        end = Calendar.getInstance();
        start.add(Calendar.MONTH, -1);
        //end.add(Calendar.MONTH, 1);
        tempCourse2.setCourse_name("Software 2");
        tempCourse2.setCourse_start(start.getTime());
        tempCourse2.setCourse_end(end.getTime());
        tempCourse2.setCourse_status("Completed");
        tempCourse2.setCourse_notes("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        tempCourse2.setTerm_id_fk(TermList.get(0).getTerm_id());

        start = Calendar.getInstance();
        end = Calendar.getInstance();
        //start.add(Calendar.MONTH, -2);
        end.add(Calendar.MONTH, -1);
        tempCourse3.setCourse_name("Mobile Development");
        tempCourse3.setCourse_start(start.getTime());
        tempCourse3.setCourse_end(end.getTime());
        tempCourse3.setCourse_status("Dropped");
        tempCourse3.setCourse_notes("Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.");
        tempCourse3.setTerm_id_fk(TermList.get(0).getTerm_id());

        db.courseDao().insertAllCourses(tempCourse1, tempCourse2, tempCourse3);
    }

    private void insertCourseMentors() {

        List<Term> TermList = db.termDao().getTermList();
        List<Course> CourseList = db.courseDao().getCourseList(TermList.get(0).getTerm_id());

        if (CourseList == null) return;

        tempCourseMentor1.setMentor_name("Carolyn Sher-DeCusatis");
        tempCourseMentor1.setMentor_email("carolyn@wgu.edu");
        tempCourseMentor1.setMentor_phone("385-528-1197");
        tempCourseMentor1.setCourse_id_fk(CourseList.get(0).getCourse_id());

        db.MentorDao().insertAllCourseMentors(tempCourseMentor1);
    }

    private void insertAssessments() {
        Calendar start;
        Calendar end;
        List<Term> TermList = db.termDao().getTermList();
        List<Course> CourseList = db.courseDao().getCourseList(TermList.get(0).getTerm_id());
        if (CourseList == null) return;

        start = Calendar.getInstance();
        end = Calendar.getInstance();
        start.add(Calendar.MONTH, -2);
        end.add(Calendar.MONTH, -1);
        tempAssessment1.setAssessment_name("Software Assessment #1");
        tempAssessment1.setAssessment_due_date(start.getTime());
        tempAssessment1.setAssessment_type("Objective");
        tempAssessment1.setCourse_id_fk(CourseList.get(0).getCourse_id());
        tempAssessment1.setAssessment_status("Pending");

        db.assessmentDao().insertAllAssessments(tempAssessment1);
    }
}
