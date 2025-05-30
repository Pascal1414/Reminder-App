package com.pascalrieder.reminder_app.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.pascalrieder.reminder_app.dto.ReminderWithChecks
import com.pascalrieder.reminder_app.model.Reminder

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminder")
    suspend fun getAll(): List<ReminderWithChecks>

    @Query("SELECT * FROM reminder WHERE id = :id")
    suspend fun getById(id: Long): ReminderWithChecks?

    @Insert
    suspend fun create(reminder: Reminder): Long

    @Delete
    suspend fun delete(reminder: Reminder)

    companion object {
        fun List<ReminderWithChecks>.toReminders(): List<Reminder> {
            return this.map {
                Reminder(
                    id = it.reminder.id,
                    name = it.reminder.name,
                    description = it.reminder.description,
                    interval = it.reminder.interval,
                    weekday = it.reminder.weekday,
                    time = it.reminder.time
                ).apply {
                    reminderChecks = it.reminderChecks
                }
            }
        }

        fun ReminderWithChecks.toReminder(): Reminder {
            return Reminder(
                id = reminder.id,
                name = reminder.name,
                description = reminder.description,
                interval = reminder.interval,
                weekday = reminder.weekday,
                time = reminder.time
            ).also { reminder ->
                reminder.reminderChecks = reminderChecks
            }
        }
    }
}