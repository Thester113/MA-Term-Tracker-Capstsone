package com.example.wgutscheduler.Activity

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wgutscheduler.DB.DataBase
import com.example.wgutscheduler.R
import com.example.wgutscheduler.adapters.AssessmentAdapter
import com.example.wgutscheduler.adapters.CourseAdapter
import com.example.wgutscheduler.adapters.TermAdapter


class SearchActivity : AppCompatActivity() {
    lateinit var db: DataBase
    lateinit var termList: RecyclerView
    lateinit var courseList: RecyclerView
    lateinit var assessmentList: RecyclerView
    lateinit var searchView: SearchView
    val termListAdapter: TermAdapter by lazy { TermAdapter(layoutInflater) }
    val courseListAdapter: CourseAdapter by lazy { CourseAdapter(layoutInflater) }
    val assessmentListAdapter: AssessmentAdapter by lazy { AssessmentAdapter(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        db = DataBase.getInstance(applicationContext)!!
        searchView = findViewById(R.id.search_bar)
        termList = findViewById(R.id.TermList)
        termList.adapter = termListAdapter
        termList.layoutManager = LinearLayoutManager(this)
        courseList = findViewById(R.id.CourseList)
        courseList.adapter = courseListAdapter
        courseList.layoutManager = LinearLayoutManager(this)
        assessmentList = findViewById(R.id.AssessmentList)
        assessmentList.adapter = assessmentListAdapter
        assessmentList.layoutManager = LinearLayoutManager(this)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val dbTerm = db.termDao()?.searchTerms("%$newText%")
                val dbCourse = db.courseDao()?.searchCourses("%$newText%")
                val assessCourse = db.assessmentDao()?.searchAssessments("%$newText%")

                courseListAdapter.submitList(dbCourse)
                termListAdapter.submitList(dbTerm)
                assessmentListAdapter.submitList(assessCourse)
                return true
            }
        })

    }
}