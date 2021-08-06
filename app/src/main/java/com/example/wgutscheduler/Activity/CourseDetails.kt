package com.example.wgutscheduler.Activity

import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.wgutscheduler.DB.DataBase
import com.example.wgutscheduler.Entity.Assessment
import com.example.wgutscheduler.Entity.Course
import com.example.wgutscheduler.Entity.CourseMentor
import com.example.wgutscheduler.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CourseDetails : AppCompatActivity() {
    lateinit var db: DataBase
    var courseID = 0
    var termID = 0
    private lateinit var cdMentorList: ListView
    private lateinit var cdAssessmentList: ListView
    private lateinit var allMentors: List<CourseMentor>
    private lateinit var allAssessments: List<Assessment>
    private lateinit var cdAddMentorFAB: FloatingActionButton
    private lateinit var cdAddAssessmentFAB: FloatingActionButton
    private lateinit var cdName: TextView
    private lateinit var cdStatus: TextView
    private lateinit var cdAlert: TextView
    private lateinit var cdsDate: TextView
    private lateinit var cdeDate: TextView
    private lateinit var cdNotes: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        db = DataBase.getInstance(applicationContext)!!
        termID = intent.getIntExtra("termID", -1)
        courseID = intent.getIntExtra("courseID", -1)
        cdMentorList = findViewById(R.id.cdMentorList)
        cdAssessmentList = findViewById(R.id.cdAssessmentList)
        cdAddMentorFAB = findViewById(R.id.cdAddMentorFAB)
        cdAddAssessmentFAB = findViewById(R.id.cdAddAssessmentFAB)
        cdName = findViewById(R.id.cdName)
        cdStatus = findViewById(R.id.cdStatus)
        cdAlert = findViewById(R.id.cdAlert)
        cdsDate = findViewById(R.id.cdSdate)
        cdeDate = findViewById(R.id.cdEdate)
        cdNotes = findViewById(R.id.cdNotes)
        setValues()
        updateLists()

        //Mentors
        cdAddMentorFAB.setOnClickListener {
            val intent = Intent(applicationContext, AddMentor::class.java)
            intent.putExtra("termID", termID)
            intent.putExtra("courseID", courseID)
            startActivity(intent)
        }
        cdMentorList.onItemClickListener = OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            val intent = Intent(applicationContext, MentorDetails::class.java)
            intent.putExtra("termID", termID)
            intent.putExtra("courseID", courseID)
            intent.putExtra("mentorID", allMentors[position].mentor_id)
            intent.putExtra("mentorType",allMentors[position]::class.simpleName)
            startActivity(intent)
            println(id)
        }

        //Assessments
        cdAddAssessmentFAB.setOnClickListener {
            val intent = Intent(applicationContext, AddAssessment::class.java)
            intent.putExtra("termID", termID)
            intent.putExtra("courseID", courseID)
            startActivity(intent)
        }
        cdAssessmentList.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            val intent = Intent(applicationContext, AssessmentDetails::class.java)
            intent.putExtra("termID", termID)
            intent.putExtra("courseID", courseID)
            intent.putExtra("assessmentID", allAssessments[position].assessment_id)
            startActivity(intent)
            println(id)
        }
    }

    private fun setValues() {
        try {
            val course: Course? = db.courseDao()?.getCourse(termID, courseID)
            val name = course?.course_name
            val status = course?.course_status
            val alert1 = course?.course_alert
            val sDate = DateFormat.format("MM/dd/yyyy", course?.course_start).toString()
            val eDate = DateFormat.format("MM/dd/yyyy", course?.course_end).toString()
            val notes = course?.course_notes
            var alert = "Off"
            if (alert1 == true) {
                alert = "On"
            }
            cdName.text = name
            cdStatus.text = status
            cdAlert.text = alert
            cdsDate.text = sDate
            cdeDate.text = eDate
            cdNotes.text = notes
        } catch (e: NullPointerException) {
            print("NullPointerException caught")
        }
    }

    private fun updateLists() {

        val allMentors = mutableListOf<CourseMentor>().apply {
            db.MentorDao()?.getMentorList(courseID)?.let { addAll(it) }
            db.MentorDao()?.getProgramMentorList(courseID)?.let { addAll(it) }
            db.MentorDao()?.getCourseInstructorList(courseID)?.let { addAll(it) }
            sortBy { it.mentor_id }
        }
        val adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                allMentors.filterNotNull())
        cdMentorList.adapter = adapter
        this.allMentors = allMentors.filterNotNull()
        adapter.notifyDataSetChanged()
        val allAssessments = db.assessmentDao()?.getAssessmentList(courseID)
        val adapter2 = allAssessments?.let { ArrayAdapter(this, android.R.layout.simple_list_item_1, it.filterNotNull()) }
        cdAssessmentList.adapter = adapter2
        if (allAssessments != null) {
            this.allAssessments = allAssessments.filterNotNull()
        }
        adapter2?.notifyDataSetChanged()
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.edit_course, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.tdEditCourseIC) {
            val intent = Intent(applicationContext, EditCourse::class.java)
            intent.putExtra("termID", termID)
            intent.putExtra("courseID", courseID)
            intent.putExtra("mentorList", allMentors.size)
            intent.putExtra("assessmentList", allAssessments.size)
            startActivity(intent)
            return true
        } else if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}