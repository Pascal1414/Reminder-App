package com.pascalrieder.reminder_app.repository

import com.pascalrieder.reminder_app.dao.ReminderDao
import com.pascalrieder.reminder_app.dto.ReminderWithChecks
import com.pascalrieder.reminder_app.model.Reminder

class ReminderRepository(private val reminderDao: ReminderDao) {

    suspend fun getAll(): List<Reminder> {
        return reminderDao.getAll().toReminders()
    }

    suspend fun getById(id: Long): Reminder? {
        return reminderDao.getById(id)?.toReminder()
    }

    suspend fun create(reminder: Reminder): Reminder? {
        val id = reminderDao.create(reminder)
        return reminderDao.getById(id)?.toReminder()
    }

    suspend fun delete(reminder: Reminder) {
        reminderDao.delete(reminder)
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
