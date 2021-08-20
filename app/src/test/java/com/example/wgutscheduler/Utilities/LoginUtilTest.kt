package com.example.wgutscheduler.Utilities

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Created by Thomas Hester on 8/19/21 in the Software Development program.
 */
class LoginUtilTest {
    @Test
    fun `test hash`(){
        //Arrange/Given
        val someWords = "abcd"
        //Act/When
        val hash = hash(someWords)
        //Assert/Then
        assertThat(hash).isEqualTo("E2FC714C4727EE9395F324CD2E7F331F")
    }


}