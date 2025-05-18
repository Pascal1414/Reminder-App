package com.pascalrieder.todotracker.converter

import androidx.room.TypeConverter
import com.pascalrieder.todotracker.model.Interval

class IntervalConverters {
    @TypeConverter
    fun fromInterval(interval: Interval): String {
        return interval.toString()
    }

    @TypeConverter
    fun toInterval(value: String): Interval {
        return Interval.valueOf(value)
    }
}