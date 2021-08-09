package com.example.wgutscheduler.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
class User {
    @PrimaryKey(autoGenerate = true)
    var user_id = 0

    @ColumnInfo(name = "user_password")
    var user_password: String? = null

    @ColumnInfo(name = "user_name")
    var user_name: String? = null

    override fun toString(): String {
        return user_name!!
    }
}
