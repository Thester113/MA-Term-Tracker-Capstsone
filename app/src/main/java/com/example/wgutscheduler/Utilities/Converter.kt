package com.example.wgutscheduler.Utilities

import androidx.room.TypeConverter
import java.util.*

object Converter {
    @JvmStatic
    @TypeConverter
    fun timeStampToDate(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @JvmStatic
    @TypeConverter
    fun dateToTimeStamp(date: Date?): Long? {
        return date?.time
    }
}