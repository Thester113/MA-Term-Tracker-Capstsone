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
import androidx.fragment.app.DialogFragment
import com.example.wgutscheduler.DB.DataBase
import com.example.wgutscheduler.Entity.Term
import com.example.wgutscheduler.R
import com.example.wgutscheduler.Utilities.DatePickerFrag
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EditTerm : AppCompatActivity(), OnDateSetListener {
    lateinit var db: DataBase
    private var termDeleted = false
    private var termUpdated = false
    private lateinit var editTermName: EditText
    private lateinit var updateTermButton: ExtendedFloatingActionButton
    private var courseList = 0
    var termID = 0
    private lateinit var formatter: SimpleDateFormat
    lateinit var editTermStatus: Spinner
    lateinit var statusV: String
    private lateinit var editEDateTerm: TextView
    private lateinit var editSDateTerm: TextView
    private  lateinit var datePickerView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_term)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        db = DataBase.getInstance(applicationContext)!!
        termID = intent.getIntExtra("termID", -1)
        courseList = intent.getIntExtra("courseList", -1)
        editTermName = findViewById(R.id.editTermName)
        editTermStatus = findViewById(R.id.editTermStatus)
        editSDateTerm = findViewById(R.id.editSDateTerm)
        editEDateTerm = findViewById(R.id.editEDateTerm)
        updateTermButton = findViewById(R.id.updateTermFAB)
        setupDatePicker()
        val adapter = ArrayAdapter.createFromResource(this, R.array.term_array, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        editTermStatus.adapter = adapter
        editTermStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                statusV = editTermStatus.getItemAtPosition(i).toString()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        setValues()
        updateTermButton.setOnClickListener {
            try {
                updateTerm()
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            if (termUpdated) {
                val intent = Intent(applicationContext, TermDetails::class.java)
                intent.putExtra("termID", termID)
                startActivity(intent)
            }
        }
    }

    private fun setValues() {
        try {
            val term: Term? = db.termDao()?.getTerm(termID)
            val name = term?.term_name
            val status = term?.term_status
            val startDate = DateFormat.format("MM/dd/yyyy", term?.term_start).toString()
            val endDate = DateFormat.format("MM/dd/yyyy", term?.term_end).toString()
            editTermName.setText(name)
            status?.let { getIndex(editTermStatus, it) }?.let { editTermStatus.setSelection(it) }
            editSDateTerm.text = startDate
            editEDateTerm.text = endDate
        } catch (e: RuntimeException) {
            println("Caught RuntimeException")
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

    private fun deleteTerm() {
        if (courseList > 0) {
            Toast.makeText(this, "Cant delete a term with associated courses", Toast.LENGTH_SHORT).show()
            return
        }
        val term: Term? = db.termDao()?.getTerm(termID)
        db.termDao()?.deleteTerm(term)
        Toast.makeText(this, "Term has been deleted", Toast.LENGTH_SHORT).show()
        termDeleted = true
    }

    @Throws(ParseException::class)
    private fun updateTerm() {
        formatter = SimpleDateFormat("MM/dd/yyyy")
        val name = editTermName.text.toString()
        val sDate = editSDateTerm.text.toString()
        val eDate = editEDateTerm.text.toString()
        val stDate = formatter.parse(sDate)
        val enDate = formatter.parse(eDate)
        if (name.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show()
            return
        }
        if (stDate.after(enDate)) {
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
        term.term_id = termID
        term.term_name = name
        term.term_status = statusV
        term.term_start = stDate
        term.term_end = enDate
        db.termDao()?.updateTerm(term)
        Toast.makeText(this, "Term has been updated.", Toast.LENGTH_SHORT).show()
        termUpdated = true
    }

    private fun setupDatePicker() {
        editSDateTerm.setOnClickListener {
            datePickerView = findViewById(R.id.editSDateTerm)
            val datePicker: DialogFragment = DatePickerFrag()
            datePicker.show(supportFragmentManager, "date picker")
        }
        editEDateTerm.setOnClickListener {
            datePickerView = findViewById(R.id.editEDateTerm)
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
        menuInflater.inflate(R.menu.delete_term, menu)
        return true
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