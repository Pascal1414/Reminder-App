package com.pascalrieder.todotracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "interval") val interval: Interval,
    @ColumnInfo(name = "weekday") val weekday: Weekday? = null,
    @ColumnInfo(name = "time") val time: LocalTime,
) {
    @Ignore
    var reminderChecks: List<ReminderCheck> = emptyList()
}