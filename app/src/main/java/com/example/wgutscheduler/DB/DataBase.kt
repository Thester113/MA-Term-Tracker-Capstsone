package com.example.wgutscheduler.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.wgutscheduler.DAO.AssessmentDAO
import com.example.wgutscheduler.DAO.CourseDAO
import com.example.wgutscheduler.DAO.MentorDAO
import com.example.wgutscheduler.DAO.TermDAO
import com.example.wgutscheduler.Entity.Assessment
import com.example.wgutscheduler.Entity.Course
import com.example.wgutscheduler.Entity.CourseMentor
import com.example.wgutscheduler.Entity.Term
import com.example.wgutscheduler.Utilities.Converter

@Database(entities = [Term::class, Course::class, CourseMentor::class, Assessment::class], exportSchema = false, version = 5)
@TypeConverters(Converter::class)
abstract class DataBase : RoomDatabase() {
    abstract fun termDao(): TermDAO?
    abstract fun courseDao(): CourseDAO?
    abstract fun MentorDao(): MentorDAO?
    abstract fun assessmentDao(): AssessmentDAO?

    companion object {
        private const val DB_Name = "WGUTScheduler.db"
        private var instance: DataBase? = null
        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): DataBase? {
            if (instance == null) {
                instance = Room.databaseBuilder(context.applicationContext, DataBase::class.java, DB_Name).allowMainThreadQueries().fallbackToDestructiveMigration().build()
            }
            return instance
        }
    }
}