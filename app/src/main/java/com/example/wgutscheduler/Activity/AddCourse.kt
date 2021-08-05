package com.example.wgutscheduler.Activity

import android.Manifest
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
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

class AddCourse : AppCompatActivity(), OnDateSetListener {
    lateinit var db: DataBase
    private var courseAdded = false
    private lateinit var cDate: Date
    private lateinit var endDate: Date
    private lateinit var startDate: Date
    private lateinit var acSendNumber: EditText
    private lateinit var addCourseNameText: EditText
    private lateinit var addCourseNotes: EditText
    lateinit var acSendFAB: ExtendedFloatingActionButton
    var courseID = 0
    var termID = 0
    lateinit var formatter: SimpleDateFormat
    lateinit var addCourseStatus: Spinner
    private lateinit var message: String
    lateinit var name: String
    lateinit var phone: String
    lateinit var statusV: String
    lateinit var addCourseAlert: SwitchCompat
    private lateinit var addECourseTerm: TextView
    lateinit var addSCourseTerm: TextView
    private lateinit var datePickerView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        db = DataBase.getInstance(applicationContext)!!
        termID = intent.getIntExtra("termID", -1)
        addCourseNameText = findViewById(R.id.addCourseNameText)
        addCourseStatus = findViewById(R.id.addCourseStatus)
        addSCourseTerm = findViewById(R.id.addSCourseTerm)
        addECourseTerm = findViewById(R.id.addECourseTerm)
        addCourseAlert = findViewById(R.id.addCourseAlert)
        addCourseNotes = findViewById(R.id.addCourseNotes)
        acSendFAB = findViewById(R.id.acSendbutton)
        acSendNumber = findViewById(R.id.acSendNumber)
        setupDatePicker()
        val adapter = ArrayAdapter.createFromResource(this, R.array.course_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        addCourseStatus.adapter = adapter
        addCourseStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                statusV = addCourseStatus.getItemAtPosition(i).toString()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        acSendFAB.setOnClickListener { messagePermission() }
    }

    @Throws(ParseException::class)
    private fun addCourse() {
        formatter = SimpleDateFormat("MM/dd/yyyy")
        name = addCourseNameText.text.toString()
        val sDate = addSCourseTerm.text.toString()
        val eDate = addECourseTerm.text.toString()
        var notes = addCourseNotes.text.toString()
        val cDate = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date())
        val alert = addCourseAlert.isChecked
        startDate = formatter.parse(sDate)
        endDate = formatter.parse(eDate)
        this.cDate = formatter.parse(cDate)
        if (name.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show()
            return
        }
        if (startDate.after(endDate)) {
            Toast.makeText(this, "Start date cannot be after the end date", Toast.LENGTH_SHORT).show()
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
        course.course_name = name
        course.course_start = startDate
        course.course_end = endDate
        course.course_status = statusV
        course.course_notes = notes
        course.course_alert = alert
        db.courseDao()?.insertCourse(course)
        Toast.makeText(this, "$name has been added", Toast.LENGTH_SHORT).show()
        courseAdded = true
        if (alert) {
            AddCourseAlert()
        }
    }

    fun AddCourseAlert() {
        var course = Course()
        db.courseDao()?.getCurrentCourse(termID).also {
            if (it != null) {
                course = it
            }
        }
        courseID = course.course_id
        val sText = name + "starts today!"
        val eText = name + "ends today!"
        setAlert(courseID, startDate, name, sText)
        setAlert(courseID, endDate, name, eText)
    }

    private fun setAlert(ID: Int, date: Date?, title: String?, text: String) {
        val alertTime = Converter.dateToTimeStamp(date)
        if (date?.compareTo(cDate)!! < 0) {
            return
        }
        if (alertTime != null) {
            Notifications.setCourseAlert(applicationContext, ID, alertTime, title, text)
        }
        Toast.makeText(this, "Alarm for " + title + "added", Toast.LENGTH_SHORT).show()
    }

    private fun setupDatePicker() {
        addSCourseTerm.setOnClickListener {
            datePickerView = findViewById(R.id.addSCourseTerm)
            val datePicker: DialogFragment = DatePickerFrag()
            datePicker.show(supportFragmentManager, "date picker")
        }
        addECourseTerm.setOnClickListener {
            datePickerView = findViewById(R.id.addECourseTerm)
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
        // String currentDateString = month + "/" + dayOfMonth + "/" + year;
        val currentDateString = "$month/$dayOfMonth/$year"
        datePickerView.text = currentDateString
    }

    protected fun messagePermission() {
        phone = acSendNumber.text.toString()
        val notes = addCourseNotes.text.toString()
        val cName = addCourseNameText.text.toString()
        message = "Course: $cName  Notes: $notes"
        if (phone.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show()
            return
        }
        if (cName.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "Please enter a course name", Toast.LENGTH_SHORT).show()
            return
        }
        if (notes.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "Please add some notes", Toast.LENGTH_SHORT).show()
            return
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), REQUEST_READ_PHONE_STATE)
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), PERMISSIONS_REQUEST_SMS)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                sendSMSMessage()
            }
        }
    }

    protected fun sendSMSMessage() {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phone, null, message, null, null)
        Toast.makeText(applicationContext, "SMS message successfully sent", Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResult: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult)
        if (requestCode == PERMISSIONS_REQUEST_SMS) {
            if (grantResult.size > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(phone, null, message, null, null)
                Toast.makeText(applicationContext, "SMS message successfully sent", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(applicationContext, "SMS message failed, try again", Toast.LENGTH_LONG).show()
                return
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_course, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.addCourseFAB) {
            try {
                addCourse()
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            if (courseAdded) {
                val intent = Intent(applicationContext, TermDetails::class.java)
                intent.putExtra("termID", termID)
                startActivity(intent)
                return true
            }
        } else if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val PERMISSIONS_REQUEST_SMS = 0
        private const val REQUEST_READ_PHONE_STATE = 0
    }
}