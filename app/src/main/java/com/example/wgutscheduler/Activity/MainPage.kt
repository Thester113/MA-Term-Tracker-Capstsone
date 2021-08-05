package com.example.wgutscheduler.Activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wgutscheduler.DB.DataBase
import com.example.wgutscheduler.R
import com.example.wgutscheduler.Utilities.AddSampleData
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class MainPage : AppCompatActivity() {
    lateinit var db: DataBase
    private lateinit var termData: TextView
    private lateinit var coursesPendingTextView: TextView
    private lateinit var coursesCompletedTextView: TextView
    private lateinit var coursesDroppedTextView: TextView
    private lateinit var assessmentsPendingTextView: TextView
    private lateinit var assessmentsPassedTextView: TextView
    private lateinit var assessmentsFailedTextView: TextView
    private lateinit var TermListFAB: ExtendedFloatingActionButton
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = DataBase.getInstance(applicationContext)!!
        termData = findViewById(R.id.termData)
        coursesPendingTextView = findViewById(R.id.coursesPendingTextView)
        coursesCompletedTextView = findViewById(R.id.coursesCompletedTextView)
        coursesDroppedTextView = findViewById(R.id.coursesDroppedTextView)
        assessmentsPendingTextView = findViewById(R.id.assessmentsPendingTextView)
        assessmentsPassedTextView = findViewById(R.id.assessmentsPassedTextView)
        assessmentsFailedTextView = findViewById(R.id.assessmentsFailedTextView)
        TermListFAB = findViewById(R.id.TermListFAB)
        updateViews()
        TermListFAB.setOnClickListener {
            val intent = Intent(applicationContext, TermList::class.java)
            startActivity(intent)
        }
    }

    private fun updateViews() {
        var term = 0
        var termComplete = 0
        var termPending = 0
        var course = 0
        var assessment = 0
        var coursesPending = 0
        var coursesCompleted = 0
        var coursesDropped = 0
        var assessmentsPending = 0
        var assessmentsPassed = 0
        var assessmentsFailed = 0
        try {
            val termList = db.termDao()?.allTerms
            val courseList = db.courseDao()?.allCourses
            val assessmentList = db.assessmentDao()?.allAssessments
            try {
                if (termList != null) {
                    for (i in termList.indices) {
                        term = termList.size
                        if (termList[i]?.term_status?.contains("Completed") == true) termComplete++
                        if (termList[i]?.term_status?.contains("In-Progress") == true) termPending++
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                if (courseList != null) {
                    for (i in courseList.indices) {
                        course = courseList.size
                        if (courseList[i]?.course_status?.contains("Pending") == true) coursesPending++
                        if (courseList[i]?.course_status?.contains("In-Progress") == true) coursesPending++
                        if (courseList[i]?.course_status?.contains("Completed") == true) coursesCompleted++
                        if (courseList[i]?.course_status?.contains("Dropped") == true) coursesDropped++
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                if (assessmentList != null) {
                    for (i in assessmentList.indices) {
                        assessment = assessmentList.size
                        if (assessmentList[i]?.assessment_status?.contains("Pending") == true) assessmentsPending++
                        if (assessmentList[i]?.assessment_status?.contains("Passed") == true) assessmentsPassed++
                        if (assessmentList[i]?.assessment_status?.contains("Failed") == true) assessmentsFailed++
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        termData.text = term.toString()
        coursesPendingTextView.text = coursesPending.toString()
        coursesCompletedTextView.text = coursesCompleted.toString()
        coursesDroppedTextView.text = coursesDropped.toString()
        assessmentsPendingTextView.text = assessmentsPending.toString()
        assessmentsFailedTextView.text = assessmentsFailed.toString()
        assessmentsPassedTextView.text = assessmentsPassed.toString()
    }

    override fun onResume() {
        super.onResume()
        updateViews()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.populateDBMenu) {
            val addSampleData = AddSampleData()
            addSampleData.populate(applicationContext)
            updateViews()
            Toast.makeText(this, "Congrats! DB Populated", Toast.LENGTH_SHORT).show()
            return true
        } else if (itemId == R.id.resetDBMenu) {
            db.clearAllTables()
            updateViews()
            Toast.makeText(this, "DB successfully Reset", Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}