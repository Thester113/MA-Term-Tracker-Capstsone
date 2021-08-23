package com.example.wgutscheduler.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import com.example.wgutscheduler.DB.DataBase
import com.example.wgutscheduler.Entity.Assessment
import com.example.wgutscheduler.Entity.Course
import com.example.wgutscheduler.Entity.CourseMentor
import com.example.wgutscheduler.Entity.Term
import com.example.wgutscheduler.R
import java.text.SimpleDateFormat
import java.util.*

class ReportsActivity : AppCompatActivity() {
    lateinit var db: DataBase
    lateinit var report: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        db = DataBase.getInstance(applicationContext)!!
        report = findViewById(R.id.Report)
        val termId = intent.getIntExtra("termID", -1)
        val term = db.termDao()?.getTerm(termId)?.let { term ->
            TermInfo(
                    term = term,
                    course = db.courseDao()?.getCourseList(termId).orEmpty().map { course ->
                        CourseInfo(
                                course = course,
                                mentors = mutableListOf<CourseMentor>().apply {
                                    db.MentorDao()?.getMentorList(course.course_id)?.let { addAll(it) }
                                    db.MentorDao()?.getProgramMentorList(course.course_id)?.let { addAll(it) }
                                    db.MentorDao()?.getCourseInstructorList(course.course_id)?.let { addAll(it) }
                                    sortBy { it.mentor_id }
                                },
                                assessment = db.assessmentDao()?.getAssessmentList(course.course_id).orEmpty()

                        )
                    }
            )
        }!!
        val formatter = SimpleDateFormat("MM/dd/yyyy hh:mm:ss a")
        val startEndFormatter = SimpleDateFormat("MM/dd/yyyy")
        val currentTime = formatter.format(Date(System.currentTimeMillis()))

        val reportText = buildString {
            append("Generated report on $currentTime \n " +
                    "\n Term Name: ${term.term.term_name} " +
                    "\n Term Status:  ${term.term.term_status} " +
                    "\n Term Start: ${startEndFormatter.format(term.term.term_start!!)} " +
                    "\n Term End: ${startEndFormatter.format(term.term.term_end!!)} \n" +
                    "\n"

            )
            term.course.forEach { course ->
                append("Course: ${course.course.course_name}: " +
                        "\n Course Start:  ${startEndFormatter.format(course.course.course_start!!)}" +
                        "\n Course End: ${startEndFormatter.format(course.course.course_end!!)}" +
                        "\n Course Status: ${course.course.course_status} " +
                        "\n Course Notes: ${course.course.course_notes} \n"
                        +
                        "\n"

                )

                course.mentors.forEach { mentors ->
                    append("Mentors: ${mentors.mentor_name} " +
                            "\n Mentor Phone: ${mentors.mentor_phone} " +
                            "\n Mentor Email: ${mentors.mentor_email} \n " +
                            "\n"

                    )


                }
                course.assessment.forEach { assessment ->
                    append("Assessment: ${assessment.assessment_name} " +
                            "\n Assessment Type: ${assessment.assessment_type} " +
                            "\n Assessment Status: ${assessment.assessment_status} " +
                            "\n Assessment Due Date: ${startEndFormatter.format(assessment.assessment_due_date!!)} " +
                            "\n " +
                            "\n "
                    )

                }
            }
        }
        report.text = reportText
    }

    data class TermInfo(
            val term: Term,
            val course: List<CourseInfo>,
    )

    data class CourseInfo(
            val course: Course,
            val mentors: List<CourseMentor>,
            val assessment: List<Assessment>
    )

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
