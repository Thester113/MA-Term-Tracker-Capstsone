package com.example.wgutscheduler.Activity

import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.wgutscheduler.DB.DataBase
import com.example.wgutscheduler.Entity.Term
import com.example.wgutscheduler.R
import com.example.wgutscheduler.Utilities.DatePickerFrag
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddTerm : AppCompatActivity(), OnDateSetListener {
    private var termAdded = false
    private lateinit var termName: EditText
    private var termDeleted = false
    private lateinit var saveTermFAB: ExtendedFloatingActionButton
    lateinit var db: DataBase
    private lateinit var formatter: SimpleDateFormat
    lateinit var status: Spinner
    lateinit var statusV: String
    private lateinit var endDate: TextView
    private lateinit var startDate: TextView
    private lateinit var datePickerView: TextView
    private var courseList = 0
    var termID = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_term)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        db = DataBase.getInstance(applicationContext)!!
        saveTermFAB = findViewById(R.id.saveTermButton)
        status = findViewById(R.id.addTermStatus)
        termName = findViewById(R.id.addTermName)
        termAdded = false
        startDate = findViewById(R.id.addSDateTerm)
        endDate = findViewById(R.id.addEDateTerm)
        setupDatePicker()
        val adapter = ArrayAdapter.createFromResource(this, R.array.term_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        status.setAdapter(adapter)
        status.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                statusV = status.getItemAtPosition(i).toString()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })
        saveTermFAB.setOnClickListener {
            try {
                addTerm()
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            if (termAdded) {
                val intent = Intent(applicationContext, TermList::class.java)
                startActivity(intent)
            }
        }
    }

    @Throws(ParseException::class)
    private fun addTerm() {
        formatter = SimpleDateFormat("MM/dd/yyyy")
        val name = termName.text.toString()
        val sDate = startDate.text.toString()
        val eDate = endDate.text.toString()
        val startDate = formatter.parse(sDate)
        val endDate = formatter.parse(eDate)
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
        val term = Term()
        term.term_name = name
        term.term_status = statusV
        term.term_start = startDate
        term.term_end = endDate
        db.termDao()?.insertTerm(term)
        Toast.makeText(this, "$name has been added", Toast.LENGTH_SHORT).show()
        termAdded = true
    }

    private fun setupDatePicker() {
//        startDate = findViewById(R.id.addSDateTerm);
//        endDate = findViewById(R.id.addEDateTerm);
        startDate.setOnClickListener {
            datePickerView = findViewById(R.id.addSDateTerm)
            val datePicker: DialogFragment = DatePickerFrag()
            datePicker.show(supportFragmentManager, "date picker")
        }
        endDate.setOnClickListener {
            datePickerView = findViewById(R.id.addEDateTerm)
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

    private fun deleteTerm() {
        if (courseList > 0) {
            Toast.makeText(this, "Cant delete a term with associated courses", Toast.LENGTH_SHORT).show()
            return
        }
        var term: Term? = Term()
        term = db.termDao()?.getTerm(termID)
        db.termDao()?.deleteTerm(term)
        Toast.makeText(this, "Term has been deleted", Toast.LENGTH_SHORT).show()
        termDeleted = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.deleteTermIC) {
            deleteTerm()
            val intent = Intent(applicationContext, TermDetails::class.java)
            intent.putExtra("termID", termID)
            startActivity(intent)
            return true
        } else if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}