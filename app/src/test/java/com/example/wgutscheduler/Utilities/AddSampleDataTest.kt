package com.example.wgutscheduler.Utilities

import com.example.wgutscheduler.DAO.TermDAO
import com.example.wgutscheduler.DB.DataBase
import com.example.wgutscheduler.Entity.Term
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.mockito.Mockito.*

/**
 * Created by Thomas Hester on 8/19/21 in the Software Development program.
 */


class AddSampleDataTest {

    @Test
    fun `insert sample terms test`(){
        //Arrange/Given
        val dbMock = mock(DataBase::class.java)
        val termDaoMock = mock(TermDAO::class.java)
        val termList = mutableListOf<Term>()


        `when`(dbMock.termDao()).thenReturn(termDaoMock)
        `when`(termDaoMock.insertAllTerms(anyList<Term>())).then {
            termList.addAll(it.arguments.first() as List<Term>)
        }
        //Act/When
        AddSampleData(dbMock).insertTerms()
        //Assert/Then
        assertThat(termList).hasSize(4)
        assertThat(termList.first().term_name).isEqualTo("Spring 2021")
    }



}