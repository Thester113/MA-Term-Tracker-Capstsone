package com.example.wgutscheduler.DAO;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.wgutscheduler.Entity.CourseMentor;

import java.util.List;

@Dao
public interface MentorDAO {
    @Query("SELECT * FROM course_mentor WHERE course_id_fk = :courseID ORDER BY mentor_id")
    List<CourseMentor> getMentorList(int courseID);

    @Query("SELECT * FROM  course_mentor WHERE course_id_fk = :courseID and mentor_id = :mentorID")
    CourseMentor getMentor(int courseID, int mentorID);

    @Insert
    void insertMentor(CourseMentor courseMentor);

    @Insert
    void insertAllCourseMentors(CourseMentor... courseMentor);

    @Update
    void updateMentor(CourseMentor courseMentor);

    @Delete
    void deleteMentor(CourseMentor courseMentor);
}

