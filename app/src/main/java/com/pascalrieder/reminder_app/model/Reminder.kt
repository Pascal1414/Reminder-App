package com.pascalrieder.reminder_app.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.IsoFields
import java.time.temporal.TemporalAdjusters
import java.util.Calendar

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "interval") val interval: Interval,
    @ColumnInfo(name = "weekday") val weekday: DayOfWeek? = null,
    @ColumnInfo(name = "time") val time: LocalTime,
) {
    @Ignore
    var reminderChecks: MutableList<ReminderCheck> = mutableListOf()

    fun isDone(): Boolean {
        val latestCheck = reminderChecks.maxByOrNull { check: ReminderCheck -> check.dateTime }

        if (latestCheck == null) {
            return false
        }

        if (interval == Interval.Daily) {
            // Check if latestCheck was today
            val currentDate = LocalDate.now()
            val checkDate = latestCheck.dateTime.toLocalDate()
            return if (currentDate != checkDate) {
                false
            } else {
                latestCheck.done
            }
        } else if (interval == Interval.Weekly && weekday != null) {
            // Todo
            return false
            /*
            val today = LocalDate.now()
            val checkDate = latestCheck.dateTime.toLocalDate()

            return latestCheck.done && !checkDate.isBefore(lastDueDate)
            */
        } else {
           return false
        }
    }

    fun getFormattedInterval(): String {
        return if (weekday == null)
            "$interval at $time"
        else
            "Every $weekday at $time"
    }
}