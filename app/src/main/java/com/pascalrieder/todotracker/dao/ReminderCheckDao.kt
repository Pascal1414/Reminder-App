package com.pascalrieder.todotracker.dao

import androidx.room.Dao
import androidx.room.Insert
import com.pascalrieder.todotracker.model.ReminderCheck

@Dao
interface ReminderCheckDao {

    @Insert
    suspend fun create(reminderCheck: ReminderCheck)
}