package com.pascalrieder.reminder_app.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.pascalrieder.reminder_app.model.Reminder
import com.pascalrieder.reminder_app.model.ReminderCheck

data class ReminderWithChecks(
    @Embedded val reminder: Reminder,
    @Relation(
        parentColumn = "id",
        entityColumn = "reminder_id"
    )
    val reminderChecks: MutableList<ReminderCheck>
)