package com.example.wgutscheduler.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wgutscheduler.DB.DataBase
import com.example.wgutscheduler.R
import com.example.wgutscheduler.adapters.TermAdapter


class SearchActivity : AppCompatActivity() {
    lateinit var db: DataBase
    lateinit var termList: RecyclerView
    lateinit var courseList: RecyclerView
    lateinit var assessmentList: RecyclerView
    lateinit var searchView: SearchView
    val termListAdapter : TermAdapter by lazy { TermAdapter(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        db = DataBase.getInstance(applicationContext)!!
        searchView = findViewById(R.id.search_bar)
        termList = findViewById(R.id.TermList)
        termList.adapter = termListAdapter
        termList.layoutManager = LinearLayoutManager(this)
        courseList = findViewById(R.id.CourseList)
        assessmentList = findViewById(R.id.AssessmentList)
        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val dbTerm = db.termDao()?.searchTerms("%$newText%")
              termListAdapter.submitList(dbTerm)
                return true
            }
        })

    }
}