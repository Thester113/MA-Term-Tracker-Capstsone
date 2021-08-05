package com.example.wgutscheduler.DAO;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.wgutscheduler.Entity.Term;

import java.util.List;

@Dao
public interface TermDAO {
    @Query("SELECT * FROM term ORDER BY term_id")
    List<Term> getTermList();

    @Query("SELECT * FROM term WHERE term_id = :termID ORDER BY term_id")
    Term getTerm(int termID);

    @Query("SELECT * FROM term")
    List<Term> getAllTerms();

    @Insert
    void insertTerm(Term term);

    @Insert
    void insertAllTerms(Term... term);

    @Update
    void updateTerm(Term term);

    @Delete
    void deleteTerm(Term term);
}

