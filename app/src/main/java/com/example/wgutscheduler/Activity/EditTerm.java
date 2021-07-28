package com.example.wgutscheduler.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
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

public class EditTerm extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    DataBase db;
    boolean termDeleted;
    boolean termUpdated;
    EditText editTermName;
    ExtendedFloatingActionButton updateTermButton;
    int courseList;
    Intent intent;
    int termID;
    SimpleDateFormat formatter;
    Spinner editTermStatus;
    String statusV;
    TextView editEDateTerm;
    TextView editSDateTerm;
    private TextView datePickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_term);
        intent = getIntent();
        db = DataBase.getInstance(getApplicationContext());
        termID = intent.getIntExtra("termID", -1);
        courseList = intent.getIntExtra("courseList", -1);
        editTermName = findViewById(R.id.editTermName);
        editTermStatus = findViewById(R.id.editTermStatus);
        editSDateTerm = findViewById(R.id.editSDateTerm);
        editEDateTerm = findViewById(R.id.editEDateTerm);
        updateTermButton = findViewById(R.id.updateTermFAB);

        setupDatePicker();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.term_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editTermStatus.setAdapter(adapter);

        editTermStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                statusV = editTermStatus.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        setValues();
        updateTermButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    updateTerm();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (termUpdated) {
                    Intent intent = new Intent(getApplicationContext(), TermDetails.class);
                    intent.putExtra("termID", termID);
                    startActivity(intent);
                }
            }
        });
    }

    private void setValues() {
        try{
            Term term = new Term();
            term = db.termDao().getTerm(termID);
            String name = term.getTerm_name();
            String status = term.getTerm_status();
            String startDate = DateFormat.format("MM/dd/yyyy", term.getTerm_start()).toString();
            String endDate = DateFormat.format("MM/dd/yyyy", term.getTerm_end()).toString();

            editTermName.setText(name);
            editTermStatus.setSelection(getIndex(editTermStatus, status));
            editSDateTerm.setText(startDate);
            editEDateTerm.setText(endDate);

        } catch(RuntimeException e) {
            System.out.println("Caught RuntimeException");
        }

    }

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }
        return 0;
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

    private void updateTerm() throws ParseException {
        formatter = new SimpleDateFormat("MM/dd/yyyy");
        String name = editTermName.getText().toString();
        String sDate = editSDateTerm.getText().toString();
        String eDate = editEDateTerm.getText().toString();
        Date stDate = formatter.parse(sDate);
        Date enDate = formatter.parse(eDate);

        if (name.trim().isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (stDate.after(enDate)) {
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
        term.setTerm_id(termID);
        term.setTerm_name(name);
        term.setTerm_status(statusV);
        term.setTerm_start(stDate);
        term.setTerm_end(enDate);
        db.termDao().updateTerm(term);
        Toast.makeText(this, "Term has been updated.", Toast.LENGTH_SHORT).show();
        termUpdated = true;
    }

    private void setupDatePicker() {

        editSDateTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerView = findViewById(R.id.editSDateTerm);
                DialogFragment datePicker = new DatePickerFrag();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        editEDateTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerView = findViewById(R.id.editEDateTerm);
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.delete_term, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteTermIC:
                deleteTerm();
                Intent intent = new Intent(getApplicationContext(), TermList.class);
                intent.putExtra("termID", termID);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
