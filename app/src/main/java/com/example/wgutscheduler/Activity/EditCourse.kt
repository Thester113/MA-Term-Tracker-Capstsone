package com.example.wgutscheduler.Activity

import android.Manifest
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.wgutscheduler.DB.DataBase
import com.example.wgutscheduler.Entity.Course
import com.example.wgutscheduler.R
import com.example.wgutscheduler.Utilities.Converter
import com.example.wgutscheduler.Utilities.DatePickerFrag
import com.example.wgutscheduler.Utilities.Notifications
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EditCourse : AppCompatActivity(), OnDateSetListener {
    lateinit var db: DataBase
    private var courseDeleted = false
    private var courseUpdated = false
    private lateinit var ecSendFAB: ExtendedFloatingActionButton
    private lateinit var updateCourseFAB: ExtendedFloatingActionButton
    private lateinit var cDate: Date
    private lateinit var endDate: Date
    private lateinit var startDate: Date
    private lateinit var ecSendNumber: EditText
    private lateinit var editCourseNameText: EditText
    private lateinit var editCourseNotes: EditText
    private var assessmentList = 0
    var courseID = 0
    private var mentorList = 0
    var termID = 0
    private  lateinit var formatter: SimpleDateFormat
    lateinit var editCourseStatus: Spinner
    private lateinit var message: String
    lateinit var name: String
    lateinit var phone: String
    lateinit var statusV: String
    private lateinit var editCourseAlert: SwitchCompat
    private lateinit var editECourseTerm: TextView
    private lateinit var editSCourseTerm: TextView
    private  lateinit var datePickerView: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_course)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        db = DataBase.getInstance(applicationContext)!!
        termID = intent.getIntExtra("termID", -1)
        courseID = intent.getIntExtra("courseID", -1)
        mentorList = intent.getIntExtra("mentorList", -1)
        assessmentList = intent.getIntExtra("assessmentList", -1)
        editCourseNameText = findViewById(R.id.editCourseNameText)
        editCourseStatus = findViewById(R.id.editCourseStatus)
        editSCourseTerm = findViewById(R.id.editSCourseTerm)
        editECourseTerm = findViewById(R.id.editECourseTerm)
        editCourseAlert = findViewById(R.id.editCourseAlert)
        editCourseNotes = findViewById(R.id.editCourseNotes)
        updateCourseFAB = findViewById(R.id.updateCourseFAB)
        ecSendFAB = findViewById(R.id.ecSendFAB)
        ecSendNumber = findViewById(R.id.ecSendNumber)
        setupDatePicker()
        val adapter = ArrayAdapter.createFromResource(this, R.array.course_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        editCourseStatus.adapter = adapter
        editCourseStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                statusV = editCourseStatus.getItemAtPosition(i).toString()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        setValues()
        updateCourseFAB.setOnClickListener {
            try {
                updateCourse()
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            if (courseUpdated) {
                val intent = Intent(applicationContext, CourseDetails::class.java)
                intent.putExtra("termID", termID)
                intent.putExtra("courseID", courseID)
                startActivity(intent)
            }
        }
        ecSendFAB.setOnClickListener { messagePermission() }
    }

    private fun setValues() {
        val course: Course? = db.courseDao()?.getCourse(termID, courseID)
        val name = course?.course_name
        val status = course?.course_status
        val sDate = DateFormat.format("MM/dd/yyyy", course?.course_start).toString()
        val eDate = DateFormat.format("MM/dd/yyyy", course?.course_end).toString()
        val alert1 = course?.course_alert
        val notes = course?.course_notes
        editCourseNameText.setText(name)
        status?.let { getIndex(editCourseStatus, it) }?.let { editCourseStatus.setSelection(it) }
        editSCourseTerm.text = sDate
        editECourseTerm.text = eDate
        if (alert1 != null) {
            editCourseAlert.isChecked = alert1
        }
        editCourseNotes.setText(notes)
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

    private fun setupDatePicker() {
        editSCourseTerm.setOnClickListener {
            datePickerView = findViewById(R.id.editSCourseTerm)
            val datePicker: DialogFragment = DatePickerFrag()
            datePicker.show(supportFragmentManager, "date picker")
        }
        editECourseTerm.setOnClickListener {
            datePickerView = findViewById(R.id.editECourseTerm)
            val datePicker: DialogFragment = DatePickerFrag()
            datePicker.show(supportFragmentManager, "date picker")
        }
    }

    private fun deleteCourse() {
        if (mentorList > 0) {
            Toast.makeText(this, "Must delete all mentors associated with this course before deleting", Toast.LENGTH_SHORT).show()
            return
        }
        if (assessmentList > 0) {
            Toast.makeText(this, "Must delete all assessments associated with this course before deleting", Toast.LENGTH_SHORT).show()
            return
        }
        val course: Course? = db.courseDao()?.getCourse(termID, courseID)
        db.courseDao()?.deleteCourse(course)
        Toast.makeText(this, "Course has been deleted", Toast.LENGTH_SHORT).show()
        courseDeleted = true
    }

    @Throws(ParseException::class)
    private fun updateCourse() {
        formatter = SimpleDateFormat("MM/dd/yyyy")
        name = editCourseNameText.text.toString()
        val sDate = editSCourseTerm.text.toString()
        val eDate = editECourseTerm.text.toString()
        var notes = editCourseNotes.text.toString()
        val cDate = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date())
        val alert = editCourseAlert.isChecked
        startDate = formatter.parse(sDate)
        endDate = formatter.parse(eDate)
        this.cDate = formatter.parse(cDate)
        if (name.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show()
            return
        }
        if (startDate.after(endDate)) {
            Toast.makeText(this, "Start date cant be after the end date", Toast.LENGTH_SHORT).show()
            return
        }
        if (sDate.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "Start date is required", Toast.LENGTH_SHORT).show()
            return
        }
        if (eDate.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "End date is required", Toast.LENGTH_SHORT).show()
            return
        }
        if (notes.trim { it <= ' ' }.isEmpty()) {
            notes = " "
        }
        val course = Course()
        course.term_id_fk = termID
        course.course_id = courseID
        course.course_name = name
        course.course_start = startDate
        course.course_end = endDate
        course.course_status = statusV
        course.course_notes = notes
        course.course_alert = alert
        db.courseDao()?.updateCourse(course)
        Toast.makeText(this, "$name has been updated", Toast.LENGTH_SHORT).show()
        courseUpdated = true
        if (alert) {
            AddCourseAlert()
        }
    }

    private fun AddCourseAlert() {
        val sText = "$name starts today!"
        val eText = "$name ends today!"
        setAlert(courseID, startDate, name, sText)
        setAlert(courseID, endDate, name, eText)
    }

    private fun setAlert(ID: Int, date: Date?, title: String?, text: String) {
        val alertTime = Converter.dateToTimeStamp(date)
        if (date != null) {
            if (date < cDate) {
                return
            }
        }
        if (alertTime != null) {
            Notifications.setCourseAlert(applicationContext, ID, alertTime, title, text)
        }
        Toast.makeText(this, "Course alarm enabled", Toast.LENGTH_SHORT).show()
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

    private fun messagePermission() {
        phone = ecSendNumber.text.toString()
        val notes = editCourseNotes.text.toString()
        val cName = editCourseNameText.text.toString()
        message = "Course: $cName  Notes: $notes"
        if (phone.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "A phone number is required", Toast.LENGTH_SHORT).show()
            return
        }
        if (cName.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "A course name is required", Toast.LENGTH_SHORT).show()
            return
        }
        if (notes.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "Please add some notes", Toast.LENGTH_SHORT).show()
            return
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), PERMISSIONS_REQUEST_SMS)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            sendSMSMessage()
        }
    }

    private fun sendSMSMessage() {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phone, null, message, null, null)
        Toast.makeText(applicationContext, "SMS message sent successfully", Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResult: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult)
        if (requestCode == PERMISSIONS_REQUEST_SMS) {
            if (grantResult.isNotEmpty() && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(phone, null, message, null, null)
                Toast.makeText(applicationContext, "SMS message sent successfully", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(applicationContext, "SMS message failed, please try again", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.delete_course, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.deleteCourseIC) {
            deleteCourse()
            val intent = Intent(applicationContext, TermList::class.java)
            intent.putExtra("termID", termID)
            startActivity(intent)
            return true
        } else if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val PERMISSIONS_REQUEST_SMS = 0
    }
}