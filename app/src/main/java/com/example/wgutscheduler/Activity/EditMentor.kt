package com.example.wgutscheduler.Activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.wgutscheduler.DB.DataBase
import com.example.wgutscheduler.Entity.CourseInstructor
import com.example.wgutscheduler.Entity.CourseMentor
import com.example.wgutscheduler.Entity.ProgramMentor
import com.example.wgutscheduler.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class EditMentor : AppCompatActivity() {
    lateinit var db: DataBase
    private var mentorDeleted = false
    private var mentorUpdated = false
    private lateinit var updateMentorFAB: ExtendedFloatingActionButton
    private lateinit var editMentorEmailAddress: EditText
    private lateinit var editMentorName: EditText
    private lateinit var editMentorPhone: EditText
    var courseID = 0
    private var mentorID = 0
    var termID = 0
    lateinit var addMentorML: Spinner
    lateinit var statusV: String
    private lateinit var mentorType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_mentor)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        db = DataBase.getInstance(applicationContext)!!
        termID = intent.getIntExtra("termID", -1)
        courseID = intent.getIntExtra("courseID", -1)
        mentorID = intent.getIntExtra("mentorID", -1)
        mentorType = intent.getStringExtra("mentorType").orEmpty()
        editMentorName = findViewById(R.id.editMentorName)
        editMentorPhone = findViewById(R.id.editMentorPhone)
        editMentorEmailAddress = findViewById(R.id.editMentorEmailAddress)
        updateMentorFAB = findViewById(R.id.updateMentorFAB)
        addMentorML = findViewById(R.id.addMentorML)
        val adapter = ArrayAdapter.createFromResource(this, R.array.mentor_type_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        addMentorML.adapter = adapter
        addMentorML.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                statusV = addMentorML.getItemAtPosition(i).toString()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        setValues()
        updateMentorFAB.setOnClickListener {
            updateMentor()
            if (mentorUpdated) {
                val intent = Intent(applicationContext, MentorDetails::class.java)
                intent.putExtra("termID", termID)
                intent.putExtra("courseID", courseID)
                intent.putExtra("mentorID", mentorID)
                intent.putExtra("mentorType", mentorType)
                startActivity(intent)
            }
        }

    }

    private fun setValues() {
        when (mentorType) {
            "CourseMentor" -> {
                val mentor: CourseMentor? = db.MentorDao()?.getMentor(courseID, mentorID)
                val name = mentor?.mentor_name
                val phone = mentor?.mentor_phone
                val email = mentor?.mentor_email
                editMentorName.setText(name)
                editMentorPhone.setText(phone)
                editMentorEmailAddress.setText(email)
                addMentorML.setSelection(0)

            }
            "ProgramMentor" -> {
                val mentor: ProgramMentor? = db.MentorDao()?.getProgramMentor(courseID, mentorID)
                val name = mentor?.mentor_name
                val phone = mentor?.mentor_phone
                val email = mentor?.mentor_email
                editMentorName.setText(name)
                editMentorPhone.setText(phone)
                editMentorEmailAddress.setText(email)
                addMentorML.setSelection(1)


            }
            "CourseInstructor" -> {
                val mentor: CourseInstructor? = db.MentorDao()?.getCourseInstructor(courseID, mentorID)
                val name = mentor?.mentor_name
                val phone = mentor?.mentor_phone
                val email = mentor?.mentor_email
                editMentorName.setText(name)
                editMentorPhone.setText(phone)
                editMentorEmailAddress.setText(email)
                addMentorML.setSelection(2)

            }
        }

    }

    private fun updateMentor() {
        val name = editMentorName.text.toString()
        val phone = editMentorPhone.text.toString()
        val email = editMentorEmailAddress.text.toString()
        if (name.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show()
            return
        }
        if (phone.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "Number is required", Toast.LENGTH_SHORT).show()
            return
        }
        if (email.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
            return
        }
        when (statusV) {
            "Course Mentor" -> {
                val mentor = CourseMentor()
                mentor.course_id_fk = courseID
                mentor.mentor_name = name
                mentor.mentor_phone = phone
                mentor.mentor_email = email
                db.MentorDao()?.insertMentor(mentor)
            }
            "Program Mentor" -> {
                val mentor = ProgramMentor()
                mentor.course_id_fk = courseID
                mentor.mentor_name = name
                mentor.mentor_phone = phone
                mentor.mentor_email = email
                db.MentorDao()?.insertProgramMentor(mentor)

            }
            "Course Instructor" -> {
                val mentor = CourseInstructor()
                mentor.course_id_fk = courseID
                mentor.mentor_name = name
                mentor.mentor_phone = phone
                mentor.mentor_email = email
                db.MentorDao()?.insertCourseInstructor(mentor)

            }
        }
        Toast.makeText(this, "$name has been updated", Toast.LENGTH_SHORT).show()
        mentorUpdated = true
    }


    private fun deleteMentor() {
        when (statusV) {
            "Course Mentor" -> {
                db.MentorDao()?.deleteMentor(courseID, mentorID)
            }
            "Program Mentor" -> {
                db.MentorDao()?.deleteProgramMentor(courseID, mentorID)

            }
            "Course Instructor" -> {
                db.MentorDao()?.deleteCourseInstructor(courseID, mentorID)

            }
        }
        Toast.makeText(this, "Mentor has been deleted", Toast.LENGTH_SHORT).show()
        mentorDeleted = true

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.delete_mentor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.deleteMentorIC) {
            deleteMentor()
            val intent = Intent(applicationContext, MentorDetails::class.java)
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