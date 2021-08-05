package com.example.wgutscheduler.Entity

import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.*

@Entity(tableName = "term")
class Term {
    @PrimaryKey(autoGenerate = true)
    var term_id = 0

    @ColumnInfo(name = "term_name")
    var term_name: String? = null

    @ColumnInfo(name = "term_status")
    var term_status: String? = null

    @ColumnInfo(name = "term_start")
    var term_start: Date? = null

    @ColumnInfo(name = "term_end")
    var term_end: Date? = null
    override fun toString(): String {
        return term_name!!
    }
}