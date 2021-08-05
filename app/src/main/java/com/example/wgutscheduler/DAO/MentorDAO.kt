package com.example.wgutscheduler.DAO

import androidx.room.*
import com.example.wgutscheduler.Entity.CourseMentor

@Dao
interface MentorDAO {
    @Query("SELECT * FROM course_mentor WHERE course_id_fk = :courseID ORDER BY mentor_id")
    fun getMentorList(courseID: Int): List<CourseMentor?>?

    @Query("SELECT * FROM  course_mentor WHERE course_id_fk = :courseID and mentor_id = :mentorID")
    fun getMentor(courseID: Int, mentorID: Int): CourseMentor?

    @Insert
    fun insertMentor(courseMentor: CourseMentor?)

    @Insert
    fun insertAllCourseMentors(vararg courseMentor: CourseMentor?)

    @Update
    fun updateMentor(courseMentor: CourseMentor?)

    @Delete
    fun deleteMentor(courseMentor: CourseMentor?)
}