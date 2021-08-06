package com.example.wgutscheduler.DAO

import androidx.room.*
import com.example.wgutscheduler.Entity.CourseInstructor
import com.example.wgutscheduler.Entity.CourseMentor
import com.example.wgutscheduler.Entity.ProgramMentor

@Dao
interface MentorDAO {
    @Query("SELECT * FROM course_mentor WHERE course_id_fk = :courseID ORDER BY mentor_id")
    fun getMentorList(courseID: Int): List<CourseMentor>

    @Query("SELECT * FROM program_mentor WHERE course_id_fk = :courseID ORDER BY mentor_id")
    fun getProgramMentorList(courseID: Int): List<ProgramMentor>

    @Query("SELECT * FROM course_instructor WHERE course_id_fk = :courseID ORDER BY mentor_id")
    fun getCourseInstructorList(courseID: Int): List<CourseInstructor>

    @Query("SELECT * FROM  course_mentor WHERE course_id_fk = :courseID and mentor_id = :mentorID")
    fun getMentor(courseID: Int, mentorID: Int): CourseMentor?

    @Query("SELECT * FROM program_mentor WHERE course_id_fk = :courseID and mentor_id = :mentorID")
    fun getProgramMentor(courseID: Int, mentorID: Int): ProgramMentor?

    @Query("SELECT * FROM  course_instructor WHERE course_id_fk = :courseID and mentor_id = :mentorID")
    fun getCourseInstructor(courseID: Int, mentorID: Int): CourseInstructor?

    @Insert
    fun insertMentor(courseMentor: CourseMentor)

    @Insert
    fun insertProgramMentor(ProgramMentor: ProgramMentor)

    @Insert
    fun insertCourseInstructor(courseInstructor: CourseInstructor)


    @Insert
    fun insertAllCourseMentors(vararg courseMentor: CourseMentor?)

    @Update
    fun updateMentor(courseMentor: CourseMentor?)

    @Delete
    fun deleteMentor(courseMentor: CourseMentor?)
}