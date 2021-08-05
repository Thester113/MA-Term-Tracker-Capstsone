package com.example.wgutscheduler.Entity

import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import java.util.*

@Entity(tableName = "course", foreignKeys = [ForeignKey(entity = Term::class, parentColumns = arrayOf("term_id"), childColumns = arrayOf("term_id_fk"), onDelete = ForeignKey.CASCADE)])
class Course {
    @PrimaryKey(autoGenerate = true)
    var course_id = 0

    @ColumnInfo(name = "term_id_fk")
    var term_id_fk = 0

    @ColumnInfo(name = "course_name")
    var course_name: String? = null

    @ColumnInfo(name = "course_start")
    var course_start: Date? = null

    @ColumnInfo(name = "course_end")
    var course_end: Date? = null

    @ColumnInfo(name = "course_status")
    var course_status: String? = null

    @ColumnInfo(name = "course_notes")
    var course_notes: String? = null

    @ColumnInfo(name = "course_alert")
    var course_alert = false
    override fun toString(): String {
        return course_name!!
    }
}