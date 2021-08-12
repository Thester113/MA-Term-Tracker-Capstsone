package com.example.wgutscheduler.Activity

import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import com.example.wgutscheduler.DB.DataBase
import com.example.wgutscheduler.Entity.Course
import com.example.wgutscheduler.Entity.Term
import com.example.wgutscheduler.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class TermDetails : AppCompatActivity() {
    lateinit var db: DataBase
    private lateinit var tdAddClassFAB: ExtendedFloatingActionButton
    var termID = 0
    private lateinit var allCourses: List<Course>
    private lateinit var tdClassList: ListView
    private lateinit var tdeDate: TextView
    private lateinit var tdName: TextView
    private lateinit var tdsDate: TextView
    private lateinit var tdStatus: TextView
    private lateinit var reportBar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_term_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        tdClassList = findViewById(R.id.tdClassList)
        db = DataBase.getInstance(applicationContext)!!
        termID = intent.getIntExtra("termID", -1)
        tdName = findViewById(R.id.tdName)
        tdStatus = findViewById(R.id.tdStatus)
        tdName = findViewById(R.id.tdName)
        tdsDate = findViewById(R.id.tdSdate)
        tdeDate = findViewById(R.id.tdEdate)
        tdAddClassFAB = findViewById(R.id.tdAddClassFAB)
        reportBar = findViewById(R.id.report_bar)
        updateClassList()
        setValues()
        tdAddClassFAB.setOnClickListener {
            val intent = Intent(applicationContext, AddCourse::class.java)
            intent.putExtra("termID", termID)
            startActivity(intent)
        }
        tdClassList.onItemClickListener = OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            val intent = Intent(applicationContext, CourseDetails::class.java)
            intent.putExtra("termID", termID)
            intent.putExtra("courseID", allCourses[position].course_id)
            startActivity(intent)
            println(id)
        }
        reportBar.setOnClickListener {
            val intent = Intent(applicationContext, ReportsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setValues() {
        try {
            val term: Term? = db.termDao()?.getTerm(termID)
            val name = term?.term_name
            val status = term?.term_status
            val sDate = DateFormat.format("MM/dd/yyyy", term?.term_start).toString()
            val eDate = DateFormat.format("MM/dd/yyyy", term?.term_end).toString()
            tdName.text = name
            tdStatus.text = status
            tdsDate.text = sDate
            tdeDate.text = eDate
        } catch (e: NullPointerException) {
            println("NullPointerException caught")
        }
    }

    private fun updateClassList() {
        val allCourses = db.courseDao()?.getCourseList(termID)
        val adapter = allCourses?.let { ArrayAdapter(this, android.R.layout.simple_list_item_1, it.filterNotNull()) }
        tdClassList.adapter = adapter
        if (allCourses != null) {
            this.allCourses = allCourses.filterNotNull()
        }
        adapter?.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.edit_term, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.tdEditTermFAB) {
            val intent = Intent(applicationContext, EditTerm::class.java)
            intent.putExtra("termID", termID)
            intent.putExtra("courseList", allCourses.size)
            startActivity(intent)
            return true
        } else if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}