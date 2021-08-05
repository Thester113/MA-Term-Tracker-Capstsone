package com.example.wgutscheduler.DAO

import androidx.room.*
import com.example.wgutscheduler.Entity.Assessment

@Dao
interface AssessmentDAO {
    @Query("SELECT * FROM assessment WHERE course_id_fk = :courseID ORDER BY assessment_id")
    fun getAssessmentList(courseID: Int): List<Assessment?>?

    @Query("Select * from assessment WHERE course_id_fk = :courseID and assessment_id = :assessmentID")
    fun getAssessment(courseID: Int, assessmentID: Int): Assessment?

    @get:Query("SELECT * FROM assessment")
    val allAssessments: List<Assessment?>?

    @Query("SELECT * FROM assessment WHERE course_id_fk = :courseID ORDER BY assessment_id DESC LIMIT 1")
    fun getCurrentAssessment(courseID: Int): Assessment?

    @Insert
    fun insertAssessment(assessment: Assessment?)

    @Insert
    fun insertAllAssessments(vararg assessment: Assessment?)

    @Update
    fun updateAssessment(assessment: Assessment?)

    @Delete
    fun deleteAssessment(assessment: Assessment?)
}