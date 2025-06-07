package com.pascalrieder.reminder_app.converter

import androidx.room.TypeConverter
import java.time.DayOfWeek

class DayOfWeekConverter {
    @TypeConverter
    fun fromDayOfWeek(dayOfWeek: DayOfWeek?): Int? {
        return dayOfWeek?.value
    }

    @TypeConverter
    fun toDayOfWeek(value: Int?): DayOfWeek? {
        return value?.let { DayOfWeek.of(it) }
    }
}