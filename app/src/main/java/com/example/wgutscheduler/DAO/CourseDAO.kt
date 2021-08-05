package com.example.wgutscheduler.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.wgutscheduler.Entity.Course;

import java.util.List;

@Dao
public interface CourseDAO {
    @Query("SELECT * FROM course WHERE term_id_fk = :termID ORDER BY course_id")
    List<Course> getCourseList(int termID);

    @Query("SELECT * FROM course WHERE term_id_fk = :termID and course_id = :courseID")
    Course getCourse(int termID, int courseID);

    @Query("SELECT * FROM course")
    List<Course> getAllCourses();

    @Query("SELECT * FROM course WHERE term_id_fk = :termID ORDER BY course_id DESC LIMIT 1")
    Course getCurrentCourse(int termID);

    @Insert
    void insertCourse(Course course);

    @Insert
    void insertAllCourses(Course... course);

    @Update
    void updateCourse(Course course);

    @Delete
    void deleteCourse(Course course);
}
