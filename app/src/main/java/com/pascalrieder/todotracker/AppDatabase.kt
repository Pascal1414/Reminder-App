package com.pascalrieder.todotracker

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pascalrieder.todotracker.converter.Converters
import com.pascalrieder.todotracker.dao.ReminderCheckDao
import com.pascalrieder.todotracker.dao.ReminderDao
import com.pascalrieder.todotracker.model.Reminder
import com.pascalrieder.todotracker.model.ReminderCheck

@Database(entities = [Reminder::class, ReminderCheck::class], version = 3)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
    abstract fun reminderCheckDao(): ReminderCheckDao
}