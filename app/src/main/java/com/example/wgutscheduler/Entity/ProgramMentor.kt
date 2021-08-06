package com.example.wgutscheduler.Entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "program_mentor", foreignKeys = [ForeignKey(entity = Course::class, parentColumns = ["course_id"], childColumns = ["course_id_fk"], onDelete = ForeignKey.CASCADE)])
    class ProgramMentor :CourseMentor()

