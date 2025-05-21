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
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "shownAt") val shownAt: LocalDateTime,
    @ColumnInfo(name = "reminder_id") val reminderId: Long,
    @ColumnInfo(name = "wasAlreadyDone") val wasAlreadyDone: Boolean = false,
)