package com.pascalrieder.reminder_app.dao

import androidx.room.Dao
import androidx.room.Insert
import com.pascalrieder.reminder_app.model.ReminderCheck

@Dao
interface ReminderCheckDao {

    @Insert
    suspend fun create(reminderCheck: ReminderCheck)
}