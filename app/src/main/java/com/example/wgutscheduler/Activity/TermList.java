package com.example.wgutscheduler.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wgutscheduler.DB.DataBase;
import com.example.wgutscheduler.Entity.Term;
import com.example.wgutscheduler.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

public class TermList extends AppCompatActivity {
    DataBase db;
    ExtendedFloatingActionButton addTermFAB;
    List<Term> allTerms;
    ListView termList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_list);
        termList = findViewById(R.id.tdTermList);
        db = DataBase.getInstance(getApplicationContext());
        addTermFAB = findViewById(R.id.addTermFAB);

        termList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getApplicationContext(), TermDetails.class);
            intent.putExtra("termID", allTerms.get(position).getTerm_id());
            startActivity(intent);
            System.out.println(id);
        });

        updateTermList();

        addTermFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddTerm.class);
                startActivity(intent);
            }
        });
    }

    private void updateTermList() {
        List<Term> allTerms = db.termDao().getTermList();
        ArrayAdapter<Term> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allTerms);
        termList.setAdapter(adapter);
        this.allTerms = allTerms;

        adapter.notifyDataSetChanged();
    }
}
