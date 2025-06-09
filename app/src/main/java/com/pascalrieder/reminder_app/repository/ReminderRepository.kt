package com.pascalrieder.reminder_app.repository

import com.pascalrieder.reminder_app.dao.ReminderDao
import com.pascalrieder.reminder_app.dto.ReminderWithChecks
import com.pascalrieder.reminder_app.model.Reminder

class ReminderRepository(private val userDao: ReminderDao) {

    suspend fun getAll(): List<Reminder> {
        return userDao.getAll().toReminders()
    }

    suspend fun getById(id: Long): Reminder? {
        return userDao.getById(id)?.toReminder()
    }

    suspend fun create(reminder: Reminder): Long {
        return userDao.create(reminder)
    }

    suspend fun delete(reminder: Reminder) {
        userDao.delete(reminder)
    }

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
