package com.example.wgutscheduler.Activity

import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EditAssessment : AppCompatActivity(), OnDateSetListener {
    lateinit var db: DataBase
    private var assessmentDeleted = false
    private var assessmentUpdated = false
    private lateinit var cDate: Date
    private lateinit var dueDate: Date
    private lateinit var editAssessmentNameText: EditText
    private lateinit var updateAssessmentFAB: ExtendedFloatingActionButton
    private var assessmentID = 0
    var courseID = 0
    var termID = 0
    private lateinit var formatter: SimpleDateFormat
    lateinit var editAssessmentStatus: Spinner
    lateinit var editAssessmentType: Spinner
    lateinit var name: String
    lateinit var status: String
    lateinit var type: String
    private lateinit var editaAlert: SwitchCompat
    private lateinit var editAssessmentDueDate: TextView
    private lateinit var datePickerView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_assessment)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        db = DataBase.getInstance(applicationContext)!!
        termID = intent.getIntExtra("termID", -1)
        courseID = intent.getIntExtra("courseID", -1)
        assessmentID = intent.getIntExtra("assessmentID", -1)
        editAssessmentNameText = findViewById(R.id.editAssessmentNameText)
        editAssessmentType = findViewById(R.id.editAssessmentType)
        editAssessmentStatus = findViewById(R.id.editAssessmentStatus)
        editAssessmentDueDate = findViewById(R.id.editAssessmentDueDate)
        editaAlert = findViewById(R.id.editaAlert)
        updateAssessmentFAB = findViewById(R.id.updateAssessmentButton)
        setupDatePicker()
        setupSpinner()
        updateAssessmentFAB.setOnClickListener {
            try {
                updateAssessment()
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            if (assessmentUpdated) {
                val intent = Intent(applicationContext, AssessmentDetails::class.java)
                intent.putExtra("termID", termID)
                intent.putExtra("courseID", courseID)
                intent.putExtra("assessmentID", assessmentID)
                startActivity(intent)
            }
        }
        setValues()
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter.createFromResource(this, R.array.assessment_type_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        editAssessmentType.adapter = adapter
        editAssessmentType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                type = editAssessmentType.getItemAtPosition(i).toString()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        val adapter2 = ArrayAdapter.createFromResource(this, R.array.assessment_status_array, android.R.layout.simple_spinner_item)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        editAssessmentStatus.adapter = adapter2
        editAssessmentStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                status = editAssessmentStatus.getItemAtPosition(i).toString()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun setValues() {
        val assessment: Assessment? = db.assessmentDao()?.getAssessment(courseID, assessmentID)
        val name = assessment?.assessment_name
        val type = assessment?.assessment_type
        val status = assessment?.assessment_status
        val dDate = DateFormat.format("MM/dd/yyyy", assessment?.assessment_due_date).toString()
        val alert1 = assessment?.assessment_alert
        editAssessmentNameText.setText(name)
        type?.let { getIndex(editAssessmentType, it) }?.let { editAssessmentType.setSelection(it) }
        status?.let { getIndex(editAssessmentStatus, it) }?.let { editAssessmentStatus.setSelection(it) }
        editAssessmentDueDate.text = dDate
        if (alert1 != null) {
            editaAlert.isChecked = alert1
        }
    }

    private fun getIndex(spinner: Spinner?, myString: String): Int {
        if (spinner != null) {
            for (i in 0 until spinner.count) {
                if (spinner.getItemAtPosition(i).toString().equals(myString, ignoreCase = true)) {
                    return i
                }
            }
        }
        return 0
    }

    @Throws(ParseException::class)
    private fun updateAssessment() {
        formatter = SimpleDateFormat("MM/dd/yyyy")
        name = editAssessmentNameText.text.toString()
        val dDate = editAssessmentDueDate.text.toString()
        val cDate = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date())
        val alert = editaAlert.isChecked
        dueDate = formatter.parse(dDate)
        this.cDate = formatter.parse(cDate)
        if (name.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show()
            return
        }
        if (dDate.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "Due date is required", Toast.LENGTH_SHORT).show()
            return
        }
        val assessment = Assessment()
        assessment.course_id_fk = courseID
        assessment.assessment_id = assessmentID
        assessment.assessment_name = name
        assessment.assessment_type = type
        assessment.assessment_status = status
        assessment.assessment_due_date = dueDate
        assessment.assessment_alert = alert
        db.assessmentDao()?.updateAssessment(assessment)
        Toast.makeText(this, "$name has been updated", Toast.LENGTH_SHORT).show()
        assessmentUpdated = true
        if (alert) {
            AddAssessmentAlert()
        }
    }

    private fun AddAssessmentAlert() {
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

    private fun deleteAssessment() {
        val assessment: Assessment? = db.assessmentDao()?.getAssessment(courseID, assessmentID)
        db.assessmentDao()?.deleteAssessment(assessment)
        Toast.makeText(this, "Assessment has been deleted", Toast.LENGTH_SHORT).show()
        assessmentDeleted = true
    }

    private fun setupDatePicker() {
        editAssessmentDueDate.setOnClickListener {
            datePickerView = findViewById(R.id.editAssessmentDueDate)
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
        menuInflater.inflate(R.menu.delete_assessment, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.deleteAssessmentIC) {
            deleteAssessment()
            val intent = Intent(applicationContext, AssessmentDetails::class.java)
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