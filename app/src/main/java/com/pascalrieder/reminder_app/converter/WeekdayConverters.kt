package com.pascalrieder.reminder_app.converter

import androidx.room.TypeConverter
import com.pascalrieder.reminder_app.model.Weekday

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