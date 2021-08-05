package com.example.wgutscheduler.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.wgutscheduler.DB.DataBase
import com.example.wgutscheduler.Entity.Term
import com.example.wgutscheduler.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class TermList : AppCompatActivity() {
    lateinit var db: DataBase
    private lateinit var addTermFAB: ExtendedFloatingActionButton
    private lateinit var allTerms: MutableList<Term>
    private lateinit var termList: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_term_list)
        termList = findViewById(R.id.tdTermList)
        db = DataBase.getInstance(applicationContext)!!
        addTermFAB = findViewById(R.id.addTermFAB)
        termList.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            val intent = Intent(applicationContext, TermDetails::class.java)
            intent.putExtra("termID", allTerms[position].term_id)
            startActivity(intent)
            println(id)
        }
        updateTermList()
        addTermFAB.setOnClickListener {
            val intent = Intent(applicationContext, AddTerm::class.java)
            startActivity(intent)
        }
    }

    private fun updateTermList() {
        val allTerms = db.termDao()?.termList
        val adapter = allTerms?.let { ArrayAdapter(this, android.R.layout.simple_list_item_1, it.filterNotNull()) }
        termList.adapter = adapter
        if (allTerms != null) {
            this.allTerms = allTerms.filterNotNull().toMutableList()
        }
        adapter?.notifyDataSetChanged()
    }

}