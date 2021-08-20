package com.example.wgutscheduler.Utilities


import androidx.annotation.VisibleForTesting
import com.example.wgutscheduler.DB.DataBase
import com.example.wgutscheduler.Entity.Assessment
import com.example.wgutscheduler.Entity.Course
import com.example.wgutscheduler.Entity.CourseMentor
import com.example.wgutscheduler.Entity.Term
import java.util.*

class AddSampleData(val db : DataBase) {
    private var tempTerm1 = Term()
    private var tempTerm2 = Term()
    private var tempTerm3 = Term()
    private var tempTerm4 = Term()
    private var tempCourse1 = Course()
    private var tempCourse2 = Course()
    private var tempCourse3 = Course()
    private var tempCourse4 = Course()
    private var tempAssessment1 = Assessment()
    private var tempCourseMentor1 = CourseMentor()
    fun populate() {
        try {
            insertTerms()
            insertCourses()
            insertAssessments()
            insertCourseMentors()
        } catch (e: Exception) {
            e.printStackTrace()
            println("Populate DB failed")
        }
    }
    @VisibleForTesting
     fun insertTerms() {
        var start: Calendar = Calendar.getInstance()
        var end: Calendar = Calendar.getInstance()
        start.add(Calendar.MONTH, -2)
        end.add(Calendar.MONTH, 1)
        tempTerm1.term_name = "Spring 2021"
        tempTerm1.term_start = start.time
        tempTerm1.term_status = "Completed"
        tempTerm1.term_end = end.time
        start = Calendar.getInstance()
        end = Calendar.getInstance()
        start.add(Calendar.MONTH, 2)
        end.add(Calendar.MONTH, 5)
        tempTerm2.term_name = "Fall 2021"
        tempTerm2.term_start = start.time
        tempTerm2.term_status = "In-Progress"
        tempTerm2.term_end = end.time
        start = Calendar.getInstance()
        end = Calendar.getInstance()
        start.add(Calendar.MONTH, 6)
        end.add(Calendar.MONTH, 9)
        tempTerm3.term_name = "Spring 2022"
        tempTerm3.term_start = start.time
        tempTerm3.term_status = "Not Enrolled"
        tempTerm3.term_end = end.time
        start = Calendar.getInstance()
        end = Calendar.getInstance()
        start.add(Calendar.MONTH, 1)
        end.add(Calendar.MONTH, 7)
        tempTerm4.term_name = "Summer 2022"
        tempTerm4.term_start = start.time
        tempTerm4.term_status = "Not Enrolled"
        tempTerm4.term_end = end.time
        db.termDao()!!.insertAllTerms(listOf(tempTerm1, tempTerm2, tempTerm3, tempTerm4))
    }

    private fun insertCourses() {
        val TermList = db.termDao()!!.termList ?: return
        var start: Calendar = Calendar.getInstance()
        var end: Calendar = Calendar.getInstance()
        start.add(Calendar.MONTH, -2)
        end.add(Calendar.MONTH, -1)
        tempCourse1.course_name = "Software 1"
        tempCourse1.course_start = start.time
        tempCourse1.course_end = end.time
        tempCourse1.course_status = "Pending"
        tempCourse1.course_notes = "Please Add A Note"
        tempCourse1.term_id_fk = TermList[0]!!.term_id
        start = Calendar.getInstance()
        end = Calendar.getInstance()
        start.add(Calendar.MONTH, -1)
        //end.add(Calendar.MONTH, 1);
        tempCourse2.course_name = "Software 2"
        tempCourse2.course_start = start.time
        tempCourse2.course_end = end.time
        tempCourse2.course_status = "Completed"
        tempCourse2.course_notes = "Please add a note"
        tempCourse2.term_id_fk = TermList[0]!!.term_id
        start = Calendar.getInstance()
        end = Calendar.getInstance()
        //start.add(Calendar.MONTH, -2);
        end.add(Calendar.MONTH, -1)
        tempCourse3.course_name = "Mobile Development"
        tempCourse3.course_start = start.time
        tempCourse3.course_end = end.time
        tempCourse3.course_status = "Dropped"
        tempCourse3.course_notes = "Please add a note"
        tempCourse3.term_id_fk = TermList[0]!!.term_id
        start = Calendar.getInstance()
        end = Calendar.getInstance()
        //start.add(Calendar.MONTH, -2);
        end.add(Calendar.MONTH, -1)
        tempCourse4.course_name = "Software Engineering"
        tempCourse4.course_start = start.time
        tempCourse4.course_end = end.time
        tempCourse4.course_status = "Passed"
        tempCourse4.course_notes = "Please add a note"
        tempCourse4.term_id_fk = TermList[0]!!.term_id
        db.courseDao()!!.insertAllCourses(tempCourse1, tempCourse2, tempCourse3, tempCourse4)
    }

    private fun insertCourseMentors() {
        val TermList = db.termDao()!!.termList
        val CourseList = db.courseDao()!!.getCourseList(TermList!![0]!!.term_id) ?: return
        tempCourseMentor1.mentor_name = "Carolyn Sher-DeCusatis"
        tempCourseMentor1.mentor_email = "carolyn@wgu.edu"
        tempCourseMentor1.mentor_phone = "385-528-1197"
        tempCourseMentor1.course_id_fk = CourseList[0].course_id
        db.MentorDao()!!.insertAllCourseMentors(tempCourseMentor1)
    }

    private fun insertAssessments() {
        val TermList = db.termDao()!!.termList
        val CourseList = db.courseDao()!!.getCourseList(TermList!![0]!!.term_id) ?: return
        val start: Calendar = Calendar.getInstance()
        val end: Calendar = Calendar.getInstance()
        start.add(Calendar.MONTH, -2)
        end.add(Calendar.MONTH, -1)
        tempAssessment1.assessment_name = "Software Assessment 1"
        tempAssessment1.assessment_due_date = start.time
        tempAssessment1.assessment_type = "Objective"
        tempAssessment1.course_id_fk = CourseList[0].course_id
        tempAssessment1.assessment_status = "Pending"
        db.assessmentDao()!!.insertAllAssessments(tempAssessment1)
    }

    companion object {
        var LOG_TAG = "Data Populated"
    }
}