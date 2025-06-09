package com.pascalrieder.reminder_app.repository

import com.pascalrieder.reminder_app.dao.ReminderCheckDao
import com.pascalrieder.reminder_app.model.ReminderCheck

class ReminderCheckRepository(
    private val dao: ReminderCheckDao
) {
    suspend fun create(reminderCheck: ReminderCheck) {
        dao.create(reminderCheck)
    }
}