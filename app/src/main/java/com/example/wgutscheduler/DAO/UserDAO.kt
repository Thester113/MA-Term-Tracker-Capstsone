package com.example.wgutscheduler.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.wgutscheduler.Entity.User

@Dao
interface UserDAO{
    @Query("SELECT * FROM user WHERE user_name = :userName ORDER BY user_name")
    fun getUser(userName: String): User?

    @Query("SELECT EXISTS( SELECT * FROM user WHERE user_name = 'test')")
    fun hasTestUser() :Boolean?

    @Insert
    fun insertUser(user: User?)

}
