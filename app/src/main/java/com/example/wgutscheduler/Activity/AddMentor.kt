package com.example.wgutscheduler.Activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wgutscheduler.DB.DataBase
import com.example.wgutscheduler.Entity.CourseMentor
import com.example.wgutscheduler.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class AddMentor : AppCompatActivity() {
    lateinit var db: DataBase
    private var mentorAdded = false
    private lateinit var addMentorEmailAddress: EditText
    private lateinit var addMentorName: EditText
    private lateinit var addMentorPhone: EditText
    private lateinit var addMentorFAB: ExtendedFloatingActionButton
    var courseID = 0
    var termID = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_mentor)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        db = DataBase.getInstance(applicationContext)!!
        termID = intent.getIntExtra("termID", -1)
        courseID = intent.getIntExtra("courseID", -1)
        addMentorName = findViewById(R.id.addMentorName)
        addMentorPhone = findViewById(R.id.addMentorPhone)
        addMentorEmailAddress = findViewById(R.id.addMentorEmailAddress)
        addMentorFAB = findViewById(R.id.addMentorFAB)
        addMentorFAB.setOnClickListener {
            addMentor()
            if (mentorAdded) {
                val intent = Intent(applicationContext, CourseDetails::class.java)
                intent.putExtra("termID", termID)
                intent.putExtra("courseID", courseID)
                startActivity(intent)
            }
        }
    }

    private fun addMentor() {
        val name = addMentorName.text.toString()
        val phone = addMentorPhone.text.toString()
        val email = addMentorEmailAddress.text.toString()
        if (name.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "A name is required", Toast.LENGTH_SHORT).show()
            return
        }
        if (phone.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "A phone number is required", Toast.LENGTH_SHORT).show()
            return
        }
        if (email.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "An email is required", Toast.LENGTH_SHORT).show()
            return
        }
        val mentor = CourseMentor()
        mentor.course_id_fk = courseID
        mentor.mentor_name = name
        mentor.mentor_phone = phone
        mentor.mentor_email = email
        db.MentorDao()?.insertMentor(mentor)
        Toast.makeText(this, "$name has been added", Toast.LENGTH_SHORT).show()
        mentorAdded = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.deleteMentorIC) {
            addMentor()
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