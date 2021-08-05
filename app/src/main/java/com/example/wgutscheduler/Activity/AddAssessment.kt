package com.example.wgutscheduler.Activity

import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.DialogFragment
import com.example.wgutscheduler.DB.DataBase
import com.example.wgutscheduler.Entity.Assessment
import com.example.wgutscheduler.R
import com.example.wgutscheduler.Utilities.Converter
import com.example.wgutscheduler.Utilities.DatePickerFrag
import com.example.wgutscheduler.Utilities.Notifications
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddAssessment : AppCompatActivity(), OnDateSetListener {
    lateinit var db: DataBase
    private var assessmentAdded = false
    private lateinit var cDate: Date
    private lateinit var dueDate: Date
    private lateinit var addAssessmentName: EditText
    private var assessmentID = 0
    var courseID = 0
    var termID = 0
    private lateinit var formatter: SimpleDateFormat
    lateinit var addAssessmentStatus: Spinner
    lateinit var addAssessmentType: Spinner
    lateinit var name: String
    lateinit var status: String
    lateinit var type: String
    private lateinit var aAlert: SwitchCompat
    private lateinit var addAssessmentDueDate: TextView
    private lateinit var datePickerView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_assessment)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        db = DataBase.getInstance(applicationContext)!!
        termID = intent.getIntExtra("termID", -1)
        courseID = intent.getIntExtra("courseID", -1)
        addAssessmentName = findViewById(R.id.addAssessmentName)
        addAssessmentType = findViewById(R.id.addAssessmentType)
        addAssessmentStatus = findViewById(R.id.addAssessmentStatus)
        addAssessmentDueDate = findViewById(R.id.addAssessmentDueDate)
        aAlert = findViewById(R.id.aAlert)
        setupDatePicker()
        setupSpinner()
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter.createFromResource(this, R.array.assessment_type_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        addAssessmentType.adapter = adapter
        addAssessmentType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                type = addAssessmentType.getItemAtPosition(i).toString()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        val adapter2 = ArrayAdapter.createFromResource(this, R.array.assessment_status_array, android.R.layout.simple_spinner_item)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        addAssessmentStatus.adapter = adapter2
        addAssessmentStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                status = addAssessmentStatus.getItemAtPosition(i).toString()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    @Throws(ParseException::class)
    private fun addAssessment() {
        formatter = SimpleDateFormat("MM/dd/yyyy")
        name = addAssessmentName.text.toString()
        val dDate = addAssessmentDueDate.text.toString()
        val cDate = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date())
        val alert = aAlert.isChecked
        dueDate = formatter.parse(dDate)
        this.cDate = formatter.parse(cDate)
        if (name.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show()
            return
        }
        if (dDate.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "A due date is required", Toast.LENGTH_SHORT).show()
            return
        }
        val assessment = Assessment()
        assessment.course_id_fk = courseID
        assessment.assessment_name = name
        assessment.assessment_type = type
        assessment.assessment_status = status
        assessment.assessment_due_date = dueDate
        assessment.assessment_alert = alert
        db.assessmentDao()?.insertAssessment(assessment)
        Toast.makeText(this, "$name has been added.", Toast.LENGTH_SHORT).show()
        Log.d(LOG_TAG, "$name has been added.")
        assessmentAdded = true
        if (alert) {
            AddAssessmentAlert()
        }
    }

    fun AddAssessmentAlert() {
        var assessment = Assessment()
        assessment = db.assessmentDao()?.getCurrentAssessment(courseID)!!
        assessmentID = assessment.assessment_id
        val sText = "$name is due today!"
        setAlert(assessmentID, dueDate, name, sText)
    }

    private fun setAlert(ID: Int, date: Date?, title: String?, text: String) {
        val alertTime = Converter.dateToTimeStamp(date)
        if (dueDate < cDate) {
            return
        }
        if (alertTime != null) {
            Notifications.setAssessmentAlert(applicationContext, ID, alertTime, title, text)
        }
        Toast.makeText(this, "$name due date alarm enabled", Toast.LENGTH_SHORT).show()
    }

    private fun setupDatePicker() {
        addAssessmentDueDate.setOnClickListener {
            datePickerView = findViewById(R.id.addAssessmentDueDate)
            val datePicker: DialogFragment = DatePickerFrag()
            datePicker.show(supportFragmentManager, "date picker")
        }
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        var month = month
        val calendar = Calendar.getInstance()
        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = month + 1.also { month = it }
        calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        val currentDateString = "$month/$dayOfMonth/$year"
        datePickerView.text = currentDateString
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_assessment, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.addAssessmentFAB) {
            try {
                addAssessment()
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            if (assessmentAdded) {
                val intent = Intent(applicationContext, CourseDetails::class.java)
                intent.putExtra("termID", termID)
                intent.putExtra("courseID", courseID)
                startActivity(intent)
            }
            return true
        } else if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        var LOG_TAG = "Adding assessment"
    }
}