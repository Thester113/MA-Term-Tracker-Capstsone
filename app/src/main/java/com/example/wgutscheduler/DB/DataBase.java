package com.example.wgutscheduler.DB;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.wgutscheduler.DAO.AssessmentDAO;
import com.example.wgutscheduler.DAO.CourseDAO;
import com.example.wgutscheduler.DAO.MentorDAO;
import com.example.wgutscheduler.DAO.TermDAO;
import com.example.wgutscheduler.Entity.Assessment;
import com.example.wgutscheduler.Entity.Course;
import com.example.wgutscheduler.Entity.CourseMentor;
import com.example.wgutscheduler.Entity.Term;
import com.example.wgutscheduler.Utilities.Converter;

@androidx.room.Database(entities = {Term.class, Course.class, CourseMentor.class, Assessment.class}, exportSchema = false, version = 1)
@TypeConverters({Converter.class})
public abstract class DataBase extends RoomDatabase {
    private static final String DB_Name = "WGUTScheduler.db";
    private static DataBase instance;

    public static synchronized DataBase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), DataBase.class, DB_Name).allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        return instance;
    }

    public abstract TermDAO termDao();

    public abstract CourseDAO courseDao();

    public abstract MentorDAO MentorDao();

    public abstract AssessmentDAO assessmentDao();
}

