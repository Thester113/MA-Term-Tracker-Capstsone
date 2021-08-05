package com.example.wgutscheduler.DAO

import androidx.room.*
import com.example.wgutscheduler.Entity.Term

@Dao
interface TermDAO {
    @get:Query("SELECT * FROM term ORDER BY term_id")
    val termList: List<Term?>?

    @Query("SELECT * FROM term WHERE term_id = :termID ORDER BY term_id")
    fun getTerm(termID: Int): Term?

    @get:Query("SELECT * FROM term")
    val allTerms: List<Term?>?

    @Insert
    fun insertTerm(term: Term?)

    @Insert
    fun insertAllTerms(vararg term: Term?)

    @Update
    fun updateTerm(term: Term?)

    @Delete
    fun deleteTerm(term: Term?)
}