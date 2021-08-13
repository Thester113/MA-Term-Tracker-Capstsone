package com.example.wgutscheduler.DAO

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.wgutscheduler.Entity.Course
import com.example.wgutscheduler.Entity.Term

@Dao
interface CourseDAO {
    @Query("SELECT * FROM course WHERE term_id_fk = :termID ORDER BY course_id")
    fun getCourseList(termID: Int): List<Course>

    @Query("SELECT * FROM course WHERE term_id_fk = :termID and course_id = :courseID")
    fun getCourse(termID: Int, courseID: Int): Course?

    @get:Query("SELECT * FROM course")
    val allCourses: List<Course?>?

    @Query("SELECT * FROM course WHERE term_id_fk = :termID ORDER BY course_id DESC LIMIT 1")
    fun getCurrentCourse(termID: Int): Course?

    @Query("SELECT * FROM course WHERE course_name LIKE :courseName")
    fun searchCourses(courseName: String): List<Course>

    @Insert
    fun insertCourse(course: Course?)

    @Insert
    fun insertAllCourses(vararg course: Course?)

    @Update
    fun updateCourse(course: Course?)

    @Delete
    fun deleteCourse(course: Course?)
}