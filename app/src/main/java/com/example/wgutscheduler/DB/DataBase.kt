package com.example.wgutscheduler.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.wgutscheduler.DAO.*
import com.example.wgutscheduler.Entity.*
import com.example.wgutscheduler.Utilities.Converter

@Database(entities = [Term::class, Course::class, CourseMentor::class, ProgramMentor::class,CourseInstructor::class, Assessment::class, User::class], exportSchema = false, version = 5)
@TypeConverters(Converter::class)
abstract class DataBase : RoomDatabase() {
    abstract fun termDao(): TermDAO?
    abstract fun courseDao(): CourseDAO?
    abstract fun MentorDao(): MentorDAO?
    abstract fun assessmentDao(): AssessmentDAO?
    abstract fun UserDao():UserDAO?

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