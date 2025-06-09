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
}