package com.pascalrieder.todotracker.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.pascalrieder.todotracker.model.Reminder
import com.pascalrieder.todotracker.model.ReminderCheck

data class ReminderWithChecks(
    @Embedded val reminder: Reminder,
    @Relation(
        parentColumn = "id",
        entityColumn = "reminder_id"
    )
    val reminderChecks: List<ReminderCheck>
)