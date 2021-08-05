package com.example.wgutscheduler.Activity

import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wgutscheduler.DB.DataBase
import com.example.wgutscheduler.Entity.Assessment
import com.example.wgutscheduler.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class AssessmentDetails : AppCompatActivity() {
    lateinit var db: DataBase
    var termID = 0
    var courseID = 0
    private var assessmentID = 0
    private lateinit var adName: TextView
    private lateinit var adType: TextView
    private lateinit var adStatus: TextView
    private lateinit var adDueDate: TextView
    private lateinit var adAlert: TextView
    private lateinit var adEditFAB: ExtendedFloatingActionButton
    private var assessmentDeleted = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assessment_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        db = DataBase.getInstance(applicationContext)!!
        termID = intent.getIntExtra("termID", -1)
        courseID = intent.getIntExtra("courseID", -1)
        assessmentID = intent.getIntExtra("assessmentID", -1)
        adName = findViewById(R.id.adName)
        adType = findViewById(R.id.adType)
        adStatus = findViewById(R.id.adStatus)
        adDueDate = findViewById(R.id.adDueDate)
        adAlert = findViewById(R.id.adAlert)
        adEditFAB = findViewById(R.id.adEditFAB)
        setValues()
        adEditFAB.setOnClickListener {
            val intent = Intent(applicationContext, EditAssessment::class.java)
            intent.putExtra("termID", termID)
            intent.putExtra("courseID", courseID)
            intent.putExtra("assessmentID", assessmentID)
            startActivity(intent)
        }
    }

    private fun setValues() {
        val assessment: Assessment? = db.assessmentDao()?.getAssessment(courseID, assessmentID)
        val name = assessment?.assessment_name
        val type = assessment?.assessment_type
        val status = assessment?.assessment_status
        val dDate = DateFormat.format("MM/dd/yyyy", assessment?.assessment_due_date).toString()
        val alert1 = assessment?.assessment_alert
        var alert = "Off"
        if (alert1 == true) {
            alert = "On"
        }
        adName.text = name
        adType.text = type
        adStatus.text = status
        adDueDate.text = dDate
        adAlert.text = alert
    }

    private fun deleteAssessment() {
        val assessment: Assessment? = db.assessmentDao()?.getAssessment(courseID, assessmentID)
        db.assessmentDao()?.deleteAssessment(assessment)
        Toast.makeText(this, "Assessment has been deleted", Toast.LENGTH_SHORT).show()
        assessmentDeleted = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.deleteAssessmentIC) {
            deleteAssessment()
            val intent = Intent(applicationContext, CourseDetails::class.java)
            intent.putExtra("termID", termID)
            intent.putExtra("courseID", courseID)
            startActivity(intent)
            return true
        } else if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}