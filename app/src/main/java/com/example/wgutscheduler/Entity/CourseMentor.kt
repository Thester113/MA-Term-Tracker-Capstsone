package com.example.wgutscheduler.Entity

import com.example.wgutscheduler.Entity.Course
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "course_mentor", foreignKeys = [ForeignKey(entity = Course::class, parentColumns = ["course_id"], childColumns = ["course_id_fk"], onDelete = ForeignKey.CASCADE)])
open class CourseMentor {
    @PrimaryKey(autoGenerate = true)
    var mentor_id = 0

    @ColumnInfo(name = "course_id_fk")
    var course_id_fk = 0

    @ColumnInfo(name = "mentor_name")
    var mentor_name: String? = null

    @ColumnInfo(name = "mentor_phone")
    var mentor_phone: String? = null

    @ColumnInfo(name = "mentor_email")
    var mentor_email: String? = null

    override fun toString(): String {
        return mentor_name!!
    }
}