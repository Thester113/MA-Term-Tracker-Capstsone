package com.example.wgutscheduler.Entity

import com.example.wgutscheduler.Entity.Course
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import java.util.*

@Entity(tableName = "assessment", foreignKeys = [ForeignKey(entity = Course::class, parentColumns = arrayOf("course_id"), childColumns = arrayOf("course_id_fk"), onDelete = ForeignKey.CASCADE)])
class Assessment {
    @PrimaryKey(autoGenerate = true)
    var assessment_id = 0

    @ColumnInfo(name = "course_id_fk")
    var course_id_fk = 0

    @ColumnInfo(name = "assessment_name")
    var assessment_name: String? = null

    @ColumnInfo(name = "assessment_type")
    var assessment_type: String? = null

    @ColumnInfo(name = "assessment_status")
    var assessment_status: String? = null

    @ColumnInfo(name = "assessment_due_date")
    var assessment_due_date: Date? = null

    @ColumnInfo(name = "assessment_alert")
    var assessment_alert = false
    override fun toString(): String {
        return assessment_name!!
    }
}