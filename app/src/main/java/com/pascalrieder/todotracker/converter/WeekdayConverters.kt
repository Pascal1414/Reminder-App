package com.pascalrieder.todotracker.converter

import androidx.room.TypeConverter
import com.pascalrieder.todotracker.model.Weekday
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WeekdayConverters {
    @TypeConverter
    fun fromWeekday(weekday: Weekday): String {
        return weekday.toString()
    }

    @TypeConverter
    fun toWeekday(value: String): Weekday {
        return Weekday.valueOf(value)
    }
}