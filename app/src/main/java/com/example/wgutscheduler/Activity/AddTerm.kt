package com.example.wgutscheduler.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.wgutscheduler.DB.DataBase;
import com.example.wgutscheduler.Entity.Term;
import com.example.wgutscheduler.R;
import com.example.wgutscheduler.Utilities.DatePickerFrag;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class AddTerm extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    boolean termAdded;
    EditText termName;
    boolean termDeleted;
    ExtendedFloatingActionButton saveTermFAB;
    DataBase db;
    SimpleDateFormat formatter;
    Spinner status;
    String statusV;
    TextView endDate;
    TextView startDate;
    private TextView datePickerView;
    int courseList;
    int termID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_term);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        db = DataBase.getInstance(getApplicationContext());
        saveTermFAB = findViewById(R.id.saveTermButton);
        status = findViewById(R.id.addTermStatus);
        termName = findViewById(R.id.addTermName);
        termAdded = false;
        startDate = findViewById(R.id.addSDateTerm);
        endDate = findViewById(R.id.addEDateTerm);

        setupDatePicker();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.term_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(adapter);

        status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                statusV = status.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        saveTermFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addTerm();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (termAdded) {
                    Intent intent = new Intent(getApplicationContext(), TermList.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void addTerm() throws ParseException {
        formatter = new SimpleDateFormat("MM/dd/yyyy");
        String name = termName.getText().toString();
        String sDate = startDate.getText().toString();
        String eDate = endDate.getText().toString();
        Date startDate = formatter.parse(sDate);
        Date endDate = formatter.parse(eDate);

        if (name.trim().isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (startDate.after(endDate)) {
            Toast.makeText(this, "Start date cant be after the end date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (sDate.trim().isEmpty()) {
            Toast.makeText(this, "Start date is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (eDate.trim().isEmpty()) {
            Toast.makeText(this, "End date is required", Toast.LENGTH_SHORT).show();
            return;
        }


        Term term = new Term();
        term.setTerm_name(name);
        term.setTerm_status(statusV);
        term.setTerm_start(startDate);
        term.setTerm_end(endDate);
        db.termDao().insertTerm(term);
        Toast.makeText(this, name + " has been added", Toast.LENGTH_SHORT).show();
        termAdded = true;
    }

    private void setupDatePicker() {
//        startDate = findViewById(R.id.addSDateTerm);
//        endDate = findViewById(R.id.addEDateTerm);

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerView = findViewById(R.id.addSDateTerm);
                DialogFragment datePicker = new DatePickerFrag();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerView = findViewById(R.id.addEDateTerm);
                DialogFragment datePicker = new DatePickerFrag();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month = month + 1);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = month + "/" + dayOfMonth + "/" + year;
        datePickerView.setText(currentDateString);
    }
    private void deleteTerm() {
        if (courseList > 0) {
            Toast.makeText(this, "Cant delete a term with associated courses", Toast.LENGTH_SHORT).show();
            return;
        }
        Term term = new Term();
        term = db.termDao().getTerm(termID);
        db.termDao().deleteTerm(term);
        Toast.makeText(this, "Term has been deleted", Toast.LENGTH_SHORT).show();
        termDeleted = true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.deleteTermIC) {
            deleteTerm();
            Intent intent = new Intent(getApplicationContext(), TermDetails.class);
            intent.putExtra("termID", termID);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
