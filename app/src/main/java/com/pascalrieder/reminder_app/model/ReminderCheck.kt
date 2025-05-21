package com.pascalrieder.reminder_app.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Reminder::class,
            parentColumns = ["id"],
            childColumns = ["reminder_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("reminder_id")]
)
data class ReminderCheck(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "done") val done: Boolean,
    @ColumnInfo(name = "datetime") val dateTime: LocalDateTime,
    @ColumnInfo(name = "reminder_id") val reminderId: Long
)