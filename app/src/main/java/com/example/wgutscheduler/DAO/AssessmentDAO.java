package com.example.wgutscheduler.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.wgutscheduler.Entity.Assessment;

import java.util.List;

@Dao
public interface AssessmentDAO {
    @Query("SELECT * FROM assessment WHERE course_id_fk = :courseID ORDER BY assessment_id")
    List<Assessment> getAssessmentList(int courseID);

    @Query("Select * from assessment WHERE course_id_fk = :courseID and assessment_id = :assessmentID")
    Assessment getAssessment(int courseID, int assessmentID);

    @Query("SELECT * FROM assessment")
    List<Assessment> getAllAssessments();

    @Query("SELECT * FROM assessment WHERE course_id_fk = :courseID ORDER BY assessment_id DESC LIMIT 1")
    Assessment getCurrentAssessment(int courseID);

    @Insert
    void insertAssessment(Assessment assessment);

    @Insert
    void insertAllAssessments(Assessment... assessment);

    @Update
    void updateAssessment(Assessment assessment);

    @Delete
    void deleteAssessment(Assessment assessment);


}
