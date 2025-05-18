package com.pascalrieder.todotracker.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pascalrieder.todotracker.dto.ReminderWithChecks
import com.pascalrieder.todotracker.model.Reminder

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminder /*inner join remindercheck on reminder.id = remindercheck.reminder_id*/")
    suspend fun getAll(): List<ReminderWithChecks>

    @Insert
    suspend fun create(reminder: Reminder) : Long

    companion object {
        fun List<ReminderWithChecks>.toReminders(): List<Reminder> {
            return this.map {
                Reminder(
                    id = it.reminder.id,
                    name = it.reminder.name,
                    description = it.reminder.description
                ).apply {
                    reminderChecks = it.reminderChecks
                }
            }
        }
    }
}