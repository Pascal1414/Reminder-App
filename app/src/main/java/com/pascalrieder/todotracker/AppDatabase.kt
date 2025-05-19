package com.pascalrieder.todotracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pascalrieder.todotracker.converter.IntervalConverters
import com.pascalrieder.todotracker.converter.LocalDateTimeConverters
import com.pascalrieder.todotracker.converter.LocalTimeConverter
import com.pascalrieder.todotracker.converter.WeekdayConverters
import com.pascalrieder.todotracker.dao.ReminderCheckDao
import com.pascalrieder.todotracker.dao.ReminderDao
import com.pascalrieder.todotracker.model.Reminder
import com.pascalrieder.todotracker.model.ReminderCheck

@Database(entities = [Reminder::class, ReminderCheck::class], version = 5)
@TypeConverters(
    LocalDateTimeConverters::class,
    IntervalConverters::class,
    WeekdayConverters::class,
    LocalTimeConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        fun getInstance(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java, "database-name"
            )
                .fallbackToDestructiveMigration(true)
                .build()
        }
    }

    abstract fun reminderDao(): ReminderDao
    abstract fun reminderCheckDao(): ReminderCheckDao
}